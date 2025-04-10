package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SendType extends LocalType{
    String destination;
    public SendType(String destination, LocalType next){
        this.destination = destination;
        nextTypes.put(";", next);
    }

    @Override
    public boolean equivalent(Object obj) {
        if(!(obj instanceof SendType)) return false;
        return nextTypes.get(";").equivalent(((SendType) obj).nextTypes.get(";"));
    }

    @Override
    public String toString() {
        return destination+"!;"+nextTypes.get(";").toString();
    }

    @Override
    public LocalType reduce(String pr, MessageQueues mqs) {
        mqs.add(Utils.Direction.SEND, pr, destination, null);
        return nextTypes.get(";");
    }

    @Override
    public boolean visitedAllPaths() {
        return nextTypes.get(";").visitedAllPaths();
    }

    @Override
    public LocalType duplicate() {
        return new SendType(destination, nextTypes.get(";"));
    }

    @Override
    protected LocalType extractParticipation(String process) {
        if(destination.equals(process)) return new SendType(destination, nextTypes.get(";").extractParticipation(process));
        return nextTypes.get(";").extractParticipation(process);
    }


    @Override
    public HashMap<String, ArrayList<String>> getMsgsToReceiveRecursive() {
        return nextTypes.get(";").getMsgsToSendRecursive();
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToSendRecursive() {
        var hm = nextTypes.get(";").getMsgsToSendRecursive();
        if(hm == null){
            hm = new HashMap<>();
            hm.put(destination, new ArrayList<>(List.of("")));
        }else{
            if(hm.containsKey(destination)) hm.get(destination).add("");
            else hm.put(destination, new ArrayList<>(List.of("")));
        }
        return hm;
    }

    @Override
    public PossibleMessages getMsgsToReceive(String name) {
        return null;
    }

    @Override
    public PossibleMessages getMsgsToSend(String name) {
        return new PossibleMessages(name, destination, List.of(""));
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return nextTypes.get(";").knowlegdgeOfChoice(processes);
    }
}
