package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

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

    public void addLeafFrames(ArrayList<StackFrame> stackFrames){
        if (nextFrames.size() == 0){
            nextFrames.addAll(stackFrames);
            return;
        }
        nextFrames.get(0).addLeafFrames(stackFrames);
    }

    public boolean isVarNameInGraph(String var){
        if(this.varName.equals(var)) return true;
        return nextFrames.stream().anyMatch(item -> item.isVarNameInGraph(var));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(varName).append("{");
        for (StackFrame nextFrame : nextFrames) {
            s.append(String.format("\n\t%s,", nextFrame));
        }
        if(nextFrames.size()>0){
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
            System.out.println("not the same name");
            return false;
        }
        return nextFrames.equals(comp.nextFrames);
    }
}