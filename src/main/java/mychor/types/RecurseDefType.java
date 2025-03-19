package mychor.types;

import mychor.MessageQueues;

import java.util.HashMap;

public class RecurseDefType extends LocalType{
    String varName;
    public RecurseDefType(String varName, LocalType continuation){
        this.varName = varName;
        nextTypes.put("unfold", continuation);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RecurseDefType rdt)) return false;
        return this.nextTypes.get("unfold").equals(rdt.nextTypes.get("unfold"));
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        return nextTypes.get("unfold").reduce(process, mqs);
    }

    @Override
    public LocalType duplicate() {
        return new RecurseDefType(varName, nextTypes.get("unfold"));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("µ ").append(varName).append(".").append(" ").append(nextTypes.get("unfold").toString());
        return b.toString();
    }
}
