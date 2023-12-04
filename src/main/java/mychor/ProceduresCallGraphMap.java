package mychor;

import java.util.HashMap;
import java.util.Objects;

public class ProceduresCallGraphMap extends HashMap<String, ProceduresCallGraph> {
    public ProceduresCallGraphMap(){
        super();
    }
    public ProceduresCallGraphMap(ProceduresCallGraphMap gm){
        putAll(gm);
    }

    public static ProceduresCallGraphMap mergeCalledProceduresHorizontal(
            ProceduresCallGraphMap calledProcedures1,
            ProceduresCallGraphMap calledProcedures2
    ){
        var toAdd = calledProcedures2.keySet();
        var res = new ProceduresCallGraphMap(calledProcedures1);
        for (String s : res.keySet()) {
            if(calledProcedures2.containsKey(s)){
                res.get(s).addRoots(calledProcedures2.get(s));
                toAdd.remove(s);
            }
        }
        for (String s : toAdd) {
            res.put(s, calledProcedures2.get(s));
        }
        return res;
    }
    public static ProceduresCallGraphMap mergeCalledProceduresVertical(
            ProceduresCallGraphMap calledProcedures1,
            ProceduresCallGraphMap calledProcedures2
    ){
        var toAdd = calledProcedures2.keySet();
        var res = new ProceduresCallGraphMap(calledProcedures1);
        for (String s : res.keySet()) {
            if(calledProcedures2.containsKey(s)){
                res.get(s).addLeafFrames(calledProcedures2.get(s));
                toAdd.remove(s);
            }
        }
        for (String s : toAdd) {
            res.put(s, calledProcedures2.get(s));
        }
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProceduresCallGraphMap comp)){
            return false;
        }
        if(!Objects.equals(comp.keySet(),this.keySet())){
            System.out.println("not the same keyset");
            return false;
        }
        for (String s : this.keySet()) {
            if(!get(s).equals(comp.get(s))){
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("{");
        for (String k : keySet()) {
            s.append("\n\t").append(k).append(":\n\t\t").append(get(k).toString().replace("\n","\n\t"));
        }
        s.append("\n}");
        return s.toString();
    }

    public ProceduresCallGraphMap duplicate() {
        ProceduresCallGraphMap pcgm = new ProceduresCallGraphMap();
        for (String s : keySet()) {
            pcgm.put(s, get(s).duplicate());
        }
        return pcgm;
    }
}
