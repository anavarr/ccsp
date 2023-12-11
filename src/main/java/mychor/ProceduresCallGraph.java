package mychor;

import java.util.ArrayList;

public class ProceduresCallGraph extends ArrayList<StackFrame>{

    public ProceduresCallGraph() {
        super();
    }

    public ProceduresCallGraph(ArrayList<StackFrame> roots) {
        super(roots);
    }

    public boolean isVarNameInGraph(String varName) {
        return stream().anyMatch(it -> it.isVarNameInGraph(varName));
    }

    public void addLeafFrame(StackFrame stackFrame) {
        if(size()==0){
            add(stackFrame);
        }else{
            get(0).addLeafFrame(stackFrame);
        }
    }

    public void addLeafFrames(ArrayList<StackFrame> frames) {
        if(size()==0){
            addAll(frames);
        }else{
            get(0).addLeafFrames(frames);
        }
    }

    @Override
    public String toString() {
        return String.join("\n\t",stream()
                .map(item -> item.toString().replace("\n","\n\t"))
                .toList()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProceduresCallGraph comp)){
            return false;
        }
        return this.containsAll(comp) && comp.containsAll(this);
    }

    public ProceduresCallGraph duplicate() {
        var pcg = new ProceduresCallGraph();
        for (StackFrame stackFrame : this) {
            pcg.add(stackFrame.duplicate());
        }
        return pcg;
    }
}
