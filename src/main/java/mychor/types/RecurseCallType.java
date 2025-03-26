package mychor.types;

import mychor.MessageQueues;

import java.util.Collection;
import java.util.HashMap;

public class RecurseCallType extends LocalType{
    String name;
    LocalType origin;
    int visited;
    int toVisit = 10;
    public RecurseCallType(String name, LocalType origin){
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
        visited++;
        if(visited < toVisit) return origin.reduce(process, mqs);
        return new EndType();
    }

    @Override
    public LocalType duplicate() {
        return new RecurseCallType(name, this.origin);
    }

    @Override
    protected LocalType extractParticipation(String process) {
        return new RecurseCallType(this.name, this.origin);
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
