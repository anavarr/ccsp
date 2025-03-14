package mychor.types;

import java.util.HashMap;

public class RecurseDefType {
    String varName;
    HashMap<String, LocalType> nextTypes = new HashMap<>();
    public RecurseDefType(String varName, LocalType continuation){
        this.varName = varName;
        nextTypes.put("unfold", continuation);
    }
}
