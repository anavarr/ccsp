package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class BranchType extends LocalType{
    String destination;

    private List<String> visitedLabels = new ArrayList<>();
    private ArrayList<String> finishedBranches = new ArrayList<>();
    private String visitingLabel = null;
    private LocalType visitingBranch = null;

    public BranchType(String destination, HashMap<String, LocalType> branches){
        this.destination = destination;
        for (String s : branches.keySet()) {
            this.nextTypes.put(s, branches.get(s));
        }
    }

    public String getDestination(){
        return destination;
    }

    @Override
    public LocalType duplicate() {
        var s = new BranchType(destination, nextTypes);
        s.nextTypes.replaceAll((k, v) -> s.nextTypes.get(k).duplicate());
        if(visitingBranch != null) {
            if(visitingBranch.equivalent(this)){
                s.visitingBranch = new BranchType(destination, nextTypes);
            }else if (visitingBranch instanceof SelectType set){
                s.visitingBranch = new SelectType(set.destination, set.nextTypes);
            }else{
                s.visitingBranch = visitingBranch.duplicate();
            }
        }
        s.visitedLabels.addAll(visitedLabels);
        s.visitingLabel = visitingLabel;
        return s;
    }

    @Override
    public boolean equivalent(Object obj) {
        if(!(obj instanceof BranchType bt)) return false;
        // if they don't have the exact same labels
        if(!(bt.nextTypes.keySet().containsAll(this.nextTypes.keySet()) &
                this.nextTypes.keySet().containsAll(bt.nextTypes.keySet()))) return false;
        for (String s : nextTypes.keySet()) {
            if(!nextTypes.get(s).equivalent(bt.nextTypes.get(s))) return false;
        }
        return true;
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        var msg = mqs.poll(destination, process);
        // no label has been sent, we wait
        if(msg == null) return this;
        if(!msg.direction().equals(Utils.Direction.SELECT)) throw new RuntimeException(
                String.format("A label branching is expected at process %s," +
                        " the queue contains a value, type is not valid", process)
        );
        // we received a label, we can proceed
        // we might have received a label we don't support
        if(!nextTypes.containsKey(msg.label())) throw new RuntimeException(
                String.format("Process %s does not support label %s at that point of its execution, type is not valid",
                        process, msg.label()));
        return nextTypes.get(msg.label());
    }

    @Override
    protected LocalType extractParticipation(String process) {
        var branches = new ArrayList<LocalType>();
        nextTypes.forEach((pr, type) -> {
            branches.add(type.extractParticipation(process));
        });
        var allSame = branches.stream().noneMatch(el -> !el.equivalent(branches.getFirst()));
        if(allSame) return branches.getFirst();
        else throw new RuntimeException("execution branches don't match");
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        var branches = new ArrayList<LocalType>();
        for (String process : processes) {
            try{
                branches.add(extractParticipation(process));
            }catch (Exception e){
                return false;
            }
        }
        if(branches.stream().anyMatch(el -> !el.equivalent(branches.getFirst()))) return false;
        for (String s : nextTypes.keySet()) {
            if(!nextTypes.get(s).knowlegdgeOfChoice(processes)) return false;
        }
        return true;
    }

    @Override
    public boolean visitedAllPaths() {
        for (String s : nextTypes.keySet()) {
            if(!nextTypes.get(s).visitedAllPaths()) return false;
        }
        return true;
    }

    private void removeBranch(String s) {
        nextTypes.remove(s);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(destination);
        b.append("& {\n");
        short counter = 1;
        for (String s : nextTypes.keySet()) {
            b.append("\t").append(s).append(" : ").append(nextTypes.get(s).toString());
            if(counter < nextTypes.keySet().size()){
                b.append(",\n");
            }
            counter++;
        }
        b.append("\n}");
        return b.toString();
    }


}