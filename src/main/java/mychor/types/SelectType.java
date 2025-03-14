package mychor.types;

import java.util.HashMap;

public class SelectType extends LocalType {
    public SelectType(HashMap<String, LocalType> branches){
        for (String s : branches.keySet()) {
            nextTypes.put(s, branches.get(s));
        }
    }

    public SelectType(){

    }

    public SelectType(String label, LocalType next){
        nextTypes.put(label, next);
    }

    public void addLabel(String label, LocalType next){
        nextTypes.put(label, next);
    }
}
