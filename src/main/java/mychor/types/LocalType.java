package mychor.types;

import mychor.Behaviour;
import mychor.Call;
import mychor.Cdt;
import mychor.Comm;
import mychor.End;
import mychor.None;
import mychor.Utils;

import java.util.HashMap;

public abstract class LocalType {

    public LocalType(){

    }

    public HashMap<String, LocalType> nextTypes = new HashMap<>();

    public static LocalType extractLocalType(Behaviour bev){
        return switch (bev){
            case Call call:
                yield new RecurseCallType(call.getVariableName());
            case Cdt cdt:
                //merge it
                var branches = cdt.getBranches();
                //two cases : first one is a selection, none is
                if(branches.stream().filter(el -> el instanceof Comm & ((Comm)el).getDirection()
                        .equals(Utils.Direction.SELECT)).count() < branches.size()){
                    //all select
                    var st = new SelectType();
                    for (Behaviour branch : branches) {
                        var label = ((Comm) branch).labels.getFirst();
                        st.addLabel(label, extractLocalType(branch.nextBehaviours.get(label)));
                    }
                    yield st;
                }else{
                    //not all select, it is not great
                    var types = branches.stream().map(LocalType::extractLocalType).toList();
                    System.out.println(types);
                    yield types.getFirst();
                }
            case Comm comm:
                yield switch (comm.getDirection()){
                    case VOID, DUMMY -> throw new IllegalArgumentException();
                    case SEND -> {
                        LocalType nextType;
                        if(comm.nextBehaviours.isEmpty()) nextType = new EndType();
                        else nextType = extractLocalType(comm.getBranches().getFirst());
                        yield new SendType(nextType);
                    }
                    case RECEIVE -> {
                        LocalType nextType;
                        if(comm.getBranches().isEmpty()) nextType = new EndType();
                        else nextType = extractLocalType(comm.getBranches().getFirst());
                        yield new ReceiveType(nextType);
                    }
                    case BRANCH -> {
                        HashMap<String, LocalType> nextTypes = new HashMap<>();
                        for (String s : comm.nextBehaviours.keySet()) {
                            nextTypes.put(s, extractLocalType(comm.nextBehaviours.get(s)));
                        }
                        yield new BranchType(nextTypes);
                    }
                    case SELECT -> {
                        HashMap<String, LocalType> nextTypes = new HashMap<>();
                        for (String s : comm.nextBehaviours.keySet()) {
                            nextTypes.put(s, extractLocalType(comm.nextBehaviours.get(s)));
                        }
                        yield new SelectType(nextTypes);
                    }
                };
            case None none:
                yield new EndType();
            case End end:
                yield new EndType();
            default:
                throw new IllegalStateException("Unexpected value: " + bev);
        };
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
