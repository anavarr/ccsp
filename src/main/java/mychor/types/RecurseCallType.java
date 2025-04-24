package mychor.types;

import mychor.MessageQueues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RecurseCallType extends LocalType{
    String name;
    RecurseDefType origin;
    public RecurseCallType(String name, RecurseDefType origin){
        this.name = name;
        this.nextTypes = new HashMap<>();
        this.origin = origin;
        this.nextTypes.put("unfold", origin);
    }

    @Override
    public boolean equivalent(Object obj) {
        if (!(obj instanceof RecurseCallType rct)) return false;
        return this.name.equals(rct.name);
    }

    public RecurseDefType getOrigin() {
        return origin;
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        visited++;
        return origin;
    }

    @Override
    public boolean visitedAllPaths() {
        if(visited == 0) return false;
        return true;
    }

    @Override
    public ArrayList<LocalType> getUnvisitedNodes() {
        if(visited == 0) return new ArrayList<>(List.of(this));
        return new ArrayList<>();
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
    public LocalType duplicate() {
        return this;
    }

    @Override
    protected LocalType extractParticipation(String process) {
        var rct = new RecurseCallType(this.name, this.origin);
        return rct;
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return true;
    }

    @Override
    public String toString() {
        return name;
    }

}
