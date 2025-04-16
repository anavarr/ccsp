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
    private HashMap<String, Integer> visitStats = new HashMap<>();

    public String getDestination() {
        return destination;
    }

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
    public boolean equivalent(Object obj) {
        if(!(obj instanceof SelectType st)) return false;
        // if they don't have the exact same labels
        if(!(st.nextTypes.keySet().containsAll(this.nextTypes.keySet()) &
                this.nextTypes.keySet().containsAll(st.nextTypes.keySet()))) return false;
        for (String s : nextTypes.keySet()) {
            if(!nextTypes.get(s).equivalent(st.nextTypes.get(s))) return false;
        }
        return true;
    }

    @Override
    public boolean visitedAllPaths() {
        if(visitStats.values().stream().anyMatch(el -> el == 0)) return false;
        for (String s : nextTypes.keySet()) {
            if(!nextTypes.get(s).visitedAllPaths()) return false;
        }
        return true;
    }

    @Override
    public boolean contains(LocalType lt) {
        if(lt == this) return true;
        for (LocalType value : nextTypes.values()) {
            if(value.contains(lt)) return true;
        }
        return false;
    }

    @Override
    public List<String> getSelectionsToVisit() {
        ArrayList<String> labels = new ArrayList<>(visitStats.entrySet().stream()
                .filter(el -> el.getValue() == 0)
                .map(Map.Entry::getKey).toList());
        for (LocalType value : nextTypes.values()) {
            labels.addAll(value.getSelectionsToVisit());
        }
        return labels;
    }

    @Override
    public List<String> getBranchesToVisit() {
        ArrayList<String> labels = new ArrayList<>();
        for (LocalType value : nextTypes.values()) {
            labels.addAll(value.getBranchesToVisit());
        }
        return labels;
    }

    @Override
    public HashMap<BranchType, List<String>> getBranchingNodesAndLabelsToVisit() {
        HashMap<BranchType, List<String>> bNodesAndLabels = new HashMap<>();
        for (LocalType value : nextTypes.values()) {
            var hm = value.getBranchingNodesAndLabelsToVisit();
            if(hm != null) bNodesAndLabels.putAll(hm);
        }
        if(bNodesAndLabels.isEmpty()) return null;
        return bNodesAndLabels;
    }

    @Override
    public HashMap<SelectType, List<String>> getSelectionNodesAndLabelsToVisit() {
        HashMap<SelectType, List<String>> sNodesAndLabels = new HashMap<>();
        var labels = visitStats.entrySet().stream().filter((e) -> e.getValue() == 0).map(Map.Entry::getKey).toList();
        if(!labels.isEmpty()) sNodesAndLabels.put(this, labels);
        for (LocalType value : nextTypes.values()) {
            var hm = value.getSelectionNodesAndLabelsToVisit();
            if(hm != null) sNodesAndLabels.putAll(hm);
        }
        if(sNodesAndLabels.isEmpty()) return null;
        return sNodesAndLabels;
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
        visited++;
        var label = getLeastVisitedLabel();
        if(label == null) return new EndType();
        visitStats.put(label, visitStats.get(label)+1);
        mqs.add(Utils.Direction.SELECT, pr, destination, label);
        if(nextTypes.get(label) instanceof RecurseCallType rct) return rct.reduce(pr, mqs);
        return nextTypes.get(label);
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
                        return branches.get(process).equivalent(lt);
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
        var allSame = branches.stream().noneMatch(el -> !el.equivalent(branches.getFirst()));
        if(allSame) return branches.getFirst();
        else throw new RuntimeException("execution branches don't match");
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToSendRecursive() {
        HashMap<String, ArrayList<String>> toSend = new HashMap<>();
        toSend.put(destination, new ArrayList<>(nextTypes.keySet().stream().toList()));

        for (String s : nextTypes.keySet()) {
            var hm = nextTypes.get(s).getMsgsToSendRecursive();
            if(hm != null){
                for (String string : hm.keySet()) {
                    if(toSend.containsKey(string)) toSend.get(string).addAll(hm.get(string));
                    else toSend.put(string, hm.get(string));
                }
            }
        }
        return toSend;
    }

    @Override
    public PossibleMessages getMsgsToReceive(String name) {
        return null;
    }

    @Override
    public PossibleMessages getMsgsToSend(String name) {
        return new PossibleMessages(name, destination, nextTypes.keySet().stream().toList());
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToReceiveRecursive() {
        HashMap<String, ArrayList<String>> toReceive = new HashMap<>();
        for (String s : nextTypes.keySet()) {
            var hm = nextTypes.get(s).getMsgsToReceiveRecursive();
            if(hm != null){
                for (String string : hm.keySet()) {
                    if(toReceive.containsKey(string)) toReceive.get(string).addAll(hm.get(string));
                    else toReceive.put(string, hm.get(string));
                }
            }
        }
        return toReceive;
    }
}
