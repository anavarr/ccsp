package mychor;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static mychor.Utils.ERROR_NULL_PROCESS;
import static mychor.Utils.ERROR_RECVAR_ADD;
import static mychor.Utils.ERROR_RECVAR_UNKNOWN;


public class SPcheckerRich extends SPparserRichBaseVisitor<List<String>>{
    //a list of recursive variables
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

    public void displayContext() {
        System.out.println("Sessions:");
        for (Session session : compilerCtx.sessions) {
            System.out.println("\t"+session.peerA()+" <-> "+session.peerB());
            System.out.println(session);
        }
        System.out.println("RecVar to Process mapping:");
        System.out.println("\t"+ compilerCtx.recvar2proc);
        System.out.println("Errors:");
        System.out.println("\t"+ compilerCtx.errors);
        System.out.println("Called procedures stack:");
        for (String s : compilerCtx.calledProceduresGraph.keySet()) {
            System.out.println("\t"+s+" : "+compilerCtx.calledProceduresGraph.get(s));
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
                .filter(vari -> !recDefs.containsKey(vari)).toList();
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
            var recVar = ctx.getChild(i).getChild(0).getText();
            var recBehaviour = ctx.getChild(i).getChild(2);
            recDefs.put(recVar, recBehaviour);
//            errors.addAll(ctx.getChild(i).accept(this));
        }
        compilerCtx.errors.addAll(ctx.getChild(0).accept(this));
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
            compilerCtx.errors.addAll(ctx.getChild(i +2).accept(this));
        }
        compilerCtx.currentProcess = null;
        return errors;
    }
    @Override
    public List<String> visitCal(SPparserRich.CalContext ctx) {
        // 0 : Call
        // 1 : name
        var varName = ctx.getChild(1).getText();
        var errors = new ArrayList<String>();

        //we check if the variable is already mapped
        if (compilerCtx.recvar2proc.containsKey(varName)){
            //if it is it can't be mapped to another process
            if  (!compilerCtx.recvar2proc.get(varName).equals(compilerCtx.currentProcess)){
                errors.add(ERROR_RECVAR_ADD(varName, compilerCtx.recvar2proc.get(varName), compilerCtx.currentProcess, ctx));
            }
        }else{
            //else we simply map it
            compilerCtx.recvar2proc.put(varName, compilerCtx.currentProcess);
        }

        //if the variable is not in the set of recursive variable definitions we add an error and return
        if(!recDefs.containsKey(varName)) {
            errors.add(ERROR_RECVAR_UNKNOWN(varName, ctx));
            compilerCtx.errors.add(ERROR_RECVAR_UNKNOWN(varName, ctx));
            return errors;
        }

        //we handle recursion here
        var callGraph = compilerCtx.calledProceduresGraph.get(compilerCtx.currentProcess);
        if (callGraph != null){
            //this process exists and has already called a method
            if(callGraph.isVarNameInGraph(varName)){
                System.err.println("we are looping");
                callGraph.addLeafFrame(new StackFrame(varName));
                return errors;
            }
        }else{
            compilerCtx.calledProceduresGraph.put(compilerCtx.currentProcess, new ProceduresCallGraph(new ArrayList<>(List.of(
                    new StackFrame(varName)
            ))));
        }
        //we visit the code of the recursive definition
        errors.addAll(recDefs.get(varName).accept(this));
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

        compilerCtx = oldContext.duplicateContext();
        compilerCtx.errors = ctx.getChild(3).accept(this);
        var contextThen = compilerCtx;

        compilerCtx = oldContext.duplicateContext();
        compilerCtx.errors = ctx.getChild(5).accept(this);
        var contextElse = compilerCtx;
        //we merge the "horizontal contexts to create one context corresponding to the conditional
        var mergedContext = CompilerContext.mergeContexts(contextThen, contextElse,
                Session::mergeSessionsHorizontalStub, ProceduresCallGraphMap::mergeCalledProceduresHorizontal, ctx);
        var errors = mergedContext.errors;
        //we merge it
        compilerCtx = CompilerContext.mergeContexts(oldContext, mergedContext, Session::mergeSessionsVertical,
                ProceduresCallGraphMap::mergeCalledProceduresVertical, ctx);
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
            compilerCtx = oldContext.duplicateContext();
            errors.addAll(visitComm(ctx, new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE, ctx.getChild(i-2).getText()), i));
            contexts.add(compilerCtx);
        }
        //merge all "horizontal" contexts together (pretty much merge their sessions and errors and stuff
        compilerCtx = contexts.stream().reduce(
                new CompilerContext(),
                (context1, context2) -> CompilerContext.mergeContexts(context1, context2, Session::mergeSessionsHorizontal,
                        ProceduresCallGraphMap::mergeCalledProceduresHorizontal, ctx));
        //we merge it with the previous context
        compilerCtx = CompilerContext.mergeContexts(oldContext, compilerCtx, Session::mergeSessionsVertical,
                ProceduresCallGraphMap::mergeCalledProceduresVertical, ctx);
        //now we check for loops
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
