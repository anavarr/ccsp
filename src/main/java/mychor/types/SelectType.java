package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SelectType extends LocalType {
    String destination;
    private List<String> visitedLabels = new ArrayList<>();
    private String visitingLabel = null;
    private LocalType visitingBranch = null;

    public SelectType(String destination, HashMap<String, LocalType> branches){
        this.destination = destination;
        for (String s : branches.keySet()) {
            nextTypes.put(s, branches.get(s));
        }
    }

    public SelectType(String destination){
        this.destination = destination;
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

    @Override
    public LocalType reduce(String pr, MessageQueues mqs) {
        if(visitingLabel != null){
            return updateVisitingBranch(pr, mqs);
        }
        for (String s : nextTypes.keySet()) {
            if(!visitedLabels.contains(s)){
                visitedLabels.add(s);
                visitingLabel = s;
                mqs.add(Utils.Direction.SELECT, pr, destination, s);
                visitingBranch = nextTypes.get(s);
                return updateVisitingBranch(pr, mqs);
            }
        }
        throw new RuntimeException("The selection can't be reduced");
    }

    private LocalType updateVisitingBranch(String pr, MessageQueues mqs){
        visitingBranch = visitingBranch.reduce(pr, mqs);
        if(visitingBranch.equals(new EndType())){
            visitingLabel = null;
            if(visitedLabels.containsAll(nextTypes.keySet())) return new EndType();
        }
        return this;
    }
}
