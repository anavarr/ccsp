package mychor;

import mychor.types.BranchType;
import mychor.types.EndType;
import mychor.types.LocalType;
import mychor.types.MessageTrace;
import mychor.types.ReceiveType;
import mychor.types.RecurseDefType;
import mychor.types.SelectType;
import mychor.types.SendType;
import org.antlr.v4.runtime.tree.ParseTree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static mychor.Utils.ERROR_NULL_PROCESS;
import static mychor.Utils.ERROR_RECVAR_ADD;
import static mychor.Utils.ERROR_RECVAR_UNKNOWN;


public class SPcheckerRich extends SPparserRichBaseVisitor<List<String>>{
    //a list of recursive variables
    public HashMap<String, ParseTree> recDefs = new HashMap<>();
    public CompilerContext compilerCtx = new CompilerContext();


    ArrayList<ArrayList<HashMap<String, LocalType>>> history = new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, ArrayList<Message>>>> qsHistory = new ArrayList<>();
    int currentHistory = 0;
    ArrayList<List<HashMap<String, LocalType>>> loopingStates = new ArrayList<>();
    HashMap<String,LocalType> reducedTypes = new HashMap<>();
    MessageQueues qs = new MessageQueues();

    public MessageQueues getQs(){
        return qs;
    }

    public ArrayList<ArrayList<HashMap<String, LocalType>>> getHistory() {
        return history;
    }

    public ArrayList<ArrayList<HashMap<String, ArrayList<Message>>>> getQsHistory() {
        return qsHistory;
    }

    public HashMap<String, Integer> numberOfInstances(Function<LocalType, Boolean> predicate){
        var hm = new HashMap<String, Integer>();
        for (String s : history.getFirst().getFirst().keySet()) {
            hm.put(s,
                    history.getFirst().getFirst().get(s).count(predicate)
            );
        }
        return hm;
    }

    public HashMap<String, List<Integer>> distributionOfInstances(Function<LocalType, Boolean> predicate){
        var hm = new HashMap<String, List<Integer>>();
        for (String s : history.getFirst().getFirst().keySet()) {
            hm.put(s,
                    history.getFirst().getFirst().get(s).countPerBranch(predicate)
            );
        }
        return hm;
    }

    public HashMap<String, Integer> numberOfSelections(){
        return numberOfInstances((lt) -> lt instanceof SelectType);
    }

    public HashMap<String, Integer> numberOfBranching(){
        return numberOfInstances((lt) -> lt instanceof BranchType);
    }

    public HashMap<String, Integer> numberOfBranchingAndSelections(){
        return numberOfInstances((lt) -> lt instanceof BranchType || lt instanceof SelectType);
    }

    public HashMap<String, List<Integer>> selectionDistribution(){
        return distributionOfInstances((lt) -> lt instanceof SelectType);
    }

    public HashMap<String, List<Integer>> branchingDistribution(){
        return distributionOfInstances((lt) -> lt instanceof BranchType);
    }

    public HashMap<String, List<Integer>> selectionAndBranchingDistribution(){
        return distributionOfInstances((lt) -> lt instanceof SelectType || lt instanceof BranchType);
    }

    public HashMap<String, ArrayList<ArrayList<String>>> getBranches() {
        for (String s : history.getFirst().getFirst().keySet()) {

        }
        return null;
    }

    private HashMap<String, LocalType> setupTypes(){
        HashMap<String,LocalType> initialTypes = new HashMap<>();
        for (String s : compilerCtx.behaviours.keySet()) {
            try {
                var type = LocalType.extractLocalType(compilerCtx.behaviours.get(s));
                initialTypes.put(s, type);
                reducedTypes.put(s, type);
            } catch (Exception e) {
                System.err.println("error while extracting type for process "+s);
                throw new RuntimeException(e);
            }
        }
        return initialTypes;
    }

    private void reduceTypes(){
        for (String s : reducedTypes.keySet()) {
            try {
                reducedTypes.put(s, reducedTypes.get(s).reduce(s, qs));
            } catch(Exception e){
                throw new RuntimeException("error while reducing process "+s +" : \n" +e.getMessage());
            }
        }
    }

    public HashMap<String, Integer> getBranchingDepths(HashMap<String, LocalType> network){
        var hm = new HashMap<String, Integer>();
        for (Map.Entry<String, LocalType> entry : network.entrySet()) {
            hm.put(entry.getKey(), 1);
        }
        return hm;
    }

    private HashMap<String, LocalType> checkLoop(){
        for (int i = 0; i < history.get(currentHistory).size(); i++) {
            if(history.get(currentHistory).get(i).values().containsAll(reducedTypes.values())){
                var qstate = new HashMap<String, ArrayList<Message>>();
                for (Map.Entry<String, Queue<Message>> entry : qs.entrySet()) {
                    qstate.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
                for (String s : qstate.keySet()) {
                    var qstates = qstate.get(s);
                    var qhistorys = qsHistory.get(currentHistory).get(i).get(s);
                    if(qstates == null){
                        if(qhistorys == null || qhistorys.isEmpty()) return history.get(currentHistory).get(i);
                    }else if(qstates.isEmpty()){
                        if(qhistorys == null || qhistorys.isEmpty()) return history.get(currentHistory).get(i);
                    }else{
                        if(qhistorys == null || qhistorys.isEmpty()) continue;
                        if(qstates.size() < qhistorys.size()) break;
                        if(qstates.containsAll(qhistorys)) return history.get(currentHistory).get(i);
                    }
                }
            }
        }
        return null;
    }

    private void registerLoop(HashMap<String, LocalType> loopingTypes){
        var start = history.get(currentHistory).indexOf(loopingTypes);
        var loopingSet = new ArrayList<HashMap<String, LocalType>>();
        for (int i = start; i < history.get(currentHistory).size(); i++) {
            loopingSet.add(history.get(currentHistory).get(i));
        }
        loopingSet.add(loopingTypes);
        loopingStates.add(loopingSet);
    }

    private void saveQs(){
        var msgs = new HashMap<String, ArrayList<Message>>();
        for (Map.Entry<String, Queue<Message>> entry : qs.entrySet()) {
            var q = new ArrayList<>(entry.getValue());
            msgs.put(entry.getKey(), q);
        }
        qsHistory.get(currentHistory).add(msgs);
    }

    private void addReducedTypesToHistory(){
        saveQs();
        var toAdd = new HashMap<String, LocalType>();
        for (String s : reducedTypes.keySet()) {
            toAdd.put(s, reducedTypes.get(s));
        }
        history.get(currentHistory).add(toAdd);
    }

    private void alignMessages(){
        if(!qs.isEmpty()){
            for (String s : qs.keySet()) {
                LocalType consumer = null;
                while(!qs.get(s).isEmpty()){
                    var dest = s.split("-")[1];
                    if(consumer == null){
                        consumer = reducedTypes.get(dest);
                    }
                    var msg = qs.get(s).peek();
                    assert msg != null;
                    var mustReduceConsumer = consumer instanceof RecurseDefType;
                    while(mustReduceConsumer){
                        var c2 = consumer.reduce(s, qs);
                        while(c2 instanceof RecurseDefType){
                            if (c2.equals(consumer)) {
                                mustReduceConsumer = false;
                                break;
                            }
                            c2 = c2.reduce(s, qs);
                        }
                        consumer = c2;
                        mustReduceConsumer = consumer instanceof RecurseDefType;
                    }
                    if(msg.label() == null){
                        if(consumer instanceof ReceiveType) {
                            consumer = consumer.reduce(s, qs);
                            qs.get(s).poll();
                        }
                        else break;
                    }else{
                        if(consumer instanceof BranchType) {
                            consumer = consumer.reduce(s, qs);
                            qs.get(s).poll();
                        }
                        else break;
                    }
                }
            }
        }
    }

    private void finalizeIteration(){
        qs.saveIteration();
    }

    private void prepareIteration(HashMap<String, LocalType> initialTypes){
        reducedTypes.replaceAll((s, v) -> initialTypes.get(s));
        currentHistory++;
        history.add(new ArrayList<>());
        qsHistory.add(new ArrayList<>());
        reducedTypes.replaceAll((s, v) -> initialTypes.get(s));
        history.get(currentHistory).add(new HashMap<>(initialTypes));
        qsHistory.get(currentHistory).add(new HashMap<>());
        qs.saveIterationAndPrepareNextOne();
    }


    public void reduceNetwork(){
        var counter = 0;
        Function<Integer, Boolean> alwaysTrue = (c) -> true;
        Function<Integer, Boolean> alwaysFals = (c) -> false;
        BiFunction<Integer, Integer, Boolean> checkOnceEveryXRounds = (c, n) -> c%n == 0;
        Function<Integer, Boolean> checkOnceEvery20Rounds = (c) -> checkOnceEveryXRounds.apply(c, 20);
        // extracting types
        var initialTypes = setupTypes();
        history.add(new ArrayList<>());
        qsHistory.add(new ArrayList<>());
        var hm = new HashMap<>(initialTypes);
        history.get(currentHistory).add(hm);
        int deadline = 1;
        var iterationsCounter = 0;
        qsHistory.get(currentHistory).add(new HashMap<>());
        while(true){
            counter++;
            var iterationOver = false;
            reduceTypes();
            if(reducedTypes.values().stream().allMatch(t -> t instanceof EndType)){
                reducedTypes.values().forEach(t -> t.reduce(null, null));
                loopingStates.add(new ArrayList<>());
                iterationOver = true;
            }else{
                // check if states are already registered AND their children are fully visited
//                HashMap<String, LocalType> loopingTypes = null;
//                if(alwaysTrue.apply(counter)) loopingTypes = checkLoop();
                var loopingTypes = checkLoop();
                //this set of states has already been registered
                if(loopingTypes != null){
                    registerLoop(loopingTypes);
                    alignMessages();
                    iterationOver = true;
                }
            }
            addReducedTypesToHistory();
            if(iterationOver){
                iterationsCounter ++;
                if(!history.subList(0, history.size()-1).contains(history.getLast())){
                    deadline = (iterationsCounter+1)*2;
                }
                if (iterationsCounter < deadline) {
                    prepareIteration(initialTypes);
                } else {
                    finalizeIteration();
                    break;
                }
            }
        }
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
        compilerCtx.sessions = Session.fromBehaviours(compilerCtx.behaviours);
        return errors;
    }

    @Override
    public List<String> visitNetwork(SPparserRich.NetworkContext ctx) {
        // 0 : first process serviceName
        // 1 : [
        // 2 : behaviour
        // 3 : ]
        // 4 : |
        // 5 : second process serviceName
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
        // 1 : serviceName
        var varName = ctx.getChild(1).getText();
        var errors = new ArrayList<String>();

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

        //add behaviour to processDefinition
        addBehaviour(new Call(compilerCtx.currentProcess, varName));

        //we handle recursion here
        var phantomGraph = compilerCtx.phantomGraph.get(compilerCtx.currentProcess);
        var callGraph = compilerCtx.calledProceduresGraph.get(compilerCtx.currentProcess);
        if(callGraph.isVarNameInGraph(varName) || (phantomGraph != null && phantomGraph.isVarNameInGraph(varName))){
            return errors;
        }
        compilerCtx.calledProceduresGraph.get(compilerCtx.currentProcess).addLeafFrame(varName);

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
                ProceduresCallGraphMap::mergeCalledProceduresHorizontal, ctx);
        var errors = mergedContext.errors;

        //we merge it
        compilerCtx = CompilerContext.mergeContexts(oldContext, mergedContext,
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
        return visitComm(ctx, Utils.Direction.SEND, ctx.getChild(2).getText(), 7);
    }

    @Override
    public List<String> visitRcv(SPparserRich.RcvContext ctx) {
        return visitComm(ctx, Utils.Direction.RECEIVE, ctx.getChild(2).getText(), 7);
    }

    @Override
    public List<String> visitSel(SPparserRich.SelContext ctx) {
        return visitComm(ctx, Utils.Direction.SELECT, ctx.getChild(2).getText(), 7);
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
            errors.addAll(visitComm(ctx, Utils.Direction.BRANCH, ctx.getChild(i-2).getText(), i));
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
                (context1, context2) -> CompilerContext.mergeContexts(context1, context2,
                        ProceduresCallGraphMap::mergeCalledProceduresHorizontal, ctx));

        //we merge it with the previous context
        compilerCtx = CompilerContext.mergeContexts(oldContext, compilerCtx,
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
    public <T extends SPparserRich.BehaviourContext> List<String> visitComm(
            T ctx, Utils.Direction direction, String payload, int continuationIndex){
        var errors = new ArrayList<String>();
        var dest = ctx.getChild(0).getText();
        if(compilerCtx.currentProcess == null){
            errors.add(ERROR_NULL_PROCESS(ctx));
        }
        addBehaviour(new Comm(
                compilerCtx.currentProcess, dest, direction, payload));
        errors.addAll(ctx.getChild(continuationIndex).accept(this));
        return errors;
    }

    @Override
    public List<String> visitEnd(SPparserRich.EndContext ctx) {
        addBehaviour(new End(compilerCtx.currentProcess));
        return new ArrayList<>();
    }

    public boolean generateWaste() {
        for (ArrayList<Message> leftOver : qs.leftOvers) {
            if(!leftOver.isEmpty()) return true;
        }
        return false;
    }

    private ArrayList<HashSet<LocalType>> clusterizeForProcess(String s) {
        var clusters = new ArrayList<HashSet<LocalType>>();
        var size = 0;
        var ssize = 0;
        ArrayList<List<LocalType>> localLoopingStates = new ArrayList<>(loopingStates.stream().map(el -> el.stream().map(item -> item.get(s)).toList()).toList());
        clusters.add(new HashSet<>(localLoopingStates.removeFirst()));
        while(clusters.size() > ssize){
            while(clusters.getLast().size() > size){
                size = clusters.getLast().size();
                var toRemove = new ArrayList<List<LocalType>>();
                for (List<LocalType> localLoopingState : localLoopingStates) {
                    if(localLoopingState.stream().anyMatch(it -> clusters.getLast().contains(it))){
                        clusters.getLast().addAll(localLoopingState);
                        toRemove.add(localLoopingState);
                    }
                }
                for (List<LocalType> localTypes : toRemove) {
                    localLoopingStates.remove(localTypes);
                }
            }
            if(localLoopingStates.isEmpty()) break;
            size = 0;
            ssize = clusters.size();
            clusters.add(new HashSet<>(localLoopingStates.removeFirst()));
        }
        return clusters;
    }

    //the logic of livelock:
    // - if two loops share a state, they are joined (we can switch from one to the other)
    // - if they don't, they are disjoined, we can't switch from one to the other
    // - if the starting state is in a loop, then it is good
    public HashMap<String, ArrayList<HashSet<LocalType>>> clusterizeNodes(){
        var clusters = new HashMap<String, ArrayList<HashSet<LocalType>>>();
        var initialStates = history.getFirst().getFirst();
        for (String s : initialStates.keySet()) {
            clusters.put(s, clusterizeForProcess(s));
        }
        return clusters;
    }

    public HashMap<String, HashSet<LocalType>> livelockedNodes(){
        var initialTypes = history.getFirst().getFirst();
        var clusters = clusterizeNodes();
        var livelockedNodes = new HashMap<String, HashSet<LocalType>>();
        for (String s : initialTypes.keySet()) {
            livelockedNodes.put(s, new HashSet<>());
        }
        for (Map.Entry<String, ArrayList<HashSet<LocalType>>> entry : clusters.entrySet()) {
            var nonAccessibleFromStart = entry.getValue().stream()
                    .filter(set -> !set.contains(initialTypes.get(entry.getKey()))).flatMap(Collection::stream).toList();
            livelockedNodes.get(entry.getKey()).addAll(nonAccessibleFromStart);
        }
        return livelockedNodes;
    }

    public HashMap<String, HashSet<LocalType>> livelockedNodesOld(){
        var hm = new HashMap<String, HashSet<LocalType>>();
        for (int i = 0; i < loopingStates.size(); i++) {
            var cls = loopingStates.get(i);
            if(cls.isEmpty()) continue;
            for (int i1 = 0; i1 < history.size(); i1++) {
                if(i== i1) continue;
                var ch = history.get(i1);
                var o = ch.stream().filter(cls::contains).toList();
                if(o.isEmpty()) {
                    for (HashMap<String, LocalType> cl : cls) {
                        for (String s : cl.keySet()) {
                            hm.computeIfAbsent(s, key -> new HashSet<>()).add(cl.get(s));
                        }
                    }
                }
            }
        }
        return hm;
    }
    
    public HashMap<String, List<LocalType>> getUnreachableNodes() {
        if(history.isEmpty()) System.err.println("network wasn't reduced yet");
        var unreach = new HashMap<String, List<LocalType>>();
        for (String s : history.getFirst().getFirst().keySet()) {
            unreach.put(s, history.getFirst().getFirst().get(s).getUnvisitedNodes());
        }
        return unreach;
    }

    private HashMap<String, ArrayList<LocalType>> deadlockedNodesInIteration(int iteration){
        var deadlockedNodes = new HashMap<String, ArrayList<LocalType>>();
        var round = history.get(iteration).getLast();
        for (String s : round.keySet()) {
            var node = round.get(s);
            if(node instanceof ReceiveType  || node instanceof BranchType){
                //there is a receive/branch in the last step of the history
                if(!loopingStates.get(iteration).stream().map(el -> el.get(s)).toList().contains(node)){
                    //the receive is not in a loop, it is a deadlock
                    deadlockedNodes.computeIfAbsent(s, k-> new ArrayList<>()).add(node);
                }else {
                    // the receive is in a loop, it might or might not be a deadlock
                    String dest;
                    if (node instanceof ReceiveType rt) dest = rt.getDestination();
                    else {
                        BranchType bt = (BranchType) node;
                        dest = bt.getDestination();
                    }
                    String finalDest = dest;
                    var senderStates = loopingStates.get(iteration).stream().map(el -> el.get(finalDest)).toList();
                    var hs = new HashSet<>(senderStates);
                    if (hs.size() == 1 ||
                            (node instanceof ReceiveType && hs.stream()
                                    .noneMatch(el -> el instanceof SendType st && st.getDestination().equals(s))) ||
                            (node instanceof BranchType && hs.stream()
                                    .noneMatch(el -> el instanceof SelectType slt && slt.getDestination().equals(s)))) {
                            deadlockedNodes.computeIfAbsent(s, k -> new ArrayList<>()).add(node);
                    }
                }
            }
        }
        return deadlockedNodes;
    }

    public HashMap<String, ArrayList<LocalType>> deadlockedNodes(){
        var deadlockedNodes = new HashMap<String, ArrayList<LocalType>>();
        for (int i = 0; i < history.size(); i++) {
            var dn = deadlockedNodesInIteration(i);
            if(dn.isEmpty()){
                continue;
            }
            if(loopingStates.get(i).contains(history.get(i).getLast())){
                var deadlockedState = history.get(i).getLast();
                int finalI = i;
                var canProgress = history.stream().anyMatch(it ->
                        !it.equals(history.get(finalI)) &&
                        it.contains(deadlockedState) &&
                        deadlockedNodesInIteration(history.indexOf(it)).isEmpty());
                if(canProgress) continue;
            }
            for (String s : dn.keySet()) {
                deadlockedNodes.computeIfAbsent(s, k  -> new ArrayList<>())
                        .addAll(dn.get(s));
            }
        }
        return deadlockedNodes;
    }

    public boolean deadlockFreedom() {
        return deadlockedNodes().isEmpty();
    }
}