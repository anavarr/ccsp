package mychor.types;

import mychor.MessageQueues;

import java.util.Collection;
import java.util.HashMap;

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
    public boolean equals(Object obj) {
        if (!(obj instanceof RecurseCallType rct)) return false;
        return this.name.equals(rct.name);
    }

    public RecurseDefType getOrigin() {
        return origin;
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        return origin;
    }

    @Override
    public boolean visitedAllPaths() {
        return true;
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
