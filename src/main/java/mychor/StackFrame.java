package mychor;

import java.util.ArrayList;
import java.util.HashMap;

public class StackFrame{
    private final ArrayList<StackFrame> nextFrames;
    public String varName;
    public StackFrame(String varName, ArrayList<StackFrame> nextFrames){
        this.varName = varName;
        this.nextFrames = nextFrames;
    }

    public StackFrame(String varName){
        this.varName = varName;
        this.nextFrames = new ArrayList<>();
    }

    public void addNextFrames(ArrayList<StackFrame> stackFrames){
        nextFrames.addAll(stackFrames);
    }

    public void addLeafFrame(StackFrame stackFrame){
        if (nextFrames.size() == 0){
            nextFrames.add(stackFrame);
            return;
        }
        nextFrames.get(0).addLeafFrame(stackFrame);
    }

    public boolean isVarNameInGraph(String var){
        if(this.varName.equals(var)) return true;
        return nextFrames.stream().anyMatch(item -> item.isVarNameInGraph(var));
    }

    public static HashMap<String, ArrayList<StackFrame>> mergeCalledProceduresHorizontal(
            HashMap<String, ArrayList<StackFrame>> calledProcedures1,
            HashMap<String, ArrayList<StackFrame>> calledProcedures2
    ){
        var toAdd = calledProcedures2.keySet();
        var res = new HashMap<>(calledProcedures1);
        for (String s : res.keySet()) {
            if(calledProcedures2.containsKey(s)){
                res.get(s).addAll(calledProcedures2.get(s));
                toAdd.remove(s);
            }
        }
        for (String s : toAdd) {
            res.put(s, calledProcedures2.get(s));
        }
        return res;
    }
    public static HashMap<String, ArrayList<StackFrame>> mergeCalledProceduresVertical(
            HashMap<String, ArrayList<StackFrame>> calledProcedures1,
            HashMap<String, ArrayList<StackFrame>> calledProcedures2
    ){
        var toAdd = calledProcedures2.keySet();
        var res = new HashMap<>(calledProcedures1);
        for (String s : res.keySet()) {
            if(calledProcedures2.containsKey(s)){
                if(res.get(s).size() > 0){
                    res.get(s).get(0).addNextFrames(calledProcedures2.get(s));
                }else{
                    res.get(s).addAll(calledProcedures2.get(s));
                }
                toAdd.remove(s);
            }
        }
        for (String s : toAdd) {
            res.put(s, calledProcedures2.get(s));
        }
        return res;
    }

}