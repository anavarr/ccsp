package mychor.types;

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
}
