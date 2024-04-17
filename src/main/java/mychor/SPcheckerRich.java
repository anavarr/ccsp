package mychor;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mychor.Utils.ERROR_NULL_PROCESS;
import static mychor.Utils.ERROR_RECVAR_ADD;
import static mychor.Utils.ERROR_RECVAR_UNKNOWN;


public class SPcheckerRich extends SPparserRichBaseVisitor<List<String>>{
    //a list of recursive variables
    public HashMap<String, ParseTree> recDefs = new HashMap<>();
    public CompilerContext compilerCtx = new CompilerContext();

    HashMap<String,Behaviour> reduced = new HashMap<>();

    static private List<Map<String, Behaviour>> generateCombinations(List<Map<String, Behaviour>> configurationsFlat,
                                                                     Map<String, List<Behaviour>> configurationsDeep,
                                                                     Map<String, Behaviour> combination,
                                                                     int index,
                                                                     ArrayList<String> keySet,
                                                                     String key){
        if(index >= configurationsDeep.get(key).size()){
            return configurationsFlat;
        }
        combination.put(key, configurationsDeep.get(key).get(index));
        //call bottom
        var nexKeyIndex =keySet.indexOf(key)+1;
        if(nexKeyIndex < keySet.size()){
            var nextKey = keySet.get(nexKeyIndex);
            generateCombinations(configurationsFlat, configurationsDeep, combination,0, keySet, nextKey);
        }else{
            configurationsFlat.add(combination);
        }
        //call side

        return generateCombinations(configurationsFlat, configurationsDeep, new HashMap<>(combination),
                index+1, keySet, key);
    }

    static List<Map<String, Behaviour>> extractCarthesiansBranches(HashMap<String, Behaviour> reducedPaths){
        var configurationsDeep = new HashMap<String, List<Behaviour>>();
        for (String s : reducedPaths.keySet()) {
            var b = reducedPaths.get(s).getBranches();
            configurationsDeep.put(s, b);
        }
        List<Map<String, Behaviour>> configurationsFlat = new ArrayList<>();
        var keys = new ArrayList<>(configurationsDeep.keySet());
        var key = keys.get(0);
        var combi = new HashMap<String, Behaviour>();
        generateCombinations(configurationsFlat, configurationsDeep,combi, 0, keys, key);
        return configurationsFlat;
    }
    public List<Map<String, Behaviour>> getExecutionPaths(){
        return extractCarthesiansBranches(compilerCtx.behaviours);
    }
    public ArrayList<Boolean> typeSafety(){
        // [x] for a selection to be safe, selection can only use a subset of the labels used in branching
        // [~] for a communication to be safe, the payload type of send must be a subtype of the payload type of recv
        // [x] for a recursive variable to be safe, its one time unfolding must be safe
        // [x] if Gamma is safe and Gamma -> Gamma' then Gamma' must be safe
        // Session is already the fully 1-time unfolded view of the communications between two processes
        // if all Sessions are type safe then the network is type safe
        var paths = getExecutionPaths();
        var results = new ArrayList<Boolean>();
        for (Map<String, Behaviour> path : paths) {
            results.add(typeSafetyOnePath(path));
        }
        return results;
    }

    private boolean typeSafetyOnePath(Map<String, Behaviour> path){
        reduced = new HashMap<>();
        for (String s : path.keySet()) {
            reduced.put(s, path.get(s).duplicate());
        }
        var qs = new MessageQueues();
        var oldReduced = new HashMap<String,Behaviour>();
        var oldQs = qs.duplicate();
        do{
            oldReduced = new HashMap<>();
            oldQs = qs.duplicate();
            for (String s : reduced.keySet()) {
                oldReduced.put(s, reduced.get(s).duplicate());
            }
            for (String s : reduced.keySet()) {
                try {
                    var b = reduced.get(s).reduce(reduced, qs);
                    reduced.put(s, b);
                } catch(Exception e){
                    System.err.println(e.getMessage());
                    return false;
                }
            }
        }while(!oldReduced.equals(reduced) || !oldQs.equals(qs));
        return true;
    }

    public List<Boolean> deadlockFreedom(){
        var paths = getExecutionPaths();
        var results = new ArrayList<Boolean>();
        for (Map<String, Behaviour> path : paths) {
            if(!typeSafetyOnePath(path)){
                results.add(false);
            }else{
                var r = true;
                for (String s : reduced.keySet()) {
                    if(!reduced.get(s).isFinal()) r = false; break;
                }
                results.add(r);
            }
        }
        return results;
    }
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
            System.out.println("\n"+session.peerA()+" <-> "+session.peerB());
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
        System.out.println("Behaviours:");
        for (String s : compilerCtx.behaviours.keySet()) {
            System.out.println(s);
            System.out.println(compilerCtx.behaviours.get(s));
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
        if(session.isEmpty()){
            return null;
        }else{
            return session.get(0);
        }
    }

    private void addBehaviour(Behaviour behaviour){
        if(compilerCtx.behaviours.containsKey(compilerCtx.currentProcess)){
            compilerCtx.behaviours.get(compilerCtx.currentProcess).addBehaviour(behaviour);
        }else{
            compilerCtx.behaviours.put(compilerCtx.currentProcess, behaviour);
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
            compilerCtx.calledProceduresGraph.put(proc, new ProceduresCallGraph());
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

        //add behaviour to processDefinition
        addBehaviour(new Call(compilerCtx.currentProcess, varName));

        //we check if the variable is already mapped
        if (compilerCtx.recvar2proc.containsKey(varName)){
            //if it is it can't be mapped to another process
            if  (!compilerCtx.recvar2proc.get(varName).equals(compilerCtx.currentProcess)){
                errors.add(ERROR_RECVAR_ADD(varName, compilerCtx.recvar2proc.get(varName), compilerCtx.currentProcess, ctx));
                return errors;
            }
        }else{
            //else we simply map it
            compilerCtx.recvar2proc.put(varName, compilerCtx.currentProcess);
        }

        //we handle recursion here
        var phantomGraph = compilerCtx.phantomGraph.get(compilerCtx.currentProcess);
        var callGraph = compilerCtx.calledProceduresGraph.get(compilerCtx.currentProcess);
        if(phantomGraph != null && phantomGraph.isVarNameInGraph(varName)){ //we first check if the phantom graph exists and contains a loop
            callGraph.addLeafFrame(new StackFrame(varName, new ArrayList<>()));
            return errors;
        }
        //this process exists and has already called a method
        if(callGraph.isVarNameInGraph(varName)){
            compilerCtx.calledProceduresGraph.get(compilerCtx.currentProcess).addLeafFrame(
                    new StackFrame(varName, new ArrayList<>()));
            return errors;
        }
        compilerCtx.calledProceduresGraph.get(compilerCtx.currentProcess).addLeafFrame(
                new StackFrame(varName, new ArrayList<>()));
        //if the variable is not in the set of recursive variable definitions we add an error and return
        if(!recDefs.containsKey(varName)) {
            errors.add(ERROR_RECVAR_UNKNOWN(varName, ctx));
            compilerCtx.errors.add(ERROR_RECVAR_UNKNOWN(varName, ctx));
            return errors;
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

        var hm = new HashMap<String, Behaviour>();
        hm.put("then",contextThen.behaviours.get(compilerCtx.currentProcess));
        hm.put("else",contextElse.behaviours.get(compilerCtx.currentProcess));
        var behaviour = new Cdt(compilerCtx.currentProcess, hm, ctx.getChild(1).getText());
        addBehaviour(behaviour);
        return errors;
    }
    @Override
    public List<String> visitSnd(SPparserRich.SndContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.SEND), 7);
    }
    @Override
    public List<String> visitRcv(SPparserRich.RcvContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.RECEIVE), 7);
    }
    @Override
    public List<String> visitSel(SPparserRich.SelContext ctx) {
        return visitComm(ctx, new Communication(Utils.Direction.SELECT, ctx.getChild(2).getText()), 7);
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
            errors.addAll(visitComm(ctx, new Communication(Utils.Direction.BRANCH, ctx.getChild(i-2).getText()), i));
            contexts.add(compilerCtx);
        }
        Behaviour merged = contexts.get(0).behaviours.get(compilerCtx.currentProcess);
        if (contexts.size() > 1) {
            var ccontexts = contexts.subList(1, contexts.size());
            merged = ccontexts.stream().map(context -> context.behaviours.get(compilerCtx.currentProcess))
                    .reduce(merged, (acc, newItem) -> {
                var c = (Comm) newItem;
                acc.nextBehaviours.putAll(c.nextBehaviours);
                return acc;
            });
        }else{
            System.err.println("BIG PROBLEM");
        }
        if(!oldContext.behaviours.containsKey(compilerCtx.currentProcess)){
            oldContext.behaviours.put(compilerCtx.currentProcess, merged);
        }else{
            oldContext.behaviours.get(compilerCtx.currentProcess).addBehaviour(merged);
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
        addBehaviour(new None(compilerCtx.currentProcess));
        return new ArrayList<>();
    }
    @Override
    public List<String> visitSom(SPparserRich.SomContext ctx) {
        return ctx.getChild(2).accept(this);
    }
    public <T extends SPparserRich.BehaviourContext> List<String> visitComm(T ctx, Communication communication, int continuationIndex){
        var errors = new ArrayList<String>();
        var dest = ctx.getChild(0).getText();
        var source = compilerCtx.currentProcess;
        if(compilerCtx.currentProcess == null){
            errors.add(ERROR_NULL_PROCESS(ctx));
        }else{
            var session = getSession(source, dest);
            if(session == null){
                compilerCtx.sessions.add(new Session(source, dest, communication));
            }else{
                session.addLeafCommunicationRoots(new ArrayList<>(List.of(communication)));
            }
        }
        addBehaviour(new Comm(
                compilerCtx.currentProcess, dest, communication.getDirection(), communication.getLabel()));
        errors.addAll(ctx.getChild(continuationIndex).accept(this));
        return errors;
    }
    @Override
    public List<String> visitEnd(SPparserRich.EndContext ctx) {
        addBehaviour(new End(compilerCtx.currentProcess));
        return new ArrayList<>();
    }
}
