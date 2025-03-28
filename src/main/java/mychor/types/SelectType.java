package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SelectType extends LocalType {
    String destination;
    private List<String> visitedLabels = new ArrayList<>();
    private ArrayList<String> finishedBranches = new ArrayList<>();
    private String visitingLabel = null;
    private LocalType visitingBranch = null;
    private HashMap<String, Integer> visitStats = new HashMap<>();

    public SelectType(String destination, HashMap<String, LocalType> branches){
        this.destination = destination;
        for (String s : branches.keySet()) {
            nextTypes.put(s, branches.get(s));
            visitStats.put(s, 0);
        }

    }

    public SelectType(String destination){
        this.destination = destination;
    }

    public void addLabel(String label, LocalType next){
        nextTypes.put(label, next);
        visitStats.put(label, 0);
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
        b.append(destination);
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
        var label = getLeastVisitedLabel();
        if(label == null) return new EndType();
        visitedLabels.add(label);
        visitStats.put(label, visitStats.get(label)+1);
        visitingLabel = label;
        mqs.add(Utils.Direction.SELECT, pr, destination, label);
        visitingBranch = nextTypes.get(label);
        return updateVisitingBranch(pr, mqs);
    }

    private String getLeastVisitedLabel() {
        if(visitStats.isEmpty()) return null;
        var smallestValue = visitStats.entrySet().stream().findAny().get();
        for (Map.Entry<String, Integer> stringIntegerEntry : visitStats.entrySet()) {
            if(stringIntegerEntry.getValue() < smallestValue.getValue()) smallestValue = stringIntegerEntry;
        }
        return smallestValue.getKey();
    }

    @Override
    public LocalType duplicate() {
        var s = new SelectType(destination, nextTypes);
        s.nextTypes.replaceAll((k, v) -> s.nextTypes.get(k).duplicate());
        if(visitingBranch != null) {
            if(visitingBranch.equals(this)){
                s.visitingBranch = new SelectType(destination, nextTypes);
            }else{
                s.visitingBranch = visitingBranch.duplicate();
            }
        }
        s.visitedLabels.addAll(visitedLabels);
        s.visitingLabel = visitingLabel;
        s.visitStats = visitStats;
        return s;
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        HashMap<String, LocalType> branches = new HashMap<>();
        for (String s : nextTypes.keySet()) {
            for (String process : processes) {
                try{
                    if(branches.containsKey(process)) {
                        var lt = branches.get(s).extractParticipation(process);
                        return branches.get(process).equals(lt);
                    }else{
                        branches.put(process, nextTypes.get(s).extractParticipation(process));
                    }
                }catch(Exception e){
                    return false;
                }
            }
        }
        for (String s : nextTypes.keySet()) {
            if(nextTypes.get(s).knowlegdgeOfChoice(processes)) return false;
        }
        return true;
    }

    @Override
    protected LocalType duplicateReset() {
        var st = new SelectType(destination, nextTypes);
        visitedLabels.remove(st.visitingLabel);
        visitingLabel = null;
        return this;
    }

    private void removeBranch(String s) {
        nextTypes.remove(s);
        visitStats.remove(s);
    }

    @Override
    protected LocalType extractParticipation(String process) {
        if(destination.equals(process)) return new SelectType(destination);
        var branches = new ArrayList<LocalType>();
        nextTypes.forEach((pr, type) -> {
            branches.add(type.extractParticipation(process));
        });
        var allSame = branches.stream().noneMatch(el -> !el.equals(branches.getFirst()));
        if(allSame) return branches.getFirst();
        else throw new RuntimeException("execution branches don't match");
    }

    private LocalType updateVisitingBranch(String pr, MessageQueues mqs){
        visitingBranch = visitingBranch.reduce(pr, mqs);
        if(visitingBranch.equals(new EndType())){
            finishedBranches.add(visitingLabel);
            visitStats.remove(visitingLabel);
            visitingLabel = null;
            if(visitedLabels.containsAll(nextTypes.keySet())) return new EndType();
        }
        return this;
    }
}
