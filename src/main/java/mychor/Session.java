package mychor;

import java.util.List;
import java.util.Optional;

public record Session(String peerA, String peerB, List<Communication> communications) {
    public Optional<String> getInitiator(){
        return communications.size() > 0 ?
                Optional.of(communications.get(0).isSend() ? peerA : peerB) :
                Optional.empty();
    }

    public Optional<String> getInitiated(){
        return communications.size() > 0 ?
                Optional.of(communications.get(0).isReceive() ? peerA : peerB) :
                Optional.empty();
    }

    public Optional<Boolean> isSingle(int index){
        return communications.size() >= (index-1) ?
                Optional.of(communications.get(index).isSingle()):
                Optional.empty();
    }
}