package mychor;

import java.util.ArrayList;
import java.util.List;
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

    public boolean areEnds(String peerAP, String peerBP){
        return (peerA.equals(peerAP) || peerA.equals(peerBP)) & (peerB.equals(peerAP) || peerB.equals(peerBP));
    }

    public void displayCommunications(String prefix){
        for (Communication communicationsRoot : communicationsRoots) {
            communicationsRoot.displayCommunications(prefix);
        }
    }

    public boolean isSelfComm(){
        return peerA.equals(peerB);
    }

    public int getCommunicationsSize(){
        int c = 0;
        for (Communication communicationsRoot : communicationsRoots) {
           c+=communicationsRoot.getBranchesDepth();
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
        if(!getInitiated().equals(comp.getInitiated())){
            System.err.println("not the same initiated");
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
        if(communicationsRoots.size() == 0) return true;
        //if the branches are all SELECT, then anything following is valid
        var allSelect = communicationsRoots.stream()
                .allMatch(item -> item.direction() == Utils.Direction.SELECT);
        //if the branches are all BRANCH, then anything following is valid
        var allBranch = communicationsRoots.stream()
                .allMatch(item -> item.direction() == Utils.Direction.BRANCH);
        //else, branching is valid if all branches are the same
        var allSame = communicationsRoots.stream()
                .allMatch(item -> item.isEqual(communicationsRoots.get(0)));
        if(!(allSame || allSelect || allBranch)) return false;
        return communicationsRoots.stream().allMatch(Communication::isBranchingValid);
    }

    public boolean hasSameEnds(Session comp) {
        return peerA.equals(comp.peerA()) && peerB.equals(comp.peerB());
    }

    public boolean isEqual(Session comp) {
        if (!hasSameEnds(comp)) return false;
        if (communicationsRoots.size() != comp.communicationsRoots().size()) return false;
        for (int i = 0; i < communicationsRoots().size(); i++) {
            if(!communicationsRoots.get(i).isEqual(comp.communicationsRoots.get(i))) return false;
        }
        return true;
    }

    public void expandTopCommunicationRoots(ArrayList<Communication> roots){
        communicationsRoots.addAll(roots);
    }

    public void addLeafCommunicationRoots(ArrayList<Communication> roots) {
        if(communicationsRoots.size() == 0){
            communicationsRoots.addAll(roots);
        }else{
            communicationsRoots.get(0).addLeafCommunicationRoots(roots);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("\nSession[\n");
        s.append(String.format("\tpeerA=%s\n\tpeerB=%s\n\tcommunicationsRoots=[", peerA, peerB));
        for (Communication communicationsRoot : communicationsRoots) {
            s.append("\n\t\t").append(communicationsRoot.toString().replace("\n", "\n\t\t"));
        }
        return s.append("\n\t]\n]").toString();
    }
}