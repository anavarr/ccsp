package mychor.types;

import java.util.HashMap;

public class BranchType extends LocalType{
    public BranchType(HashMap<String, LocalType> branches){
        for (String s : branches.keySet()) {
            this.nextTypes.put(s, branches.get(s));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof BranchType bt)) return false;
        // if they don't have the exact same labels
        if(!(bt.nextTypes.keySet().containsAll(this.nextTypes.keySet()) &
                this.nextTypes.keySet().containsAll(bt.nextTypes.keySet()))) return false;
        for (String s : nextTypes.keySet()) {
            if(!nextTypes.get(s).equals(bt.nextTypes.get(s))) return false;
        }
        return true;
    }
}