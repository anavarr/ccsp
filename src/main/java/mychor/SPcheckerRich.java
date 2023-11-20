package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;


public class SPcheckerRich extends SPparserRichBaseVisitor<List<String>>{

    //a list of recursive variables
    public Set<String> recVars = new HashSet<>();
    static String ERROR_RECVAR_ADD(String key, String boundProcess, String newProcess){
        return String.format(
                "Procedure %s is already bound to process %s, can't bind it to %s",
                key,
                boundProcess,
                newProcess);
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

    public static class Context{
        public String currentRecVar;
        //the current process studied
        public String currentProcess;
        //a set of processes
        public Set<String> processes = new HashSet<>();
        //a list of communications
        public ArrayList<Session> sessions = new ArrayList<>();
        //maps a recursive variable to a process
        public HashMap<String, String> recvar2proc = new HashMap<>();
        //a list of errors
        public List<String> errors = new ArrayList<>();
    }

    //sessions and errors are not duplicated
    Context duplicateContextSessionLessErrorLess(Context ctx){
        var c = new Context();
        c.currentProcess = ctx.currentProcess;
        c.currentRecVar = ctx.currentRecVar;
        c.processes.addAll(ctx.processes);
        c.recvar2proc.putAll(ctx.recvar2proc);
        return c;
    }

    public Context compilerCtx = new Context();

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

    private Context mergeContexts(Context superCtx, Context context,
                                  BiFunction<ArrayList<Session>, ArrayList<Session>, ArrayList<Session>> sessionMerger){
        superCtx.errors.addAll(context.errors);
        superCtx.sessions = sessionMerger.apply(superCtx.sessions, context.sessions);
        for (String key : context.recvar2proc.keySet()){
            if (superCtx.recvar2proc.containsKey(key)){
                var superValue = superCtx.recvar2proc.get(key);
                var value = context.recvar2proc.get(key);
                if(!superValue.equals(value)){
                    superCtx.errors.add(ERROR_RECVAR_ADD(key, superValue, value));
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
                    //if both session (left and right) are the same then we don't need to have two different paths
                    if (!session1.isEqual(session2)) {
                        session1.expandCommunicationRoots(session2.communicationsRoots());
                    }else{
                        session1.expandLeafCommunicationRoots(session2.communicationsRoots());
                    }
                    sessionsToAdd.remove(session2);
                }
            }
        }
        sessions1.addAll(sessionsToAdd);
        return sessions1;
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
    public List<String> visitRecdef(SPparserRich.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        var recVar = ctx.getChild(0).getText();
        recVars.add(recVar);
        var superContext= compilerCtx;

        var currentProcess = compilerCtx.recvar2proc.get(ctx.getChild(0).getText());
        compilerCtx = new Context();
        compilerCtx.currentProcess = currentProcess;
        compilerCtx.currentRecVar = recVar;
        var errors = (ctx.getChild(2).accept(this));
        compilerCtx = mergeContexts(superContext, compilerCtx, this::mergeSessionsVertical);
        compilerCtx.currentProcess = null;
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
        for(int i =0; i < children -1; i+=5){
            var proc = ctx.getChild(i).getText();
            compilerCtx.currentProcess = proc;
            compilerCtx.processes.add(proc);
            compilerCtx.errors.addAll(ctx.getChild(i +2).accept(this));
        }
        compilerCtx.currentProcess = null;
        return null;
    }

    @Override
    public List<String> visitNon(SPparserRich.NonContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<String> visitSom(SPparserRich.SomContext ctx) {
        return ctx.getChild(2).accept(this);
    }

    @Override
    public List<String> visitCdt(SPparserRich.CdtContext ctx) {
        // 0: If
        // 1: expr
        // 2: Then
        // 3: behaviour
        // 4: Else
        // 5: behaviour
        var oldContext = compilerCtx;

        compilerCtx = duplicateContextSessionLessErrorLess(oldContext);
        compilerCtx.errors = ctx.getChild(3).accept(this);
        var contextThen = compilerCtx;

        compilerCtx = duplicateContextSessionLessErrorLess(oldContext);
        compilerCtx.errors = ctx.getChild(5).accept(this);
        var contextElse = compilerCtx;

        //we merge the "horizontal contexts to create one context corresponding to the conditional
        var mergedContext = mergeContexts(contextThen, contextElse, this::mergeSessionsHorizontal);
        var errors = mergedContext.errors;
        //we merge it
        compilerCtx = mergeContexts(oldContext, mergedContext, this::mergeSessionsVertical);

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

    public <T extends SPparserRich.BehaviourContext> List<String> visitComm(T ctx, Communication communication, int continuationIndex){
        // 0: dest
        // 1: !/?
        // 2: expr
        // 3: @
        // 4: !/?
        // 5: ann
        // 6: seq
        // 7: behaviour
        var errors = new ArrayList<String>();
        if(compilerCtx.currentProcess == null){
            errors.add("Current Process can't be null in a communication");
        }else{
            var dest = ctx.getChild(0).getText();
            var source = compilerCtx.currentProcess;
            var session = getSession(source, dest);
            if(session == null){
                compilerCtx.sessions.add(new Session(source, dest, communication));
            }else{
                session.addLeafCommunicationRoot(communication);
            }
        }
        errors.addAll(ctx.getChild(continuationIndex).accept(this));
        return errors;
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
        var contexts = new ArrayList<Context>();
        for(int i=5; i< ctx.getChildCount();i+=6){
            compilerCtx = duplicateContextSessionLessErrorLess(oldContext);
            visitComm(ctx, new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE, ctx.getChild(i-2).getText()), i);
            contexts.add(compilerCtx);
        }
        //merge all "horizontal" contexts together (pretty much merge their sessions and errors and stuff
        compilerCtx = contexts.stream().reduce(
                new Context(),
                (context1, context2) -> mergeContexts(context1, context2, this::mergeSessionsHorizontal));
        var errors = compilerCtx.errors;
        //we merge it with the previous context
        compilerCtx = mergeContexts(oldContext, compilerCtx, this::mergeSessionsVertical);
        return errors;
    }


    @Override
    public List<String> visitCal(SPparserRich.CalContext ctx) {
        // 0 : Call
        // 1 : name
        var process_name = compilerCtx.currentProcess;
        var varName = ctx.getChild(1).getText();
        var el = new ArrayList<String>();
        if (compilerCtx.recvar2proc.containsKey(varName)){
            if  (!compilerCtx.recvar2proc.get(varName).equals(process_name)){
                el.add(ERROR_RECVAR_ADD(varName, compilerCtx.recvar2proc.get(varName), process_name));
            }
        }else{
            compilerCtx.recvar2proc.put(varName, process_name);
        }
        return el;
    }

    @Override
    public List<String> visitEnd(SPparserRich.EndContext ctx) {
        return new ArrayList<>();
    }
}
