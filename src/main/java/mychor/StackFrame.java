package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class StackFrame{
    Map<Session, List<Communication>> previousCommunications = new HashMap<>();
    private final ArrayList<StackFrame> nextFrames = new ArrayList<>();
    private StackFrame previousFrame;
    public String varName;
    public StackFrame(String varName, ArrayList<StackFrame> nextFrames, Map<Session, List<Communication>> previousCommunications){
        this.varName = varName;
        this.previousCommunications = previousCommunications;
        this.nextFrames.addAll(nextFrames);
    }
    public StackFrame(String varName, ArrayList<StackFrame> nextFrames){
        this.varName = varName;
        this.nextFrames.addAll(nextFrames);
    }
    public StackFrame(String varName){
        this.varName = varName;
    }
    public void addNextFrames(ArrayList<StackFrame> stackFrames){
        nextFrames.addAll(stackFrames);
        stackFrames.forEach(f -> f.setPreviousFrames(this));
    }
    public void addLeafFrame(StackFrame stackFrame){
        if (nextFrames.isEmpty()){
            nextFrames.add(stackFrame);
            stackFrame.previousFrame = this;
            return;
        }
        nextFrames.get(0).addLeafFrame(stackFrame);
    }
    public void setPreviousFrames(StackFrame pf){
        this.previousFrame = pf;
    }
    public void addLeafFrames(ArrayList<StackFrame> stackFrames){
        if (nextFrames.isEmpty()){
            nextFrames.addAll(stackFrames);
            stackFrames.forEach(sf -> sf.setPreviousFrames(this));
            return;
        }
        nextFrames.get(0).addLeafFrames(stackFrames);
    }
    // CAN LEAD TO STACK OVERFLOW IF NAME ISN'T IN, NEED A BLOCKER
    public boolean isVarNameInGraph(String var){
        if(this.varName.equals(var)) return true;
        if(previousFrame != null && previousFrame.isVarNameInGraph(var)) return true;
        return nextFrames.stream().anyMatch(item -> item.isVarNameInGraph(var));
    }

    public boolean isVarNameAboveOrSelf(String var){
        if (varName.equals(var)) return true;
        return previousFrame != null && previousFrame.isVarNameAboveOrSelf(var);
    }
    public boolean isVarNameAbove(String var){
        return previousFrame != null && previousFrame.isVarNameAboveOrSelf(var);
    }
    public boolean isVarNameBelowOrSelf(String var){
        if(varName.equals(var)) return true;
        return nextFrames.stream().anyMatch(item -> item.isVarNameBelowOrSelf(var));
    }
    public boolean isVarNameBelow(String var){
        return nextFrames.stream().anyMatch(item -> item.isVarNameBelowOrSelf(var));
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
        s.append("}").append("-").append(previousCommunications);
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