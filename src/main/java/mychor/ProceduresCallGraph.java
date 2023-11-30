package mychor;

import mychor.StackFrame;
import java.util.ArrayList;
import java.util.Stack;

public class ProceduresCallGraph extends ArrayList<StackFrame>{
    private final ArrayList<StackFrame> roots;

    public ProceduresCallGraph() {
        this.roots = new ArrayList<>();
    }

    public ProceduresCallGraph(ArrayList<StackFrame> roots) {
        this.roots = roots;
    }

    public ArrayList<StackFrame> getRoots() {
        return roots;
    }

    public void addRoot(StackFrame root) {
        roots.add(root);
    }

    public void addRoots(ArrayList<StackFrame> roots) {
        this.roots.addAll(roots);
    }

    public boolean isVarNameInGraph(String varName) {
        return roots.stream().anyMatch(it -> it.isVarNameInGraph(varName));
    }

    public void addLeafFrame(StackFrame stackFrame) {
        if(roots.size()==0){
            roots.add(stackFrame);
        }else{
            roots.get(0).addLeafFrame(stackFrame);
        }
    }

    public int getRootsCount() {
        return roots.size();
    }

    public void addLeafFrames(ArrayList<StackFrame> frames) {
        if(roots.size()==0){
            roots.addAll(frames);
        }else{
            roots.get(0).addLeafFrames(frames);
        }
    }

    @Override
    public String toString() {
        return String.join("\n\t",roots.stream()
                .map(item -> item.toString().replace("\n","\n\t"))
                .toList()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProceduresCallGraph comp)){
            return false;
        }
        return roots.equals(comp.roots);
    }
}
