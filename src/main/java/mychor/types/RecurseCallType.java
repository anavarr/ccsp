package mychor.types;

import mychor.MessageQueues;

import java.util.Collection;
import java.util.HashMap;

public class RecurseCallType extends LocalType{
    String name;
    RecurseDefType origin;
    int visited;
    int toVisit = 26;
    public RecurseCallType(String name, RecurseDefType origin){
        this.name = name;
        this.nextTypes = new HashMap<>();
        this.origin = origin;
        this.nextTypes.put("unfold", origin);
        this.visited = 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecurseCallType rct)) return false;
        return this.name.equals(rct.name);
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        this.visited++;
        if(visited%2 == 0) return this;
        if(visited < toVisit) {
            var node = origin.reduceReset(process, mqs);
            if(node.equals(new EndType())) return new EndType();
            return this;
        }
        return new EndType();
    }

    @Override
    public LocalType duplicate() {
        return this;
    }

    @Override
    protected LocalType extractParticipation(String process) {
        var rct = new RecurseCallType(this.name, this.origin);
        rct.visited = this.visited;
        return rct;
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return true;
    }

    @Override
    protected LocalType duplicateReset() {
        return this;
    }

    @Override
    public String toString() {
        return name;
    }


}
