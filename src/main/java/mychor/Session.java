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

    public void expandCommunicationRoots(ArrayList<Communication> roots){
        communicationsRoots.addAll(roots);
    }

    public void addLeafCommunicationRoot(Communication communication) {
        if(communicationsRoots.size() == 0){
            communicationsRoots.add(communication);
        }else{
            communicationsRoots.get(0).addLeafCommunication(communication);
        }
    }

    public void addLeafCommunicationRoots(ArrayList<Communication> communications) {
        if(communicationsRoots.size() == 0){
            communicationsRoots.addAll(communications);
        }else{
            communicationsRoots.get(0).addLeafCommunications(communications);
        }
    }

    public void expandLeafCommunicationRoots(ArrayList<Communication> communicationsRoots) {
        if(communicationsRoots.size() == 0){
            expandCommunicationRoots(communicationsRoots);
        }else{
            if(!communicationsRoots.get(0).expandLeafCommunicationRoots(communicationsRoots)){
                communicationsRoots.addAll(communicationsRoots);
            }
        }
    }
}