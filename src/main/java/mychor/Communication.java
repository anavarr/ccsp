package mychor;

import java.lang.reflect.Array;
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

    public void addLeafCommunication(Communication communication){
        if(communicationsBranches.size() == 0){
            communicationsBranches.add(communication);
            return;
        }
        communicationsBranches.get(0).addLeafCommunication(communication);
    }

    public void addLeafCommunications(ArrayList<Communication> communications){
        if(communicationsBranches.size() == 0){
            communicationsBranches.addAll(communications);
            return;
        }
        communicationsBranches.get(0).addLeafCommunications(communications);
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
                        (isBranch() ? comp.isSelect() : comp.isBranch())))){
            return false;
        }
        if(communicationsBranches.size() != comp.communicationsBranches().size()){
            return false;
        }
        for (int i = 0; i < communicationsBranches.size(); i++) {
            var c1 = communicationsBranches.get(i);
            var c2 = comp.communicationsBranches().get(i);
            if(!c1.isComplementary(c2)){
                return false;
            }
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

    public static Communication list2chain(List<Communication> communications){
        if(communications == null){
            return null;
        }
        if(communications.size() == 0){
            return null;
        }
        Communication root = communications.get(0);
        for (int i = 1; i < communications.size(); i++) {
            root.addLeafCommunication(communications.get(i));
        }
        return root;
    }

    public boolean expandLeafCommunicationRoots(ArrayList<Communication> communicationsRoots) {
        if(communicationsBranches.size() == 0){
            return false;
        }else{
            if(!communicationsBranches.get(0).expandLeafCommunicationRoots(communicationsRoots)){
                communicationsBranches.addAll(communicationsRoots);
            }
        }
        return true;
    }
}
