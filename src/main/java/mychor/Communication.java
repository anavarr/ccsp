package mychor;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static mychor.Utils.Direction.SELECT;
import static mychor.Utils.Direction.VOID;

public class Communication {
    private final Utils.Direction direction;
    private final ArrayList<Communication> communicationsBranches;
    private final String label;
    private Communication previous;

    public Communication(Utils.Direction direction, ArrayList<Communication> comBranches, String label, Communication prev){
        Objects.requireNonNull(direction);
        this.direction = direction;
        communicationsBranches = comBranches;
        this.label = label;
        previous = prev;
        if(direction.equals(SELECT) || direction.equals(Utils.Direction.BRANCH)){
            Objects.requireNonNull(label);
        }
        for (Communication communicationsBranch : communicationsBranches) {
            communicationsBranch.previous = this;
        }
    }

    public Utils.Direction getDirection() {
        return direction;
    }
    public String getLabel(){
        return label;
    }

    public Communication(Utils.Direction direction, String label){
        this(direction, new ArrayList<>(), label, null);
    }
    public Communication(Utils.Direction direction, ArrayList<Communication> communicationsBranches){
        this(direction, communicationsBranches, null, null);
    }
    public Communication(Utils.Direction direction, ArrayList<Communication> communicationsBranches, String label){
        this(direction, communicationsBranches, label, null);
    }
    public Communication(Utils.Direction direction, Communication nextCommunication){
        this(direction, new ArrayList<>(List.of(nextCommunication)));
    }
    public Communication(Utils.Direction direction){
        this(direction, new ArrayList<>());
    }

    public boolean isSend(){
        return direction == Utils.Direction.SEND;
    }

    public boolean isReceive(){
        return direction == Utils.Direction.RECEIVE;
    }

    public boolean isSelect(){
        return direction == SELECT;
    }

    public boolean isBranch(){
        return direction == Utils.Direction.BRANCH;
    }

    public int getBranchesSize(){
        int c = 0;
        for (Communication communicationsBranch : communicationsBranches) {
            c+=communicationsBranch.getBranchesSize();
        }
        return c+1;
    }
    public boolean isComplementary(Communication comp){
        if (!(isSend() ? comp.isReceive() :
                (isReceive() ? comp.isSend() :
                        (isBranch() ? comp.isSelect() : comp.isBranch())))) return false;
        if(communicationsBranches.size() != comp.communicationsBranches.size()) return false;
        if(!Objects.equals(label, comp.label)) return false;
        for (int i = 0; i < communicationsBranches.size(); i++) {
            var c1 = communicationsBranches.get(i);
            var c2 = comp.communicationsBranches.get(i);
            if(!c1.isComplementary(c2)) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Communication comp)) return false;
        if(!(direction == comp.direction && Objects.equals(label, comp.label))) return false;
        if(communicationsBranches.size() != comp.communicationsBranches.size()) return false;
        for (int i = 0; i < communicationsBranches.size(); i++) {
            if(!(communicationsBranches.contains(comp.communicationsBranches.get(i))
                    & comp.communicationsBranches.contains(communicationsBranches.get(i)))) return false;
        }
        return true;
    }

    public boolean isBranchingValid() {
        if(communicationsBranches.isEmpty()) return true;
        //if the branches are all SELECT, then anything following is valid
        var allSelect = communicationsBranches.stream()
                .allMatch(item -> item.direction == SELECT);
        //if the branches are all BRANCH, then anything following is valid
        var allBranch = communicationsBranches.stream()
                .allMatch(item -> item.direction == Utils.Direction.BRANCH);
        //else, branching is valid if all branches are the same
        var allSame = communicationsBranches.stream()
                .allMatch(item -> item.equals(communicationsBranches.get(0)));
        if(!(allSelect || allBranch || allSame)) return false;
        return communicationsBranches.stream().allMatch(Communication::isBranchingValid);
    }

    public void addLeafCommunicationRoots(ArrayList<Communication> roots){
        if(communicationsBranches.isEmpty()){
            communicationsBranches.addAll(roots);
            for (Communication root : roots) {
                root.previous = this;
            }
        }else{
            if(roots.contains(new Communication(VOID))){
                //can't add any continuation if there is already a void
                return;
            }
            communicationsBranches.get(0).addLeafCommunicationRoots(roots);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Communication[\n");
        s.append(String.format("\tdirection=%s\n\tlabel=%s\n\t[", direction, label));
        for (Communication communicationsRoot : communicationsBranches) {
            s.append("\n\t\t").append(communicationsRoot.toString().replace("\n", "\n\t\t"));
        }
        return s.append("\n\t]\n]").toString();
    }

    public ArrayList<Communication> getLeaves() {
        if(communicationsBranches.isEmpty()){
            return new ArrayList<>(List.of(this));
        }
        return communicationsBranches.stream().map(Communication::getLeaves).reduce(new ArrayList<>(),
                (acc, it) -> {acc.addAll(it); return acc;});
    }

    public Set<String> getDirectedLabels(Utils.Direction dir) {
        var labels = new HashSet<String>();
        if(label != null && direction == dir) labels.add(label);
        for (Communication communicationsBranch : communicationsBranches) {
            labels.addAll(communicationsBranch.getDirectedLabels(dir));
        }
        return labels;
    }
}
