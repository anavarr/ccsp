package mychor.types;

import mychor.MessageQueues;

import java.util.Collection;

public class RecurseDefType extends LocalType{
    String varName;
    int visited;
    LocalType visiting = null;
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
    public LocalType reduceOnce(String process, MessageQueues mqs) {
        return nextTypes.get("unfold").reduceOnce(process, mqs);
    }

    @Override
    public LocalType reduceNoRec(String process, MessageQueues mqs) {
        return nextTypes.get("unfold").reduceNoRec(process, mqs);
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        if(visited > 1000) {
            return this;
        }
        if(visiting != null) {
            if(visiting instanceof EndType) return visiting;
            var tmp = visiting.reduce(process, mqs);
            if(tmp != this) visiting = tmp;
            else visiting = null;
        }
        else{
            visited ++;
            visiting = nextTypes.get("unfold").reduce(process, mqs);
            if(visiting == this) visiting = null;
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
    public void hardReset() {
        this.visited = 0;
        nextTypes.get("unfold").hardReset();
    }

    @Override
    protected LocalType softReset() {
        nextTypes.get("unfold").hardReset();
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
