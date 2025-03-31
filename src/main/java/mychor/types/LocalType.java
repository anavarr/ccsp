package mychor.types;

import mychor.Behaviour;
import mychor.Call;
import mychor.Cdt;
import mychor.Comm;
import mychor.End;
import mychor.MessageQueues;
import mychor.None;
import mychor.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class LocalType {

    public LocalType(){
    }

    public HashMap<String, LocalType> nextTypes = new HashMap<>();
    private static HashMap<String, LocalType> recursionDef = new HashMap<String, LocalType>();
    public static LocalType extractLocalType(Behaviour bev) throws Exception {
        return switch (bev){
            case Call call:
                if(call.nextBehaviours.isEmpty()){
                    // it is final : the procedure has already been called, this is a recursive call
                    yield new RecurseCallType(call.getVariableName(), (RecurseDefType) recursionDef.get(call.getVariableName()));
                }else{
                    var lt = new RecurseDefType(call.getVariableName());
                    recursionDef.put(call.getVariableName(), lt);
                    lt.addUnfolding(extractLocalType(call.nextBehaviours.get("unfold")));
                    yield lt;
                }
            case Cdt cdt:
                //merge it
                var branches = cdt.getImmediateBranches();
                if(branches.isEmpty()) yield new EndType();
                //two cases : first one is a selection, none is
                var selectBranches = branches.stream()
                        .filter(el -> {
                            if(el instanceof Comm comm){
                                return comm.getDirection().equals(Utils.Direction.SELECT);
                            }
                            return false;
                        });
                if(selectBranches.count() == branches.size()){
                    //all select
                    // check that they all have same destination !!!
                    var destinations = new HashSet<>(branches.stream().map(el -> ((Comm)el).getDestination()).toList());
                    if(destinations.size() > 1) {
                        throw new Exception("Can't extract local type as all processes are not selected");
                    }
                    var st = new SelectType(((Comm)(branches.getFirst())).getDestination());
                    for (Behaviour branch : branches) {
                        var label = ((Comm) branch).labels.getFirst();
                        st.addLabel(label, extractLocalType(branch.nextBehaviours.get(label)));
                    }
                    yield st;
                }else{
                    //not all select, it is not great
                    LocalType lt;
                    LocalType oldLocalType = extractLocalType(branches.getFirst());
                    for (Behaviour branch : branches) {
                        lt = extractLocalType(branch);
                        if(!oldLocalType.equals(lt)) {
                            throw new Exception("Can't extract local type as branches of conditional are not valid");
                        }
                    }
                    yield oldLocalType;
                }
            case Comm comm:
                yield switch (comm.getDirection()){
                    case VOID, DUMMY -> throw new IllegalArgumentException();
                    case SEND -> {
                        LocalType nextType;
                        if(comm.nextBehaviours.isEmpty()) nextType = new EndType();
                        else nextType = extractLocalType(comm.nextBehaviours.get(";"));
                        yield new SendType(comm.getDestination(), nextType);
                    }
                    case RECEIVE -> {
                        LocalType nextType;
                        if(comm.nextBehaviours.isEmpty()) nextType = new EndType();
                        else nextType = extractLocalType(comm.nextBehaviours.get(";"));
                        yield new ReceiveType(comm.getDestination(), nextType);
                    }
                    case BRANCH -> {
                        HashMap<String, LocalType> nextTypes = new HashMap<>();
                        for (String s : comm.nextBehaviours.keySet()) {
                            nextTypes.put(s, extractLocalType(comm.nextBehaviours.get(s)));
                        }
                        yield new BranchType(comm.getDestination(), nextTypes);
                    }
                    case SELECT -> {
                        HashMap<String, LocalType> nextTypes = new HashMap<>();
                        for (String s : comm.nextBehaviours.keySet()) {
                            nextTypes.put(s, extractLocalType(comm.nextBehaviours.get(s)));
                        }
                        yield new SelectType(comm.getDestination(), nextTypes);
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

    public abstract LocalType reduce(String process, MessageQueues mqs);

    public abstract LocalType duplicate();

    protected abstract LocalType extractParticipation(String process);

    public abstract Boolean knowlegdgeOfChoice(Collection<String> processes);

    protected abstract LocalType softReset();
}
