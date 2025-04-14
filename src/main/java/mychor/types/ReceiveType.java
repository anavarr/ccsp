package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ReceiveType extends LocalType{
    String destination;
    public ReceiveType(String destination, LocalType next){
        this.destination = destination;
        nextTypes.put(";", next);
    }

    @Override
    public boolean visitedAllPaths() {
        return nextTypes.get(";").visitedAllPaths();
    }

    @Override
    public List<String> getSelectionsToVisit() {
        return nextTypes.get(";").getSelectionsToVisit();
    }

    @Override
    public List<String> getBranchesToVisit() {
        return nextTypes.get(";").getBranchesToVisit();
    }

    @Override
    public HashMap<BranchType, List<String>> getBranchingNodesAndLabelsToVisit() {
        return nextTypes.get(";").getBranchingNodesAndLabelsToVisit();
    }

    @Override
    public HashMap<SelectType, List<String>> getSelectionNodesAndLabelsToVisit() {
        return nextTypes.get(";").getSelectionNodesAndLabelsToVisit();
    }

    @Override
    public boolean contains(LocalType lt) {
        if(lt == this) return true;
        return nextTypes.get(";").contains(lt);
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToReceiveRecursive() {
        HashMap<String, ArrayList<String>> toReceive = new HashMap<>();
        toReceive.put(destination, new ArrayList<>(List.of("")));

        var hm = nextTypes.get(";").getMsgsToReceiveRecursive();
        if(hm != null){
            for (String s : hm.keySet()) {
                if(toReceive.containsKey(s)) toReceive.get(s).addAll(hm.get(s));
                else toReceive.put(s, hm.get(s));
            }
        }
        return toReceive;
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToSendRecursive() {
        return nextTypes.get(";").getMsgsToSendRecursive();
    }

    @Override
    public PossibleMessages getMsgsToReceive(String name) {
        return new PossibleMessages(destination, name, List.of(""));
    }

    @Override
    public PossibleMessages getMsgsToSend(String name) {
        return null;
    }

    @Override
    public boolean equivalent(Object obj) {
        if(!(obj instanceof ReceiveType rt)) return false;
        if(!this.destination.equals(rt.destination)) return false;
        return nextTypes.get(";").equivalent(((ReceiveType) obj).nextTypes.get(";"));
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        var msg = mqs.poll(destination, process);
        // maybe the message didn't arrive yet
        if(msg == null) return this;
        // the sender sent a label it was not supposed to send beforehand
        // it is not valid
        if(!msg.direction().equals(Utils.Direction.SEND)) throw new RuntimeException(
                        "A receive action is expected, the queue "+process+"-"+destination+
                                " contains a labeled selection, type is not valid"
       );
        // we got the right message, this communication can reduce
        if(nextTypes.get(";") instanceof RecurseCallType rct) return rct.reduce(process, mqs);
        return nextTypes.get(";");
    }

    @Override
    public LocalType duplicate() {
        return new ReceiveType(destination, nextTypes.get(";"));
    }

    @Override
    protected LocalType extractParticipation(String process) {
        if(destination.equals(process)) return new ReceiveType(destination, nextTypes.get(";").extractParticipation(process));
        return nextTypes.get(";").extractParticipation(process);
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return nextTypes.get(";").knowlegdgeOfChoice(processes);
    }

    @Override
    public String toString() {
        return destination+"?;"+nextTypes.get(";").toString();
    }

    public String getDestination() {
        return destination;
    }
}
