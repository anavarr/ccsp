package mychor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static mychor.Utils.Direction.SELECT;
import static mychor.Utils.Direction.VOID;

public class Communication {
    private final Utils.Direction direction;
    private final ArrayList<Communication> nextCommunicationNodes = new ArrayList<>();
    private final String label;
    private final ArrayList<Communication> previousCommunicationNodes = new ArrayList<>();
    private final ArrayList<Communication> recursiveCallers = new ArrayList<>();

    public Communication(Utils.Direction direction, ArrayList<Communication> nextNodes, String label, ArrayList<Communication> previousNodes){
        Objects.requireNonNull(direction);
        this.direction = direction;
        previousCommunicationNodes.addAll(previousNodes);
        this.label = label;
        if(direction.equals(SELECT) || direction.equals(Utils.Direction.BRANCH)){
            Objects.requireNonNull(label);
        }
        for (Communication comBranch : nextNodes) {
            if(nodeIsSelfOrAbove(comBranch)) recursiveCallers.add(comBranch);
            else {
                nextCommunicationNodes.add(comBranch);
                comBranch.previousCommunicationNodes.add(this);
            }
        }
    }

    public Utils.Direction getDirection() {
        return direction;
    }
    public String getLabel(){
        return label;
    }

    public Communication(Utils.Direction direction, String label){
        this(direction, new ArrayList<>(), label, new ArrayList<>());
    }
    public Communication(Utils.Direction direction, ArrayList<Communication> nextCommunicationNodes){
        this(direction, nextCommunicationNodes, null, new ArrayList<>());
    }
    public Communication(Utils.Direction direction, ArrayList<Communication> nextCommunicationNodes, String label){
        this(direction, nextCommunicationNodes, label, new ArrayList<>());
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
        for (Communication communicationsBranch : nextCommunicationNodes) {
            c+=communicationsBranch.getBranchesSize();
        }
        return c+1;
    }
    public boolean isComplementary(Communication comp){
        if (!(isSend() ? comp.isReceive() :
                (isReceive() ? comp.isSend() :
                        (isBranch() ? comp.isSelect() : comp.isBranch())))) return false;
        if(nextCommunicationNodes.size() != comp.nextCommunicationNodes.size()) return false;
        if(!Objects.equals(label, comp.label)) return false;
        for (int i = 0; i < nextCommunicationNodes.size(); i++) {
            var c1 = nextCommunicationNodes.get(i);
            var c2 = comp.nextCommunicationNodes.get(i);
            if(!c1.isComplementary(c2)) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Communication comp)) return false;
        if(!(direction == comp.direction && Objects.equals(label, comp.label))) return false;
        if(nextCommunicationNodes.size() != comp.nextCommunicationNodes.size()) return false;
        for (int i = 0; i < nextCommunicationNodes.size(); i++) {
            if(!(nextCommunicationNodes.contains(comp.nextCommunicationNodes.get(i))
                    & comp.nextCommunicationNodes.contains(nextCommunicationNodes.get(i)))) return false;
        }
        return true;
    }

    public boolean isBranchingValid() {
        if(nextCommunicationNodes.isEmpty()) return true;
        //if the branches are all SELECT, then anything following is valid
        var allSelect = nextCommunicationNodes.stream()
                .allMatch(item -> item.direction == SELECT);
        //if the branches are all BRANCH, then anything following is valid
        var allBranch = nextCommunicationNodes.stream()
                .allMatch(item -> item.direction == Utils.Direction.BRANCH);
        //else, branching is valid if all branches are the same
        var allSame = nextCommunicationNodes.stream()
                .allMatch(item -> item.equals(nextCommunicationNodes.get(0)));
        if(!(allSelect || allBranch || allSame)) return false;
        return nextCommunicationNodes.stream().allMatch(Communication::isBranchingValid);
    }

    public boolean nodeIsBelow(Communication node){
        if(nextCommunicationNodes.contains(node)) return true;
        for (Communication nextCommunicationNode : nextCommunicationNodes) {
            if(nextCommunicationNode.nodeIsBelow(node)) return true;
        }
        return false;
    }
    public boolean nodeIsSelfOrBelow(Communication node){
        if(node == this) return true;
        if(nextCommunicationNodes.contains(node)) return true;
        for (Communication nextCommunicationNode : nextCommunicationNodes) {
            if(nextCommunicationNode.nodeIsSelfOrBelow(node)) return true;
        }
        return false;
    }
    public boolean nodeIsAbove(Communication root){
        if(previousCommunicationNodes.contains(root)) return true;
        for (Communication communication : previousCommunicationNodes) {
            if(communication.nodeIsAbove(root)) return true;
        }
        return false;
    }
    public boolean nodeIsSelfOrAbove(Communication root){
        if(root == this) return true;
        if(previousCommunicationNodes.contains(root)) return true;
        for (Communication communication : previousCommunicationNodes) {
            if(communication.nodeIsSelfOrAbove(root)) return true;
        }
        return false;
    }
    public void addLeafCommunicationRoots(ArrayList<Communication> roots){
        if(nextCommunicationNodes.isEmpty()){
            for (Communication root : roots) {
                if(nodeIsSelfOrAbove(root)) recursiveCallers.add(root);
                else {
                    nextCommunicationNodes.add(root);
                    root.previousCommunicationNodes.add(this);
                }
            }
        }else{
            for (Communication root : roots) {
                if(nodeIsSelfOrAbove(root)) recursiveCallers.add(root);
                else{
                    if(!roots.contains(new Communication(VOID))){
                        nextCommunicationNodes.get(0).addLeafCommunicationRoots(roots);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Communication[\n");
        s.append(String.format("\tdirection=%s\n\tlabel=%s\n\t[", direction, label));
        for (Communication communicationsRoot : nextCommunicationNodes) {
            s.append("\n\t\t").append(communicationsRoot.toString().replace("\n", "\n\t\t"));
        }
        return s.append("\n\t]\n]").toString();
    }

    public ArrayList<Communication> getLeaves() {
        if(nextCommunicationNodes.isEmpty()){
            return new ArrayList<>(List.of(this));
        }
        return nextCommunicationNodes.stream().map(Communication::getLeaves).reduce(new ArrayList<>(),
                (acc, it) -> {acc.addAll(it); return acc;});
    }

    public Set<String> getDirectedLabels(Utils.Direction dir) {
        var labels = new HashSet<String>();
        if(label != null && direction == dir) labels.add(label);
        for (Communication communicationsBranch : nextCommunicationNodes) {
            labels.addAll(communicationsBranch.getDirectedLabels(dir));
        }
        return labels;
    }

    public boolean containsDirectNextNode(Communication com){
        return nextCommunicationNodes.contains(com);
    }
    public boolean containsDirectPreviousNode(Communication com) { return previousCommunicationNodes.contains(com); }
}
