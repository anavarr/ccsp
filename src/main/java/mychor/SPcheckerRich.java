package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SPcheckerRich extends SPparserRichBaseVisitor<List<String>>{

    public static class Context{
        public String currentRecVar;
        //the current process studied
        public String currentProcess;
        //a list of recursive variables
        public Set<String> recVars = new HashSet<>();
        //a list of communications
        public List<Session> sessions = new ArrayList<>();
        //maps a recursive variable to a process
        public HashMap<String, List<String>> recvar2proc = new HashMap<>();
        //a list of errors
        public List<String> errors = new ArrayList<>();

        public Context nextContext;
    }
    public Context compilerCtx = new Context();

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
        var inNetwork = compilerCtx.sessions.stream().map(Session::peerA).toList();
        return compilerCtx.sessions.stream().map(Session::peerB).filter(it -> !inNetwork.contains(it)).toList();
    }

    // check that all the recursive variables called in behaviours are defined
    public List<String> unknownVariables(){
        return compilerCtx.recvar2proc.keySet().stream()
                .filter(vari -> !compilerCtx.recVars.contains(vari)).toList();
    }

    @Override
    public List<String> visitRecdef(SPparserRich.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        compilerCtx.recVars.add(ctx.getChild(0).getText());
//        big problem with the way recvars are handled
//        compilerCtx.currentProcess = compilerCtx.recvar2proc.get(ctx.getChild(0).getText());
        var superContext= compilerCtx;
        compilerCtx = new Context();
        compilerCtx.errors.addAll(ctx.getChild(2).accept(this));
        compilerCtx = mergeContexts(superContext, compilerCtx);
        compilerCtx.currentProcess = null;
        return super.visitRecdef(ctx);
    }

    private Context mergeContexts(Context superCtx, Context context){
        superCtx.recVars.addAll(context.recVars);
        superCtx.errors.addAll(context.errors);
        superCtx.sessions.addAll(context.sessions);
        for (String key : context.recvar2proc.keySet()){
            superCtx.recvar2proc.merge(key, context.recvar2proc.get(key), Utils::addL2ToL1);
        }
        return superCtx;
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
            compilerCtx.currentProcess = ctx.getChild(i).getText();;
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
        return super.visitCdt(ctx);
    }

    @Override
    public List<String> visitSnd(SPparserRich.SndContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE));
    }
    @Override
    public List<String> visitRcv(SPparserRich.RcvContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE));
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
        if(compilerCtx.currentProcess == null){
            System.err.println("Current Process can't be null in a communication");
            return new ArrayList<>();
        }
        System.out.println("hoho");
        var dest = ctx.getChild(0).getText();
        var source = compilerCtx.currentProcess;
        var session = compilerCtx.sessions.stream()
                .filter(s -> s.peerA().equals(source) && s.peerB().equals(dest))
                .toList();
        if(session.size() == 0){
            System.out.println("no session with same ends");
            var ar = new ArrayList<Communication>();
            ar.add(communication);
            compilerCtx.sessions.add(new Session(source, dest, ar));
        }else{
            System.out.println("a session has been saved");
            System.out.println(compilerCtx.sessions);
            session.get(0).addComm(communication);
            System.out.println(compilerCtx.sessions);
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> visitSel(SPparserRich.SelContext ctx) {
        // 0: dest
        // 1: &
        // 2: LABEL
        // 3: @
        // 4: +
        // 5: ann
        // 6: seq
        // 7: behaviour
        var procs = ctx.getChild(7).accept(this);
        procs.add(ctx.getChild(0).getText());
        return procs;
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
        var al = new ArrayList<String>();
        al.add(compilerCtx.currentProcess);
        var key = ctx.getChild(1).getText();
        compilerCtx.recvar2proc.merge(key, al, Utils::addL2ToL1);
        return new ArrayList<>();
    }

    @Override
    public List<String> visitEnd(SPparserRich.EndContext ctx) {
        return new ArrayList<>();
    }
}
