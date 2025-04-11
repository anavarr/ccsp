package mychor.types;

import mychor.MessageQueues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RecurseDefType extends LocalType{
    String varName;
    int visited;
    LocalType visiting = null;
    @Override
    public boolean visitedAllPaths() {
        return nextTypes.get("unfold").visitedAllPaths();
    }

    @Override
    public List<String> getSelectionsToVisit() {
        return nextTypes.get("unfold").getSelectionsToVisit();
    }

    @Override
    public List<String> getBranchesToVisit() {
        return nextTypes.get("unfold").getBranchesToVisit();
    }

    @Override
    public HashMap<BranchType, List<String>> getBranchingNodesAndLabelsToVisit() {
        return nextTypes.get("unfold").getBranchingNodesAndLabelsToVisit();
    }

    @Override
    public HashMap<SelectType, List<String>> getSelectionNodesAndLabelsToVisit() {
        return nextTypes.get("unfold").getSelectionNodesAndLabelsToVisit();
    }

    @Override
    public boolean contains(LocalType lt) {
        if(lt == this) return true;
        return nextTypes.get("unfold").contains(lt);
    }

    public int getVisited(){
        return visited;
    }

    public RecurseDefType(String varName, LocalType continuation){
        this.varName = varName;
        nextTypes.put("unfold", continuation);
    }
    public RecurseDefType(String varName){
        this.varName = varName;
    }

    public void addUnfolding(LocalType continuation){
        nextTypes.put("unfold", continuation);
    }

    @Override
    public boolean equivalent(Object obj) {
        if(!(obj instanceof RecurseDefType rdt)) return false;
        return this.nextTypes.get("unfold").equivalent(rdt.nextTypes.get("unfold"));
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        return nextTypes.get("unfold");
    }

    @Override
    public LocalType duplicate() {
        return new RecurseDefType(varName, nextTypes.get("unfold"));
    }

    @Override
    protected LocalType extractParticipation(String process) {
        return nextTypes.get("unfold").extractParticipation(process);
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return nextTypes.get("unfold").knowlegdgeOfChoice(processes);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("µ ").append(varName).append(".").append(" ").append(nextTypes.get("unfold").toString());
        return b.toString();
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToReceiveRecursive() {
        return nextTypes.get("unfold").getMsgsToReceiveRecursive();
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToSendRecursive() {
        return nextTypes.get("unfold").getMsgsToSendRecursive();
    }

    @Override
    public PossibleMessages getMsgsToReceive(String name) {
        return nextTypes.get(";").getMsgsToReceive(name);
    }

    @Override
    public PossibleMessages getMsgsToSend(String name) {
        return nextTypes.get(";").getMsgsToSend(name);
    }
}
