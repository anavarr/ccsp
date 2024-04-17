package mychor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
        if (nextFrames.isEmpty()){
            nextFrames.add(stackFrame);
            return;
        }
        nextFrames.get(0).addLeafFrame(stackFrame);
    }
    public void addLeafFrames(ArrayList<StackFrame> stackFrames){
        if (nextFrames.isEmpty()){
            nextFrames.addAll(stackFrames);
            return;
        }
        nextFrames.get(0).addLeafFrames(stackFrames);
    }
    // CAN LEAD TO STACK OVERFLOW IF NAME ISN'T IN, NEED A BLOCKER
    public boolean isVarNameInGraph(String var){
        if(this.varName.equals(var)) return true;
        return nextFrames.stream().anyMatch(item -> item.isVarNameInGraph(var));
    }
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(varName).append("{");
        for (StackFrame nextFrame : nextFrames) {
            s.append(String.format("\n\t%s,", nextFrame.toString().replace("\n","\n\t")));
        }
        if(!nextFrames.isEmpty()){
            s.append("\n");
        }
        s.append("}");
        return s.toString();
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StackFrame comp)){
            return false;
        }
        if(!varName.equals(comp.varName)){
            return false;
        }
        return nextFrames.containsAll(comp.nextFrames) && comp.nextFrames.containsAll(nextFrames);
    }
    public StackFrame duplicate() {
        return new StackFrame(varName, new ArrayList<>(nextFrames.stream().map(StackFrame::duplicate).toList()));
    }
    public boolean containsSelf() {
        return nextFrames.stream().anyMatch(sf -> sf.isVarNameInGraph(varName));
    }
    // two lists
    // 0 : called
    // 1 : looped
    // CAN LEAD TO STACK OVERFLOW
    public List<HashSet<String>> getLoopedVariables() {
        var called=new HashSet<String>();
        var looped = new HashSet<String>();
        for (StackFrame nextFrame : nextFrames) {
            var ls=nextFrame.getLoopedVariables();
            called.addAll(nextFrame.getLoopedVariables().get(0));
            looped.addAll(ls.get(1));
        }
        if(called.contains(varName)){
            looped.add(varName);
        }
        called.add(varName);
        return List.of(called, looped);
    }
    public int getNextFramesSize(){
        return nextFrames.size();
    }
    public boolean containsStackFrames(List<StackFrame> sfs){
        return nextFrames.containsAll(sfs);
    }
}