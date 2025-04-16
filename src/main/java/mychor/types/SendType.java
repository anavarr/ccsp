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
        visited++;
        mqs.add(Utils.Direction.SEND, pr, destination, null);
        if(nextTypes.get(";") instanceof RecurseCallType rct) return rct.reduce(pr, mqs);
        return nextTypes.get(";");
    }

    @Override
    public boolean visitedAllPaths() {
        if(visited == 0) return false;
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
        if(this == lt) return true;
        return nextTypes.get(";").contains(lt);
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
        var toSend = new HashMap<String, ArrayList<String>>();
        toSend.put(destination, new ArrayList<>(List.of("")));

        var hm = nextTypes.get(";").getMsgsToSendRecursive();
        if(hm != null){
            for (String s : hm.keySet()) {
                if(toSend.containsKey(s)) toSend.get(s).addAll(hm.get(s));
                else toSend.put(s, hm.get(s));
            }
        }
        return toSend;
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
