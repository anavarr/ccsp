package mychor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        for (int i = 0; i < communicationsRoots.size(); i++) {
            if(!communicationsRoots.get(i).isComplementary(comp.communicationsRoots.get(i))){
                return false;
            }
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

            }
            case Comm comm -> {
                var peerA = comm.process;
                var peerB = comm.destination;
                var direction = comm.direction;

                Session session;
                var maybeSession = ctx.sessions.stream()
                        .filter(sess-> sess.peerA.equals(peerA) && sess.peerB.equals(peerB))
                        .findFirst();
                if(direction.equals(Utils.Direction.SELECT) || direction.equals(Utils.Direction.BRANCH)){
                    //There can be several nextNodes
                    var ctxs = new ArrayList<SmallContext>();
                    var oldCtx=ctx;
                    for (String label : comm.nextBehaviours.keySet()) {
                        var newCtx = ctx.duplicateWithoutSession();
                        Communication co = new Communication(direction, label);
                        session = new Session(peerA, peerB, co);
                        newCtx.sessions.add(session);
                        fromBehaviour(b.nextBehaviours.get(label), newCtx);
                        ctxs.add(newCtx);
                    }
                    ctx = ctxs.stream().reduce(new SmallContext(), (acc, it) -> {
                        mergeSessionsHorizontal(acc.sessions, it.sessions);
                        return acc;
                    });
                    mergeSessionsVertical(oldCtx.sessions, ctx.sessions);
                    ctx = oldCtx;
                }else{
                    Communication co = new Communication(direction);
                    if(maybeSession.isEmpty()) {
                        session = new Session(peerA, peerB, co);
                        ctx.sessions.add(session);
                    }else{
                        session = maybeSession.get();
                        session.addLeafCommunicationRoots(new ArrayList<>(List.of(co)));
                    }
                    if(!comm.nextBehaviours.isEmpty()){
                        fromBehaviour(comm.nextBehaviours.get(";"), ctx);
                    }
                }
            }
            case Cdt cdt -> {
                var oldCtx = ctx;
                var ctxs = new ArrayList<SmallContext>();
                for (String s : cdt.nextBehaviours.keySet()) {
                    var newCtx = new SmallContext();
                    fromBehaviour(cdt.nextBehaviours.get(s), newCtx);
                    ctxs.add(newCtx);
                }
                ctx = ctxs.stream().reduce(new SmallContext(), (acc, it) -> {
                    mergeSessionsHorizontal(acc.sessions, it.sessions);
                    return acc;
                });
                mergeSessionsVertical(oldCtx.sessions, ctx.sessions);
            }
            case End end -> {

            }
            case None none -> {

            }
            case Call call -> {

            }
            default -> throw new IllegalStateException("Unexpected value: " + b);
        }
    }

    static class SmallContext{
        ArrayList<Session> sessions = new ArrayList<>();
        ArrayList<String> calledVariables = new ArrayList<>();

        public SmallContext duplicateWithoutSession(){
            var sm = new SmallContext();
            sm.calledVariables.addAll(this.calledVariables);
            return sm;
        }
    }
    public static List<Session> fromBehaviours(Map<String, Behaviour> behaviours){
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
        return true;
    }

    public void expandTopCommunicationRoots(ArrayList<Communication> roots){
        communicationsRoots.addAll(roots);
    }

    public void addLeafCommunicationRoots(ArrayList<Communication> roots) {
        if(communicationsRoots.isEmpty()){
            communicationsRoots.addAll(roots);
        }else{
            communicationsRoots.get(0).addLeafCommunicationRoots(roots);
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

        common.addAll(new ArrayList<>(leftOnly.peek(s -> s.expandTopCommunicationRoots(
                new ArrayList<>(
                        List.of(
                                new Communication(
                                        Utils.Direction.VOID,
                                        new ArrayList<>())
                        )
                )
        )).toList()));
        common.addAll(new ArrayList<>(rightOnly.peek(s -> s.expandTopCommunicationRoots(
                new ArrayList<>(
                        List.of(
                                new Communication(
                                        Utils.Direction.VOID,
                                        new ArrayList<>())
                        )
                )
        )).toList()));
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