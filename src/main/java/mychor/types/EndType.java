package mychor.types;

import mychor.End;
import mychor.MessageQueues;

import java.util.HashMap;

public class EndType extends LocalType{
    public EndType(){
        this.nextTypes = new HashMap<>();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EndType;
    }

    @Override
    public String toString() {
        return "end";
    }

    @Override
    public LocalType reduce(String pr, MessageQueues mqs) {
        return this;
    }
}
