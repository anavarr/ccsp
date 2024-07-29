package mychor;

import java.nio.channels.SeekableByteChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

public record Session(String peerA, String peerB, ArrayList<Communication> communicationsRoots) {
    public Session {
        Objects.requireNonNull(peerA, "peerA cannot be null");
        Objects.requireNonNull(peerB, "peerB cannot be null");
        Objects.requireNonNull(communicationsRoots, "communication chain cannot be null");
    }

    public Session(String peerA, String peerB, Communication comRoot){
        this(peerA, peerB, new ArrayList<>(List.of(comRoot)));
    }
    public String getInitiator(){
        return (communicationsRoots.get(0).isSend() || communicationsRoots.get(0).isSelect()) ? peerA : peerB;
    }

    public String getInitiated(){
        return (communicationsRoots.get(0).isReceive() || communicationsRoots.get(0).isBranch()) ? peerA : peerB;
    }

    public List<Communication> getRecursiveCallersTo(Communication comm){
        var recursiveCallersTo = new ArrayList<Communication>();
        for (Communication communicationsRoot : communicationsRoots) {
            recursiveCallersTo.addAll(communicationsRoot.getRecursiveCallersTo(comm));
        }
        return recursiveCallersTo;
    }

    public boolean areEnds(String peerAP, String peerBP){
        return (peerA.equals(peerAP) || peerA.equals(peerBP)) & (peerB.equals(peerAP) || peerB.equals(peerBP));
    }
    public boolean isSelfComm(){
        return peerA.equals(peerB);
    }

    public int getCommunicationsSize(){
        int c = 0;
        for (Communication communicationsRoot : communicationsRoots) {
           c+=communicationsRoot.getBranchesSize();
        }
        return c;
    }
    public boolean isComplementary(Session comp){
        // we check everything session-related first
        if (!areEnds(comp.peerA, comp.peerB)){
            System.err.println("not the same peers");
            return false;
        }
        if(!getInitiator().equals(comp.getInitiator())){
            System.err.println("not the same initiators");
            return false;
        }
        // then we check everything non-session related
        if(communicationsRoots.size() != comp.communicationsRoots.size()){
            System.err.println("not the same continuation roots size");
            return false;
        }
        for (Communication communicationsRoot : communicationsRoots) {
            if(comp.communicationsRoots.stream().noneMatch(co -> co.isComplementary(communicationsRoot))) return false;
        }
        return true;
    }

    public boolean isBranchingValid(){
        if(communicationsRoots.isEmpty()) return true;
        //if the branches are all SELECT, then anything following is valid
        var allSelect = communicationsRoots.stream()
                .allMatch(item -> item.getDirection() == Utils.Direction.SELECT);
        //if the branches are all BRANCH, then anything following is valid
        var allBranch = communicationsRoots.stream()
                .allMatch(item -> item.getDirection() == Utils.Direction.BRANCH);
        //else, branching is valid if all branches are the same
        var allSame = communicationsRoots.stream()
                .allMatch(item -> item.equals(communicationsRoots.get(0)));
        if(!(allSame || allSelect || allBranch)) return false;
        return communicationsRoots.stream().allMatch(Communication::isBranchingValid);
    }

    public boolean hasSameEnds(Session comp) {
        return peerA.equals(comp.peerA()) && peerB.equals(comp.peerB());
    }

    public static void fromBehaviour(Behaviour b, SmallContext ctx){
        switch(b){
            case null -> {
                return;
            }
            case Comm comm -> {
                processComm(ctx, comm);
            }
            case Cdt cdt -> {
                processCdt(ctx, cdt);
            }
            case End end -> {

            }
            case None none -> {

            }
            case Call call -> {
                processCall(ctx, call);
            }
            default -> throw new IllegalStateException("Unexpected value: " + b);
        }
    }
    private static void processCall(SmallContext ctx, Call call) {
        var nextBehaviour = call.nextBehaviours.isEmpty() ? null : call.nextBehaviours.get("unfold");
        if(ctx.recursiveCommunicationsEntrypoint.containsKey(call.variableName)){
            //already called it
            if(ctx.recursiveCommunicationsEntrypoint.get(call.variableName) == null){
                //tight loop with nothing going on between calls to this method
                return;
            }
            var sessionsCommunicationMap = ctx.recursiveCommunicationsEntrypoint.get(call.variableName);
            for (String s : sessionsCommunicationMap.keySet()) {
                var pA = s.split("-")[0];
                var pB = s.split("-")[1];
                var exactSession = ctx.sessions.stream().filter(se -> se.peerA.equals(pA) && se.peerB.equals(pB)).findFirst();
                if(exactSession.isEmpty()){
                    //get the last communication of the same session, check if the recursive communication endpoints are above, add them or put them recursive
                    var lastSession = ctx.getLastSession(pA, pB);
                    if(lastSession == null){
                        ctx.sessions.add(new Session(pA, pB, sessionsCommunicationMap.get(s)));
                    }else{
                        var newComs = new ArrayList<Communication>();
                        for (Communication communication : sessionsCommunicationMap.get(s)) {
                            if(ctx.previousContext.communicationIsPresentInPreviousContexts(communication))
                                for (Communication leaf : lastSession.getLeaves()) {
                                    leaf.addRecursiveCallee(communication);
                                }
                            else
                                newComs.add(communication);
                            if(!newComs.isEmpty()) ctx.sessions.add(new Session(pA,pB, newComs));
                        }
                    }
                }else{
                    var previousNodes = sessionsCommunicationMap.get(s).stream()
                            .filter(ctx::communicationIsPresentInPreviousContexts).toList();
                    var nonPreviousNodes = sessionsCommunicationMap.get(s).stream()
                            .filter(co -> !ctx.communicationIsPresentInPreviousContexts(co)).toList();
                    for (Communication communication : previousNodes) {
                        for (Communication leaf : exactSession.get().getLeaves()) {
                            leaf.addRecursiveCallee(communication);
                        }
                    }for (Communication leaf : exactSession.get().getLeaves()) {
                        leaf.addLeafCommunicationRoots(new ArrayList<>(nonPreviousNodes));
                    }

                }
            }
        }else{
            //not called
            ctx.currentDummy = null;
            ctx.recursiveCommunicationsEntrypoint.put(call.variableName, new HashMap<>());
            ctx.lastVariable = call.variableName;
            fromBehaviour(nextBehaviour, ctx);
        }
    }

    private static void processCdt(SmallContext ctx, Cdt cdt) {
        var dummies = new ArrayList<Communication>();
        var ctxs = new ArrayList<SmallContext>();
        for (String s : cdt.nextBehaviours.keySet()) {
            var newCtx = new SmallContext(ctx);
            newCtx.recursiveCommunicationsEntrypoint = ctx.recursiveCommunicationsEntrypoint;
            if(ctx.lastVariable != null){
                Communication dummy = new Communication(Utils.Direction.DUMMY);
                dummies.add(dummy);
                newCtx.currentDummy = dummy;
            }
            fromBehaviour(cdt.nextBehaviours.get(s), newCtx);
            ctxs.add(newCtx);
        }
        var  topDummy = new Communication(Utils.Direction.DUMMY);
        //replace all Dummies with Single Dummy
        if(dummies.size() > 1){
            var dummy1 = dummies.getFirst();
            var ctx1 = ctxs.getFirst();
            var dummy2 = dummies.getLast();
            var ctx2 = ctxs.getLast();
            topDummy.addNextCommunicationNodes(new ArrayList<>(dummy1.getNextCommunicationNodes()));
            for (Communication recursiveCallee : dummy1.getRecursiveCallees()) {
                topDummy.addRecursiveCallee(recursiveCallee);
            }
            topDummy.addNextCommunicationNodes(new ArrayList<>(dummy2.getNextCommunicationNodes()));
            for (Communication recursiveCallee : dummy2.getRecursiveCallees()) {
                topDummy.addRecursiveCallee(recursiveCallee);
            }


            for (Session session : ctx1.sessions) {
                session.replace2(dummy2, topDummy);
                session.replace(dummy1, topDummy);
            }
            for (Session session : ctx2.sessions) {
                session.replace2(dummy1, topDummy);
                session.replace(dummy2, topDummy);
            }
            ctx = SmallContext.mergeHorizontalStub(ctxs).mergeWithPrevious();
            for (Session session : ctx.sessions) {
                session.removeInPlace(topDummy);
            }
        }else{
            ctx = SmallContext.mergeHorizontalStub(ctxs).mergeWithPrevious();
        }
    }

    private void replace2(Communication toReplace, Communication replacer){
        for (Communication recursiveCallee : toReplace.getRecursiveCallees()) {
            replacer.addRecursiveCallee(recursiveCallee);
        }
        var wasHere = communicationsRoots.remove(toReplace);
        if(wasHere) communicationsRoots.add(replacer);
        for (Communication communicationsRoot : communicationsRoots) {
            communicationsRoot.replace2(toReplace, replacer);
        }

    }

    private void replace(Communication toReplace, Communication replacer) {
        for (Communication recursiveCallee : toReplace.getRecursiveCallees()) {
            replacer.addRecursiveCallee(recursiveCallee);
        }
        var wasHere = communicationsRoots.remove(toReplace);
        if(wasHere) communicationsRoots.add(replacer);
        for (Communication communicationsRoot : communicationsRoots) {
            communicationsRoot.replace(toReplace, replacer);
        }
    }

    private void removeInPlace(Communication dummy) {
        var found = false;
        for (int i = 0; i < communicationsRoots.size(); i++) {
            communicationsRoots.get(i).removeInPlace(dummy);
            if(communicationsRoots.get(i) == dummy){
                found = true;
            }
        }
        if(found){
            while(communicationsRoots.remove(dummy)){
                communicationsRoots.remove(dummy);
            }
            communicationsRoots.addAll(dummy.getNextCommunicationNodes());
        }
    }

    private static void processComm(SmallContext ctx, Comm comm) {
        var peerA = comm.process;
        var peerB = comm.destination;
        var direction = comm.direction;
        var lastVariable = ctx.getLastCalledVariable();
        var sessionKey = peerA+"-"+peerB;
        var mustUpdateRecursiveCommunicationsEntrypoint =
                lastVariable != null &&
                        ctx.recursiveCommunicationsEntrypoint.containsKey(lastVariable) &&
                                !ctx.recursiveCommunicationsEntrypoint.get(lastVariable).containsKey(sessionKey);
        if(ctx.currentDummy != null){
            mustUpdateRecursiveCommunicationsEntrypoint = false;
            if(lastVariable != null){
                if(ctx.recursiveCommunicationsEntrypoint.get(lastVariable).containsKey(sessionKey)){
                    ctx.recursiveCommunicationsEntrypoint.get(lastVariable).get(sessionKey).add(ctx.currentDummy);
                }else{
                    ctx.recursiveCommunicationsEntrypoint.get(lastVariable)
                            .put(sessionKey, new ArrayList<>(List.of(ctx.currentDummy)));
                }
            }
            var maybeSession = ctx.sessions.stream().filter(s -> s.peerA().equals(peerA) && s.peerB.equals(peerB)).findFirst();
            if(maybeSession.isPresent()){
                maybeSession.get().addLeafCommunicationRoots(new ArrayList<>(List.of(ctx.currentDummy)));
            }else{
                ctx.sessions.add(new Session(peerA, peerB, new ArrayList<>(List.of(ctx.currentDummy))));
            }
            ctx.currentDummy = null;
        }

        if(direction.equals(Utils.Direction.BRANCH)){
            //There can be several nextNodes
            var ctxs = new ArrayList<SmallContext>();
            List<Communication> comms = comm.nextBehaviours.keySet().stream()
                    .map(label -> new Communication(Utils.Direction.BRANCH, label)).toList();
            if(mustUpdateRecursiveCommunicationsEntrypoint){
                ctx.recursiveCommunicationsEntrypoint.get(lastVariable).put(sessionKey, new ArrayList<>(comms));
                ctx.lastVariable = null;
            }
            for (Communication communication : comms) {
                var newCtx = new SmallContext(ctx);
                newCtx.recursiveCommunicationsEntrypoint = ctx.recursiveCommunicationsEntrypoint;
                var session = new Session(peerA, peerB, communication);
                newCtx.sessions.add(session);
                fromBehaviour(comm.nextBehaviours.get(communication.getLabel()), newCtx);
                ctxs.add(newCtx);
            }
            ctx = SmallContext.mergeHorizontal(ctxs).mergeWithPrevious();
        }else{
            String label=null;
            String nextBehaviourKey = comm.nextBehaviours.keySet().stream().findAny().get();
            if(direction == Utils.Direction.SELECT) label = nextBehaviourKey;
            Communication co = new Communication(direction, label);
            var maybeSession = ctx.sessions.stream()
                    .filter(sess-> sess.peerA.equals(peerA) && sess.peerB.equals(peerB))
                    .findFirst();
            Session session;
            if(maybeSession.isEmpty()) {
                session = new Session(peerA, peerB, co);
                ctx.sessions.add(session);
            }else{
                session = maybeSession.get();
                session.addLeafCommunicationRoots(new ArrayList<>(List.of(co)));
            }
            if(mustUpdateRecursiveCommunicationsEntrypoint){
                ctx.recursiveCommunicationsEntrypoint.get(lastVariable).put(sessionKey, new ArrayList<>(List.of(co)));
                ctx.lastVariable = null;
            }
            if(!comm.nextBehaviours.isEmpty()){
                fromBehaviour(comm.nextBehaviours.get(nextBehaviourKey), ctx);
            }
        }
    }

    public Set<String> getLabels(Utils.Direction dir) {
        Set<String> labels = new HashSet<>();
        for (Communication communicationsRoot : communicationsRoots) {
            labels.addAll(communicationsRoot.getDirectedLabels(dir));
        }
        return labels;
    }

    public static class SmallContext{
        public ArrayList<Session> sessions = new ArrayList<>();
        public Set<String> calledVariables = new HashSet<>();
        public String lastVariable = null;
        public HashMap<String, HashMap<String, ArrayList<Communication>>> recursiveCommunicationsEntrypoint = new HashMap<>();
        public SmallContext previousContext;
        public boolean mustUpdateRecursiveCommunicationsEntrypoint = false;
        public Communication currentDummy;

        public SmallContext(){};
        public SmallContext(
                ArrayList<Session> sessions, Set<String> calledVariables,
                HashMap<String, HashMap<String, ArrayList<Communication>>> recursiveCommunicationsEntrypoint,
                SmallContext previousContext){
            if(sessions != null) this.sessions = sessions;
            if(calledVariables != null) this.calledVariables = calledVariables;
            if(recursiveCommunicationsEntrypoint != null) this.recursiveCommunicationsEntrypoint =
                    recursiveCommunicationsEntrypoint;
            this.previousContext = previousContext;
        }
        public SmallContext(List<Session> sessions){
            this.sessions = new ArrayList<>(sessions);
        }
        public SmallContext(Session s){
            sessions.add(s);
        }
        public SmallContext(SmallContext previousContext){
            this.previousContext = previousContext;
        }

        public SmallContext duplicate(){
            var sm = new SmallContext();
            sm.sessions = new ArrayList<>(this.sessions);
            sm.calledVariables = new HashSet<>(this.calledVariables);
            sm.lastVariable = this.lastVariable;
            sm.recursiveCommunicationsEntrypoint = new HashMap<>();
            for (String s : recursiveCommunicationsEntrypoint.keySet()) {
                var hm = new HashMap<String, ArrayList<Communication>>();
                var hmsource = recursiveCommunicationsEntrypoint.get(s);
                for (String string : hmsource.keySet()) {
                    hm.put(s, new ArrayList<>(hmsource.get(string)));
                }
                sm.recursiveCommunicationsEntrypoint.put(s, hm);
            }
            sm.previousContext = previousContext;
            sm.mustUpdateRecursiveCommunicationsEntrypoint = mustUpdateRecursiveCommunicationsEntrypoint;
            return sm;
        }

        public boolean alreadyCalledVariable(String variable){
            if(calledVariables.contains(variable)) return true;
            return previousContext != null && previousContext.alreadyCalledVariable(variable);
        }

        public SmallContext mergeWithPrevious(){
            if(previousContext == null) return this;
            mergeSessionsVertical(previousContext.sessions, sessions);
            previousContext.calledVariables.addAll(calledVariables);
            return previousContext;
        }

        public Session getLastSession(String peerA, String peerB){
            var optionalSession = sessions.stream().filter(s -> s.peerB.equals(peerB) && s.peerA.equals(peerA)).findFirst();
            if(optionalSession.isPresent()) return optionalSession.get();
            if(previousContext == null) return null;
            return previousContext.getLastSession(peerA, peerB);
        }

        public boolean communicationIsPresentInPreviousContexts(Communication co){
            if(previousContext == null) return false;
            for (Session session : previousContext.sessions) {
                for (Communication communicationsRoot : session.communicationsRoots) {
                    if(communicationsRoot.nodeIsSelfOrBelow(co)) return true;
                }
            }
            return previousContext.communicationIsPresentInPreviousContexts(co);
        }

        public static SmallContext mergeHorizontalGeneral(
                ArrayList<SmallContext> ctxs,
                BiFunction<ArrayList<Session>, ArrayList<Session>, ArrayList<Session>> sessionsMerger){
            if(ctxs == null || ctxs.isEmpty()) return new SmallContext();

            //all mergeable contexts must have same previous context, else there is a problem;
            var resultContext = ctxs.getFirst();
            ctxs.removeFirst();
            for (SmallContext ctx : ctxs) {
                if(ctx.previousContext != resultContext.previousContext) return null;
            }
            // merge sessions
            resultContext = ctxs.stream().reduce(resultContext, (acc,it) -> {
                acc.sessions = sessionsMerger.apply(acc.sessions, it.sessions);
                return acc;
            });
            // merge calledVariables
            resultContext = ctxs.stream().reduce(resultContext, (acc, it) -> {
                acc.calledVariables.addAll(it.calledVariables);
                return acc;
            });
            // merge recursiveCommunicationsEntrypoint
            return resultContext;
        }

        public static SmallContext mergeHorizontalStub(ArrayList<SmallContext> ctxs){
            if(ctxs.size() < 2){
                var stub = new SmallContext();
                stub.previousContext = ctxs.getFirst().previousContext;
                ctxs.addFirst(stub);
            }
            return mergeHorizontalGeneral(ctxs, Session::mergeSessionsHorizontalStub);
        }

        public static SmallContext mergeHorizontal(ArrayList<SmallContext> ctxs){
            return mergeHorizontalGeneral(ctxs, Session::mergeSessionsHorizontal);
        }

        public String getLastCalledVariable() {
            return lastVariable != null ? lastVariable : (previousContext != null ? previousContext.lastVariable : null);
        }
    }
    public static ArrayList<Session> fromBehaviours(Map<String, Behaviour> behaviours){
        var sessions = new ArrayList<Session>();
        for (String s : behaviours.keySet()) {
            var behaviour = behaviours.get(s);
            var ctx = new SmallContext();
            fromBehaviour(behaviour, ctx);
            sessions.addAll(ctx.sessions);
        }
        return sessions;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Session comp)) return false;
        if (!hasSameEnds(comp)) return false;
        if (communicationsRoots.size() != comp.communicationsRoots().size()) return false;
        for (int i = 0; i < communicationsRoots().size(); i++) {
            if(!(communicationsRoots.containsAll(comp.communicationsRoots)
                    && comp.communicationsRoots.containsAll(communicationsRoots))) return false;
        }
        for (Communication communicationsRoot : communicationsRoots) {
            communicationsRoot.resetVisitedRecursiveBranches();
        }
        return true;
    }

    public void expandTopCommunicationRoots(ArrayList<Communication> roots){
        communicationsRoots.addAll(roots);
    }

    public void addLeafCommunicationRoots(ArrayList<Communication> roots) {
        if(communicationsRoots.isEmpty()){
            communicationsRoots.addAll(roots);
        }else{
            communicationsRoots.getFirst().addLeafCommunicationRoots(roots);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("\nSession[\n");
        s.append(String.format("\tpeerA=%s\n\tpeerB=%s\n\t[", peerA, peerB));
        for (Communication communicationsRoot : communicationsRoots) {
            s.append("\n\t\t").append(communicationsRoot.toString().replace("\n", "\n\t\t"));
        }
        return s.append("\n\t]\n]").toString();
    }

    public static ArrayList<Session> mergeSessionsVertical(ArrayList<Session> sessionsSuper, ArrayList<Session> sessions){
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
    public static ArrayList<Session> mergeSessionsHorizontal(ArrayList<Session> sessions1, ArrayList<Session> sessions2){
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
    public static ArrayList<Session> mergeSessionsHorizontalStub(ArrayList<Session> sessions1, ArrayList<Session> sessions2){
        var leftOnly = sessions1.stream()
                .filter(s1 -> sessions2.stream().noneMatch(s2 -> s2.hasSameEnds(s1)));
        var rightOnly = sessions2.stream()
                .filter(s2 -> sessions1.stream().noneMatch(s1 -> s1.hasSameEnds(s2)));
        var common = new ArrayList<>(sessions1.stream()
                .filter(s1 -> sessions2.stream().anyMatch(s2 -> s2.hasSameEnds(s1)))
                .peek(s1 -> {
                    var toAdd = sessions2.stream().filter(s2 -> s2.hasSameEnds(s1)).findFirst().get();
                    s1.expandTopCommunicationRoots(toAdd.communicationsRoots());
                })
                .toList());
        var lo = new ArrayList<>(leftOnly.peek(s -> s.expandTopCommunicationRoots(new ArrayList<>(List.of(
                new Communication(Utils.Direction.VOID)
        )))).toList());
        var ro = new ArrayList<>(rightOnly.peek(s -> s.expandTopCommunicationRoots(new ArrayList<>(List.of(
                new Communication(Utils.Direction.VOID)
        )))).toList());
        common.addAll(lo);
        common.addAll(ro);
        return common;
    }

    public ArrayList<Communication> getLeaves() {
        if(communicationsRoots.isEmpty()) return new ArrayList<>();
        return communicationsRoots.stream().map(Communication::getLeaves)
                //reduce List<ArrayList<Communication>> to ArrayList<Communication>
                .reduce(new ArrayList<>(), (acc, it) -> {acc.addAll(it); return acc;});
    }

    public boolean supports(Session target) {
        boolean oneCompatiblePath;
        for (Communication targetNode : target.communicationsRoots) {
            oneCompatiblePath = false;
            for (Communication node : communicationsRoots) {
                if(node.supports(targetNode)){
                    oneCompatiblePath = true;
                    break;
                }
            }
            if(!oneCompatiblePath) return false;
        }
        return true;
    }

    public void cleanVoid() {
        for (Communication communicationsRoot : communicationsRoots) {
            communicationsRoot.cleanVoid();
        }
    }
}