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

    public String getDestination() {
        return destination;
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
    public ArrayList<LocalType> getUnvisitedNodes() {
        var l = nextTypes.get(";").getUnvisitedNodes();
        if(visited == 0) l.add(this);
        return l;
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
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return nextTypes.get(";").knowlegdgeOfChoice(processes);
    }
}
