package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    public static class Context{
        public String currentRecVar;
        //the current process studied
        public String currentProcess;
        //a set of processes
        public Set<String> processes = new HashSet<>();
        //a list of communications
        public List<Session> sessions = new ArrayList<>();
        //maps a recursive variable to a process
        public HashMap<String, String> recvar2proc = new HashMap<>();
        //a list of errors
        public List<String> errors = new ArrayList<>();

        public List<Context> childContext;
    }
    public Context compilerCtx = new Context();

    public void displayContext() {
        System.out.println("Sessions:");
        for (Session session : compilerCtx.sessions) {
            System.out.println("\t"+session.peerA()+" <-> "+session.peerB());
            for (Communication communication : session.communications()) {
                System.out.println("\t\t"+ communication);
            }
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

    private Context mergeContexts(Context superCtx, Context context){
        superCtx.errors.addAll(context.errors);
        superCtx.sessions.addAll(context.sessions);
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

    @Override
    public List<String> visitRecdef(SPparserRich.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        recVars.add(ctx.getChild(0).getText());
        var superContext= compilerCtx;
        var currentProcess = compilerCtx.recvar2proc.get(ctx.getChild(0).getText());
        compilerCtx = new Context();
        compilerCtx.currentProcess = currentProcess;
        var errors = (ctx.getChild(2).accept(this));
        compilerCtx.currentProcess = null;
        compilerCtx = mergeContexts(superContext, compilerCtx);
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
        compilerCtx = new Context();
        ctx.getChild(3).accept(this);
        var contextThen = compilerCtx;
        compilerCtx = new Context();
        ctx.getChild(5).accept(this);
        var contextElse = compilerCtx;

        var sessionsThen = contextThen.sessions;
        var sessionsELse = contextElse.sessions;

        for (int i = 0; i < contextThen.sessions.size(); i++) {
            var communicationsThen = contextThen.sessions.get(i).communications();
            for (Communication communication : communicationsThen) {
                if (communication.isSelect() || communication.isBranch()) {
                    //must look for it in the other context
                    for (int i1 = 0; i1 < contextElse.sessions.size(); i1++) {
                        var communicationsElse = contextElse.sessions.get(i).communications();
                        for (Communication communication1 : communicationsElse) {
                            
                        }
                    }
                }
            }
        }
        // we get the two contexts, we need to check that they have the same number of SELECT or BRANCHES
        return new ArrayList<>();
    }

    @Override
    public List<String> visitSnd(SPparserRich.SndContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE));
    }
    @Override
    public List<String> visitRcv(SPparserRich.RcvContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE));
    }
    @Override
    public List<String> visitSel(SPparserRich.SelContext ctx) {
        // 0: dest
        // 1: +
        // 2: LABEL
        // 3: @
        // 4: +
        // 5: ann
        // 6: seq
        // 7: behaviour
        return visitComm(ctx, new Communication(Utils.Direction.SELECT, Utils.Arity.SINGLE));
    }

    public <T extends SPparserRich.BehaviourContext> List<String> visitComm(T ctx, Communication communication){
        // 0: dest
        // 1: !/?
        // 2: expr
        // 3: @
        // 4: !/?
        // 5: ann
        // 6: seq
        // 7: behaviour
        var dest = ctx.getChild(0).getText();
        var source = compilerCtx.currentProcess;
        System.out.println(source);
        var session = compilerCtx.sessions.stream()
                .filter(s -> s.peerA().equals(source) && s.peerB().equals(dest))
                .toList();
        if(session.size() == 0){
            var ar = new ArrayList<Communication>();
            ar.add(communication);
            compilerCtx.sessions.add(new Session(source, dest, ar));
        }else{
            session.get(0).addComm(communication);
        }
        var errors = ctx.getChild(7).accept(this);
        if(compilerCtx.currentProcess == null){
            errors.add("Current Process can't be null in a communication");
        }
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
        var procs = new ArrayList<String>();
        procs.add(ctx.getChild(0).getText());
        for(int i=5; i< ctx.getChildCount();i+=6){
            var a = ctx.getChild(i).accept(this);
            procs.addAll(a);
        }
        return procs;
    }


    @Override
    public List<String> visitCal(SPparserRich.CalContext ctx) {
        // 0 : Call
        // 1 : name
        var process_name = compilerCtx.currentProcess;
        var key = ctx.getChild(1).getText();
        var el = new ArrayList<String>();
        if (compilerCtx.recvar2proc.containsKey(key)){
            if  (!compilerCtx.recvar2proc.get(key).equals(process_name)){
                el.add(ERROR_RECVAR_ADD(key, compilerCtx.recvar2proc.get(key), process_name));
            }
            // else it is already in we just call it again
        }else{
            compilerCtx.recvar2proc.put(key, process_name);
        }
        return el;
    }

    @Override
    public List<String> visitEnd(SPparserRich.EndContext ctx) {
        return new ArrayList<>();
    }
}
