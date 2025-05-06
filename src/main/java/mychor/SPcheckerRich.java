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
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static mychor.Utils.ERROR_NULL_PROCESS;
import static mychor.Utils.ERROR_RECVAR_ADD;
import static mychor.Utils.ERROR_RECVAR_UNKNOWN;


public class SPcheckerRich extends SPparserRichBaseVisitor<List<String>>{
    //a list of recursive variables
    public HashMap<String, ParseTree> recDefs = new HashMap<>();
    public CompilerContext compilerCtx = new CompilerContext();


    ArrayList<ArrayList<HashMap<String, LocalType>>> history = new ArrayList<>();
    ArrayList<ArrayList<ArrayList<Message>>> qsHistory = new ArrayList<>();
    int currentHistory = 0;
    ArrayList<List<HashMap<String, LocalType>>> loopingStates = new ArrayList<>();
    ArrayList<List<Collection<LocalType>>> unreachableNodesSequence = null;
    HashMap<String,LocalType> reducedTypes = new HashMap<>();
    MessageQueues qs = new MessageQueues();

    public MessageQueues getQs(){
        return qs;
    }

    public ArrayList<ArrayList<HashMap<String, LocalType>>> getHistory() {
        return history;
    }

    public ArrayList<ArrayList<ArrayList<Message>>> getQsHistory() {
        return qsHistory;
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

    private HashMap<String, LocalType> checkLoop(){
        for (HashMap<String, LocalType> localTypes : history.get(currentHistory)) {
            if (localTypes.values().containsAll(reducedTypes.values())) {
                //before looping, we want to take all the new labels
                var mts = new ArrayList<MessageTrace>();
                for (String name : qs.keySet()) {
                    var source = name.split("-")[0];
                    var destin = name.split("-")[1];
                    var msgs = new ArrayList<>(qs.get(name));
                    for (Message msg : msgs) {
                        mts.add(new MessageTrace(source, destin, msg));
                    }
                }
                for (String process : reducedTypes.keySet()) {
                    var lt = reducedTypes.get(process);
                    if(lt instanceof BranchType bt){
                        var nonvisitedLabels = bt.getBranchesToVisitFirstLevel();
                        for (String nonvisitedLabel : nonvisitedLabels) {
                            for (MessageTrace mt : mts) {
                                if(mt.source().equals(((BranchType) lt).getDestination()) &&
                                        mt.destination().equals(process) &&
                                        mt.message().label().equals(nonvisitedLabel))
                                    // in the queue, there is a message that process will receive that it has never received before
                                    return null;
                            }
                        }
                    }
                }
                return localTypes;
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
        var msgs = new ArrayList<Message>();
        for (Queue<Message> q : qs.values()) {
            msgs.addAll(q);
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

    private ArrayList<LocalType> computeUnreachablePaths(HashMap<String, LocalType> startingPoint){
        //two situations :
        //  1. there are non-selected labels, in which case we must send them
        //  2. there are non-selected branches, in which case we must see if they can be selected
        HashMap<String, HashMap<BranchType, List<String>>> branchingNodes = new HashMap<>();
        HashMap<String, HashMap<SelectType, List<String>>> selectionNodes = new HashMap<>();
        for (String process : startingPoint.keySet()) {
            var hmb = startingPoint.get(process).getBranchingNodesAndLabelsToVisit();
            var hms = startingPoint.get(process).getSelectionNodesAndLabelsToVisit();
            if(hmb != null) branchingNodes.put(process, hmb);
            if(hms != null) selectionNodes.put(process, hms);
        }
        var unreachableNodes = getUnreachableNodes(branchingNodes, selectionNodes);
        int oldUnreachableCount;
        do{
            oldUnreachableCount = unreachableNodes.size();
            for (LocalType unreachableNode : unreachableNodes) {
                for (String process: selectionNodes.keySet()) {
                    var toRemove = new ArrayList<SelectType>();
                    for (SelectType selectType : selectionNodes.get(process).keySet()) {
                        if(unreachableNode.contains(selectType)) {
                            toRemove.add(selectType);
                        }
                    }
                    for (SelectType selectType : toRemove) {
                        selectionNodes.get(process).remove(selectType);
                    }
                }
                selectionNodes.entrySet().removeIf(e -> e.getValue().isEmpty());

                for (String process: branchingNodes.keySet()) {
                    var toRemove = new ArrayList<BranchType>();
                    for (BranchType branchType : branchingNodes.get(process).keySet()) {
                        if(unreachableNode.contains(branchType)) {
                            toRemove.add(branchType);
                        }
                    }
                    for (BranchType branchType : toRemove) {
                        selectionNodes.get(process).remove(branchType);
                    }
                }
                branchingNodes.entrySet().removeIf(e -> e.getValue().isEmpty());
            }
            unreachableNodes.addAll(getUnreachableNodes(branchingNodes, selectionNodes));
        }while(unreachableNodes.size() != oldUnreachableCount);
        return unreachableNodes;
    }

    private boolean nonVisitedPathsAreReachable(HashMap<String, LocalType> initialTypes){
        var unreachableNodes = computeUnreachablePaths(initialTypes);
        var unvisitedNodes = new ArrayList<LocalType>();
        for (LocalType value : initialTypes.values()) {
            unvisitedNodes.addAll(value.getUnvisitedNodes());
        }
        if(!unreachableNodes.isEmpty()) {
            if (unreachableNodesSequence == null)
                unreachableNodesSequence = new ArrayList<>();
            unreachableNodesSequence.add(Collections.singletonList(unreachableNodes));
        }
        for (LocalType unvisitedNode : unvisitedNodes) {
            if(!unreachableNodes.contains(unvisitedNode) &&
                    unreachableNodes.stream().noneMatch(type -> type.contains(unvisitedNode))) return true;
        }
        return false;
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

    private boolean mustRunOtherIteration(HashMap<String, LocalType> initialTypes){
        if(!initialTypes.values().stream().allMatch(LocalType::visitedAllPaths)){
            return nonVisitedPathsAreReachable(initialTypes);
        }
        return false;
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
        qsHistory.get(currentHistory).add(new ArrayList<>());
        qs.saveIterationAndPrepareNextOne();
    }

    private boolean checkContinuingIsPossible(){
        int unvisitedUnreachable = 0;
        var unreachablePaths = computeUnreachablePaths(reducedTypes);
        var unvisitedPaths = reducedTypes.values().stream().filter(el -> !el.visitedAllPaths()).toList();
        for (LocalType unvisitedPath : unvisitedPaths) {
            if(unreachablePaths.contains(unvisitedPath)) unvisitedUnreachable ++;
            else if(unvisitedPath instanceof BranchType bt){
                var allUnvisitedLabelsAreUnreachable = bt.nextTypes.keySet().stream()
                        .filter(el -> !bt.getVisitedLabels().contains(el))
                        .allMatch(el -> unreachablePaths.contains(bt.nextTypes.get(el)));
                if(allUnvisitedLabelsAreUnreachable) unvisitedUnreachable ++;
            }else if(unvisitedPath instanceof ReceiveType rt){
                if(unreachablePaths.contains(rt.nextTypes.get(";"))) unvisitedUnreachable ++;
            }
        }
        return unvisitedUnreachable != unvisitedPaths.size();
    }

    public void reduceNetwork(){
        // extracting types
        var initialTypes = setupTypes();
        history.add(new ArrayList<>());
        qsHistory.add(new ArrayList<>());
        var hm = new HashMap<>(initialTypes);
        history.get(currentHistory).add(hm);
        qsHistory.get(currentHistory).add(new ArrayList<>());
        while(true){
            var iterationOver = false;
            reduceTypes();
            if(reducedTypes.values().stream().allMatch(t -> t instanceof EndType)){
                reducedTypes.values().forEach(t -> t.reduce(null, null));
                loopingStates.add(new ArrayList<>());
                iterationOver = true;
            }else{
                // check if states are already registered AND their children are fully visited
                var loopingTypes = checkLoop();
                //this set of states has already been registered
                if(loopingTypes != null){
                    var continuingIsPossible = checkContinuingIsPossible();
                    if(reducedTypes.values().stream().allMatch(LocalType::visitedAllPaths) || !continuingIsPossible) {
                        registerLoop(loopingTypes);
                        alignMessages();
                        iterationOver = true;
                    }
                }
            }
            addReducedTypesToHistory();
            if(iterationOver){
                if (mustRunOtherIteration(initialTypes)) {
                    prepareIteration(initialTypes);
                } else {
                    finalizeIteration();
                    break;
                }
            }
        }
    }


    private ArrayList<LocalType> getUnreachableNodes(HashMap<String, HashMap<BranchType, List<String>>> branchingNodes,
                                                HashMap<String, HashMap<SelectType, List<String>>> selectionNodes) {
        var bNodes = new ArrayList<LocalType>();
        for (String destination : branchingNodes.keySet()) {
            for (BranchType branchType : branchingNodes.get(destination).keySet()) {
                var toRemove = new ArrayList<String>();
                for (String label : branchingNodes.get(destination).get(branchType)) {
                    boolean add = true;
                    for (String source : selectionNodes.keySet().stream()
                            .filter(el -> !el.equals(destination)).toList()) {
                        for (SelectType selectType : selectionNodes.get(source).keySet().stream()
                                .filter(el -> el.getDestination().equals(destination)).toList()) {
                            if(selectionNodes.get(source).get(selectType).contains(label)) {
                                add = false;
                            }
                        }
                    }
                    if(add){
                        bNodes.add(branchType.nextTypes.get(label));
                        toRemove.add(label);
                    }
                }
                var l = new ArrayList<>(branchingNodes.get(destination).get(branchType));
                l.removeAll(toRemove);
                branchingNodes.get(destination).put(branchType, l);
            }
            branchingNodes.get(destination).entrySet().removeIf(e -> e.getValue().isEmpty());
        }
        branchingNodes.entrySet().removeIf(e -> e.getValue().isEmpty());
        return bNodes;
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

    public ArrayList<List<Collection<LocalType>>> getUnreachableNodes() {
        if(unreachableNodesSequence == null){
            HashMap<String,LocalType> initialTypes = new HashMap<>();
            unreachableNodesSequence = new ArrayList<>();
            for (String s : compilerCtx.behaviours.keySet()) {
                try {
                    var type = LocalType.extractLocalType(compilerCtx.behaviours.get(s));
                    initialTypes.put(s, type);
                } catch (Exception e) {
                    System.err.println("error while extracting type for process "+s);
                    throw new RuntimeException(e);
                }
            }
            nonVisitedPathsAreReachable(initialTypes);
        }
        return unreachableNodesSequence;
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