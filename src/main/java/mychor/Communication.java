package mychor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static mychor.Utils.Arity.INFINITE;
import static mychor.Utils.Arity.MULTIPLE;
import static mychor.Utils.Arity.SINGLE;

public record Communication(Utils.Direction direction, Utils.Arity arity, ArrayList<Communication> communicationsBranches, String label) {

    public Communication{
        Objects.requireNonNull(direction);
        Objects.requireNonNull(arity);
        if(direction.equals(Utils.Direction.SELECT) || direction.equals(Utils.Direction.BRANCH)){
            Objects.requireNonNull(label);
        }
    }

    public Communication(Utils.Direction direction, Utils.Arity arity, String label){
        this(direction, arity, new ArrayList<>(), label);
    }
    public Communication(Utils.Direction direction, Utils.Arity arity, ArrayList<Communication> communicationsBranches){
        this(direction, arity, communicationsBranches, null);
    }
    public Communication(Utils.Direction direction, Utils.Arity arity, Communication nextCommunication){
        this(direction, arity, new ArrayList<>(List.of(nextCommunication)));
    }
    public Communication(Utils.Direction direction, Utils.Arity arity){
        this(direction, arity, new ArrayList<>());
    }

    public boolean isSend(){
        return direction == Utils.Direction.SEND;
    }

    public boolean isReceive(){
        return direction == Utils.Direction.RECEIVE;
    }

    public boolean isSelect(){
        return direction == Utils.Direction.SELECT;
    }

    public boolean isBranch(){
        return direction == Utils.Direction.BRANCH;
    }

    public boolean isSingle(){
        return arity == SINGLE;
    }

    public boolean isMultiple(){
        return arity == MULTIPLE;
    }

    public boolean isInfinite(){
        return arity == INFINITE;
    }

    public int getBranchesDepth(){
        int c = 0;
        for (Communication communicationsBranch : communicationsBranches) {
            c+=communicationsBranch.getBranchesDepth();
        }
        return c+1;
    }
    public void displayCommunications(String prefix) {
        System.out.println(prefix+direction+"-"+arity+(label != null ? label : ""));
        for (Communication communicationsBranch : communicationsBranches) {
            communicationsBranch.displayCommunications(prefix+prefix);
        }
    }

    public boolean isComplementary(Communication comp){
        if (!(isSend() ? comp.isReceive() :
                (isReceive() ? comp.isSend() :
                        (isBranch() ? comp.isSelect() : comp.isBranch())))) return false;
        if(communicationsBranches.size() != comp.communicationsBranches().size()) return false;
        if(!Objects.equals(label, comp.label)) return false;
        for (int i = 0; i < communicationsBranches.size(); i++) {
            var c1 = communicationsBranches.get(i);
            var c2 = comp.communicationsBranches().get(i);
            if(!c1.isComplementary(c2)) return false;
        }
        return true;
    }
    public boolean isEqual(Communication comp){
        if(!(direction == comp.direction() && arity == comp.arity() && Objects.equals(label, comp.label))) return false;
        if(communicationsBranches.size() != comp.communicationsBranches().size()) return false;
        for (int i = 0; i < communicationsBranches.size(); i++) {
            if(!communicationsBranches.get(i).isEqual(comp.communicationsBranches().get(i))) return false;
        }
        return true;
    }

    public boolean isBranchingValid() {
        if(communicationsBranches.size() == 0) return true;
        if(communicationsBranches.size() == 1) return communicationsBranches.get(0).isBranchingValid();
        //if the branches are all SELECT, then anything following is valid
        var allSelect = communicationsBranches.stream()
                .allMatch(item -> item.direction == Utils.Direction.SELECT);
        if (allSelect) return true;
        //if the branches are all BRANCH, then anything following is valid
        var allBranch = communicationsBranches.stream()
                .allMatch(item -> item.direction == Utils.Direction.BRANCH);
        if (allBranch) return true;
        //else, branching is valid if all branches are the same
        return  communicationsBranches.stream()
                .allMatch(item -> item.isEqual(communicationsBranches.get(0)));
    }

    public boolean expandLeafCommunicationRoots(ArrayList<Communication> roots) {
        if(communicationsBranches.size() == 0) return false;
        else{
            if(!communicationsBranches.get(0).expandLeafCommunicationRoots(roots)){
                communicationsBranches.addAll(roots);
            }
        }
        return true;
    }

    public void addLeafCommunicationRoots(ArrayList<Communication> roots){
        if(communicationsBranches.size() == 0){
            communicationsBranches.addAll(roots);
            return;
        }
        communicationsBranches.get(0).addLeafCommunicationRoots(roots);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Communication[\n");
        s.append(String.format("\tdirection=%s\n\tarity=%s\n\tlabel=%s\n\tcommunicationsBranches=[", direction, arity, label));
        for (Communication communicationsRoot : communicationsBranches) {
            s.append("\n\t\t").append(communicationsRoot.toString().replace("\n", "\n\t\t"));
        }
        return s.append("\n\t]\n]").toString();
    }
}
