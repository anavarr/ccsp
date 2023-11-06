package mychor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record Session(String peerA, String peerB, List<Communication> communications) {
    public Session {
        Objects.requireNonNull(peerA, "peerA cannot be null");
        Objects.requireNonNull(peerB, "peerB cannot be null");
        Objects.requireNonNull(communications, "communications list cannot be null");
    }
    public Optional<String> getInitiator(){
        return (communications != null & communications.size() > 0) ?
                Optional.of(communications.get(0).isSend() ? peerA : peerB) :
                Optional.empty();
    }

    public Optional<String> getInitiated(){
        return (communications != null & communications.size() > 0) ?
                Optional.of(communications.get(0).isReceive() ? peerA : peerB) :
                Optional.empty();
    }

    public Optional<Boolean> isSingle(int index){
        return (communications != null & communications.size() >= (index-1)) ?
                Optional.of(communications.get(index).isSingle()):
                Optional.empty();
    }

    public boolean areEnds(String peerAP, String peerBP){
        return (peerA == peerAP || peerA == peerBP) & (peerB == peerAP || peerB == peerBP);
    }

    public boolean isComplementary(Session comp){
        if (!areEnds(comp.peerA, comp.peerB)){
            System.out.println("not the same peers");
            return false;
        }
        if(!getInitiator().equals(comp.getInitiator())){
            System.out.println("not the same initiators");
            return false;
        }
        if(!getInitiated().equals(comp.getInitiated())){
            System.out.println("not the same initiated");
            return false;
        }
        if (communications.size() != comp.communications.size()){
            System.out.println("not the same communications size");
            return false;
        }
        Communication c1;
        Communication c2;
        for (int i = 0; i< communications.size(); i++){
            c1 = communications.get(i);
            c2 = comp.communications.get(i);
            if (!(c1.isSend() ? (c2.isReceive()) : (c2.isSend()))){
                System.out.println("not complementary communications");
                return false;
            }
        }
        return true;
    }
}