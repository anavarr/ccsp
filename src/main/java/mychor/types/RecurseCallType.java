package mychor.types;

import mychor.MessageQueues;

import java.util.HashMap;

public class RecurseCallType extends LocalType{
    String name;
    public RecurseCallType(String name){
        this.name = name;
        this.nextTypes = new HashMap<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecurseCallType rct)) return false;
        return this.name.equals(rct.name);
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        return new EndType();
    }

    @Override
    public String toString() {
        return name;
    }
}
