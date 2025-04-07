package mychor.types;

import mychor.MessageQueues;

import java.util.Collection;
import java.util.List;

public class RecurseDefType extends LocalType{
    String varName;
    int visited;
    public boolean endState = false;
    LocalType visiting = null;

    public List<String> getInvolvedProcesses(){
        return nextTypes.get("unfold").getInvolvedProcesses();
    }

    @Override
    public boolean visitedAllPaths() {
        return nextTypes.get("unfold").visitedAllPaths();
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
    public boolean equals(Object obj) {
        if(!(obj instanceof RecurseDefType rdt)) return false;
        return this.nextTypes.get("unfold").equals(rdt.nextTypes.get("unfold"));
    }

    @Override
    protected LocalType getLeaf() {
        return nextTypes.get("unfold").getLeaf();
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        if(visiting != null) {
            visiting = visiting.reduce(process, mqs);
        }
        else{
            visited ++;
            visiting = nextTypes.get("unfold").reduce(process, mqs);
        }
        var leaf = nextTypes.get("unfold").getLeaf();
        if(leaf == this) visiting = null;
        if(leaf instanceof EndType) {
            endState = true;
            visiting = null;
        }
        return this;
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
    protected LocalType softReset() {
        nextTypes.get("unfold").softReset();
        return this;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("µ ").append(varName).append(".").append(" ").append(nextTypes.get("unfold").toString());
        return b.toString();
    }

    public LocalType reduceReset(String process, MessageQueues mqs) {
        return nextTypes.get("unfold").softReset().reduce(process, mqs);
    }
}
