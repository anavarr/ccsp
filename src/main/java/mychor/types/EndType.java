package mychor.types;

import mychor.MessageQueues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class EndType extends LocalType{
    public EndType(){
        this.nextTypes = new HashMap<>();
    }

    @Override
    public boolean equivalent(Object obj) {
        return obj instanceof EndType;
    }

    @Override
    public String toString() {
        return "end";
    }

    @Override
    public LocalType reduce(String pr, MessageQueues mqs) {
        visited++;
        return this;
    }

    @Override
    public LocalType duplicate() {
        return new EndType();
    }

    @Override
    protected LocalType extractParticipation(String process) {
        return new EndType();
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return true;
    }

    @Override
    public boolean visitedAllPaths() {
        if(visited == 0) return false;
        return true;
    }

    @Override
    public List<String> getSelectionsToVisit() {
        return List.of();
    }

    @Override
    public List<String> getBranchesToVisit() {
        return List.of();
    }

    @Override
    public HashMap<BranchType, List<String>> getBranchingNodesAndLabelsToVisit() {
        return null;
    }

    @Override
    public HashMap<SelectType, List<String>> getSelectionNodesAndLabelsToVisit() {
        return null;
    }

    @Override
    public boolean contains(LocalType lt) {
        return lt == this;
    }


    @Override
    public HashMap<String, ArrayList<String>> getMsgsToReceiveRecursive() {
        return null;
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToSendRecursive() {
        return null;
    }

    @Override
    public PossibleMessages getMsgsToReceive(String name) {
        return null;
    }

    @Override
    public PossibleMessages getMsgsToSend(String name) {
        return null;
    }
}
