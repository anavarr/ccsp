package mychor;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

import static mychor.Utils.ERROR_NULL_PROCESS;
import static mychor.Utils.ERROR_RECVAR_ADD;


public class SPcheckerRich extends SPparserRichBaseVisitor<List<String>>{
    //a list of recursive variables
    public Set<String> recVars = new HashSet<>();
    public HashMap<String, ParseTree> recDefs = new HashMap<>();
    public CompilerContext compilerCtx = new CompilerContext();

    public boolean sessionsBranchingAreValid(){
        return compilerCtx.sessions.stream().allMatch(Session::isBranchingValid);
    }
    public void displayComplementarySessions() {
        var nonComplementarySessions = new ArrayList<>(compilerCtx.sessions);
        System.out.println("These sessions are complementary :");
        for (Session session : compilerCtx.sessions) {
            for (Session session1 : compilerCtx.sessions) {
                if(session.areEnds(session1.peerA(), session1.peerB())  && session1 != session){
                    if (session.isComplementary(session1)){
                        System.out.printf("\t%s <-> %s and %s <-> %s\n",
                                session.peerA(), session.peerB(), session1.peerA(), session1.peerB());
                        nonComplementarySessions.remove(session);
                        nonComplementarySessions.remove(session1);
                    }
                }
            }
        }
        System.out.println("These sessions are not complementary : ");
        for (Session nonComplementarySession : nonComplementarySessions) {
            System.out.printf("\t%s <-> %s", nonComplementarySession.peerA(), nonComplementarySession.peerB());
        }
    }
    public ArrayList<Session> getNonComplementarySessions() {
        var nonComplementarySessions = new ArrayList<>(compilerCtx.sessions);
        for (Session session : compilerCtx.sessions) {
            for (Session session1 : compilerCtx.sessions) {
                if(session.areEnds(session1.peerA(), session1.peerB())  && session1 != session){
                    if (session.isComplementary(session1)){
                        nonComplementarySessions.remove(session);
                        nonComplementarySessions.remove(session1);
                    }
                }
            }
        }
        return nonComplementarySessions;
    }
    //sessions and errors are not duplicated
    CompilerContext duplicateContextSessionLessErrorLess(CompilerContext ctx){
        var c = new CompilerContext();
        c.currentProcess = ctx.currentProcess;
        c.currentRecVar = ctx.currentRecVar;
        c.processes.addAll(ctx.processes);
        c.recvar2proc.putAll(ctx.recvar2proc);
        c.calledProceduresStacks = ctx.calledProceduresStacks;
        return c;
    }
    public void displayContext() {
        System.out.println("Sessions:");
        for (Session session : compilerCtx.sessions) {
            System.out.println("\t"+session.peerA()+" <-> "+session.peerB());
            session.displayCommunications("\t");
        }
        System.out.println("RecVar to Process mapping:");
        System.out.println("\t"+ compilerCtx.recvar2proc);
        System.out.println("Errors:");
        System.out.println("\t"+ compilerCtx.errors);
        System.out.println("Called procedures stack:");
        for (String s : compilerCtx.calledProceduresStacks.keySet()) {
            System.out.println("\t"+s+" : "+compilerCtx.calledProceduresStacks.get(s));
        }
    }
    // check that no process sends or receive a message to or from itself
    public boolean noSelfCom() {
        for (Session session : compilerCtx.sessions) {
            if(session.isSelfComm()){
                return false;
            }
        }
        return true;
    }
    // check that all the processes mentioned are defined in the network
    public List<String> unknownProcesses(){
        return compilerCtx.sessions.stream().map(Session::peerB)
                .filter(it -> !compilerCtx.processes.contains(it)).toList();
    }
    // check that all the recursive variables called in behaviours are defined
    public List<String> unknownVariables(){
        return compilerCtx.recvar2proc.keySet().stream()
                .filter(vari -> !recVars.contains(vari)).toList();
    }
    private CompilerContext mergeContexts(CompilerContext superCtx, CompilerContext context,
                                  BiFunction<ArrayList<Session>, ArrayList<Session>, ArrayList<Session>> sessionMerger,
                                  ParserRuleContext ctx){
        superCtx.errors.addAll(context.errors);
        superCtx.sessions = sessionMerger.apply(superCtx.sessions, context.sessions);
        for (String key : context.recvar2proc.keySet()){
            if (superCtx.recvar2proc.containsKey(key)){
                var superValue = superCtx.recvar2proc.get(key);
                var value = context.recvar2proc.get(key);
                if(!superValue.equals(value)){
                    superCtx.errors.add(ERROR_RECVAR_ADD(key, superValue, value, ctx));
                }
            }
        }
        return superCtx;
    }
    private ArrayList<Session> mergeSessionsVertical(ArrayList<Session> sessionsSuper, ArrayList<Session> sessions){
        var sessionsToAdd = new ArrayList<>(sessions);
        for (Session sessionSuper : sessionsSuper) {
            for (Session session : sessions) {
                if(sessionSuper.hasSameEnds(session)){
                    sessionSuper.addLeafCommunicationRoots(session.communicationsRoots());
                    sessionsToAdd.remove(session);
                }
            }
        }
        sessionsSuper.addAll(sessionsToAdd);
        return sessionsSuper;
    }
    private ArrayList<Session> mergeSessionsHorizontal(ArrayList<Session> sessions1, ArrayList<Session> sessions2){
        var sessionsToAdd = new ArrayList<>(sessions2);
        for (Session session1 : sessions1) {
            for (Session session2 : sessions2) {
                if(session1.hasSameEnds(session2)){
                    session1.expandTopCommunicationRoots(session2.communicationsRoots());
                    sessionsToAdd.remove(session2);
                }
            }
        }
        sessions1.addAll(sessionsToAdd);
        return sessions1;
    }
    private ArrayList<Session> mergeSessionsHorizontalStub(ArrayList<Session> sessions1, ArrayList<Session> sessions2){
        var leftOnly = sessions1.stream()
                .filter(s1 -> sessions2.stream().noneMatch(s2 -> s2.hasSameEnds(s1)));
        var rightOnly = sessions2.stream()
                .filter(s2 -> sessions1.stream().noneMatch(s1 -> s1.hasSameEnds(s2)));
        var common = new ArrayList<>(sessions1.stream()
                .filter(s1 -> sessions2.stream().anyMatch(s2 -> s2.hasSameEnds(s1)))
                .map(s1 -> {
                    var toAdd = sessions2.stream().filter(s2 -> s2.hasSameEnds(s1)).findFirst().get();
                    s1.expandTopCommunicationRoots(toAdd.communicationsRoots());
                    return s1;
                })
                .toList());

        common.addAll(new ArrayList<>(leftOnly.map(s -> {
            s.expandTopCommunicationRoots(
                    new ArrayList<>(
                            List.of(
                                    new Communication(
                                            Utils.Direction.VOID,
                                            Utils.Arity.SINGLE,
                                            new ArrayList<>(),
                                            null)
                            )
                    )
            );
            return s;
        }).toList()));
        common.addAll(new ArrayList<>(rightOnly.map(s -> {
            s.expandTopCommunicationRoots(
                    new ArrayList<>(
                            List.of(
                                    new Communication(
                                            Utils.Direction.VOID,
                                            Utils.Arity.SINGLE,
                                            new ArrayList<>(),
                                            null)
                            )
                    )
            );
            return s;
        }).toList()));
        return common;
    }
    private Session getSession(String source, String dest){
        var session = compilerCtx.sessions.stream()
                .filter(s -> s.peerA().equals(source) && s.peerB().equals(dest))
                .toList();
        if(session.size() == 0){
            return null;
        }else{
            return session.get(0);
        }
    }

    @Override
    public List<String> visitProgram(SPparserRich.ProgramContext ctx) {
        //0 : network
        //1+ : recdef
        //n : EOF
        var errors = new ArrayList<String>();
        for (int i = 1; i < ctx.getChildCount()-1; i++) {
            System.out.println(ctx.getChild(i));
            var recVar = ctx.getChild(i).getChild(0).getText();
            var recBehaviour = ctx.getChild(i).getChild(2);
            recDefs.put(recVar, recBehaviour);
//            errors.addAll(ctx.getChild(i).accept(this));
        }
        errors.addAll(ctx.getChild(0).accept(this));
        System.out.println(errors);
        return errors;
    }
    @Override
    public List<String> visitNetwork(SPparserRich.NetworkContext ctx) {
        // 0 : first process name
        // 1 : [
        // 2 : behaviour
        // 3 : ]
        // 4 : |
        // 5 : second process name
        var children = ctx.getChildCount();
        var errors = new ArrayList<String>();
        for(int i =0; i < children -1; i+=5){
            var proc = ctx.getChild(i).getText();
            compilerCtx.currentProcess = proc;
            compilerCtx.processes.add(proc);
            errors.addAll(ctx.getChild(i +2).accept(this));
        }
        compilerCtx.currentProcess = null;
        return errors;
    }
    @Override
    public List<String> visitRecdef(SPparserRich.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        System.out.println("visiting Recdef");
        var recVar = ctx.getChild(0).getText();
        recVars.add(recVar);
        var superContext= compilerCtx;
        var currentProcess = compilerCtx.recvar2proc.get(ctx.getChild(0).getText());

        //the procedure has already been defined in the Network
        compilerCtx = duplicateContextSessionLessErrorLess(superContext);
        compilerCtx.currentProcess = currentProcess;
        compilerCtx.currentRecVar = recVar;
        var errors = (ctx.getChild(2).accept(this));
        superContext.errors.addAll(errors);
        compilerCtx = mergeContexts(superContext, compilerCtx, this::mergeSessionsVertical, ctx);
        compilerCtx.currentProcess = null;
        return errors;
    }
    @Override
    public List<String> visitCal(SPparserRich.CalContext ctx) {
        // 0 : Call
        // 1 : name
        var varName = ctx.getChild(1).getText();
        var errors = new ArrayList<String>();
        if(!recDefs.containsKey(varName)) {
            errors.add(String.format("No recursive variable with the name %s exists", varName));
            return errors;
        }
        if (compilerCtx.calledProceduresStacks.containsKey(compilerCtx.currentProcess)){
            //this process exists and has already called a method
            if(compilerCtx.calledProceduresStacks.get(compilerCtx.currentProcess).contains(varName)){
                System.err.println("we are looping");
                compilerCtx.calledProceduresStacks.get(compilerCtx.currentProcess).push(varName);
                return errors;
            }
        }else{
            compilerCtx.calledProceduresStacks.put(compilerCtx.currentProcess, new Stack<>());
        }
        compilerCtx.calledProceduresStacks.get(compilerCtx.currentProcess).push(varName);
        errors.addAll(recDefs.get(varName).accept(this));
//        compilerCtx.calledProceduresStacks.get(compilerCtx.currentProcess).push(varName);
//        if (compilerCtx.recvar2proc.containsKey(varName)){
//            if  (!compilerCtx.recvar2proc.get(varName).equals(compilerCtx.currentProcess)){
//                errors.add(ERROR_RECVAR_ADD(varName, compilerCtx.recvar2proc.get(varName), compilerCtx.currentProcess, ctx));
//            }
//        }else{
//            compilerCtx.recvar2proc.put(varName, compilerCtx.currentProcess);
//        }
        return errors;
    }
    @Override
    public List<String> visitCdt(SPparserRich.CdtContext ctx) {
        // 0: 'If'
        // 1: expr
        // 2: 'Then'
        // 3: behaviour
        // 4: 'Else'
        // 5: behaviour
        var oldContext = compilerCtx;

        compilerCtx = duplicateContextSessionLessErrorLess(oldContext);
        compilerCtx.errors = ctx.getChild(3).accept(this);
        var contextThen = compilerCtx;

        compilerCtx = duplicateContextSessionLessErrorLess(oldContext);
        compilerCtx.errors = ctx.getChild(5).accept(this);
        var contextElse = compilerCtx;
        //we merge the "horizontal contexts to create one context corresponding to the conditional
        var mergedContext = mergeContexts(contextThen, contextElse,
                this::mergeSessionsHorizontalStub, ctx);
        var errors = mergedContext.errors;
        //we merge it
        compilerCtx = mergeContexts(oldContext, mergedContext, this::mergeSessionsVertical, ctx);
        System.out.println(compilerCtx.sessions);
        // we get the two contexts, we need to check that they have the same number of SELECT or BRANCHES
        return errors;
    }
    @Override
    public List<String> visitSnd(SPparserRich.SndContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE), 7);
    }
    @Override
    public List<String> visitRcv(SPparserRich.RcvContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE), 7);
    }
    @Override
    public List<String> visitSel(SPparserRich.SelContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.SELECT, Utils.Arity.SINGLE, ctx.getChild(2).getText()), 7);
    }
    @Override
    public List<String> visitBra(SPparserRich.BraContext ctx) {
        // proc '&' '{' BLABEL ':' mBehaviour '}'  ('//'  '{' BLABEL ':' mBehaviour '}')+
        // 0: dest
        // 1: &
        // 2: {
        // 3: BLABEL
        // 4: :
        // 5: mBe
        // 6: }
        var oldContext = compilerCtx;
        var contexts = new ArrayList<CompilerContext>();
        var errors = new ArrayList<String>();
        for(int i=5; i< ctx.getChildCount();i+=6){
            compilerCtx = duplicateContextSessionLessErrorLess(oldContext);
            errors.addAll(visitComm(ctx, new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE, ctx.getChild(i-2).getText()), i));
            contexts.add(compilerCtx);
        }
        //merge all "horizontal" contexts together (pretty much merge their sessions and errors and stuff
        compilerCtx = contexts.stream().reduce(
                new CompilerContext(),
                (context1, context2) -> mergeContexts(context1, context2, this::mergeSessionsHorizontal, ctx));
        //we merge it with the previous context
        compilerCtx = mergeContexts(oldContext, compilerCtx, this::mergeSessionsVertical, ctx);
        return errors;
    }
    @Override
    public List<String> visitNon(SPparserRich.NonContext ctx) {
        return new ArrayList<>();
    }
    @Override
    public List<String> visitSom(SPparserRich.SomContext ctx) {
        return ctx.getChild(2).accept(this);
    }
    public <T extends SPparserRich.BehaviourContext> List<String> visitComm(T ctx, Communication communication, int continuationIndex){
        var errors = new ArrayList<String>();
        if(compilerCtx.currentProcess == null){
            errors.add(ERROR_NULL_PROCESS(ctx));
        }else{
            var dest = ctx.getChild(0).getText();
            var source = compilerCtx.currentProcess;
            var session = getSession(source, dest);
            if(session == null){
                compilerCtx.sessions.add(new Session(source, dest, communication));
            }else{
                session.addLeafCommunicationRoots(new ArrayList<>(List.of(communication)));
            }
        }
        errors.addAll(ctx.getChild(continuationIndex).accept(this));
        return errors;
    }
    @Override
    public List<String> visitEnd(SPparserRich.EndContext ctx) {
        return new ArrayList<>();
    }
}
