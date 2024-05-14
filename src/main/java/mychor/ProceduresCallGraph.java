package mychor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProceduresCallGraph extends ArrayList<String>{

    public ProceduresCallGraph() {
        super();
    }

    public ProceduresCallGraph(ArrayList<String> roots) {
        super(roots);
    }

    public boolean isVarNameInGraph(String varName) {
        return this.contains(varName);
    }

    public void addLeafFrame(String varName) {
        this.add(varName);
    }

    public void addLeafFrames(ArrayList<String> frames) {
        addAll(frames);
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
        pcg.addAll(this);
        return pcg;
    }
}
