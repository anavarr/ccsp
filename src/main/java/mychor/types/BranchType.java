package mychor.types;

import java.util.HashMap;

public class BranchType extends LocalType{
    public BranchType(HashMap<String, LocalType> branches){
        for (String s : branches.keySet()) {
            this.nextTypes.put(s, branches.get(s));
        }
    }
}