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

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SelectType st)) return false;
        // if they don't have the exact same labels
        if(!(st.nextTypes.keySet().containsAll(this.nextTypes.keySet()) &
                this.nextTypes.keySet().containsAll(st.nextTypes.keySet()))) return false;
        for (String s : nextTypes.keySet()) {
            if(!nextTypes.get(s).equals(st.nextTypes.get(s))) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("+ {\n");
        short counter = 1;
        for (String s : nextTypes.keySet()) {
            b.append("\t").append(s).append(" : ").append(nextTypes.get(s).toString());
            if(counter < nextTypes.keySet().size()){
                b.append(",\n");
            }
            counter ++;
        }
        b.append("\n}");
        return b.toString();
    }
}
