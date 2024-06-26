package mychor;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static mychor.Utils.ERROR_RECVAR_ADD;

public class CompilerContext {
    public String currentRecVar;
    //the current process studied
    public String currentProcess;
    //a set of processes
    public Set<String> processes = new HashSet<>();
    //a list of communications
    public ArrayList<Session> sessions = new ArrayList<>();
    //maps a recursive variable to a process
    public HashMap<String, String> recvar2proc = new HashMap<>();
    public HashMap<String, Behaviour> behaviours = new HashMap<>();
    //a list of errors
    public List<String> errors = new ArrayList<>();
    public ProceduresCallGraphMap calledProceduresGraph = new ProceduresCallGraphMap();
    public ProceduresCallGraphMap phantomGraph = new ProceduresCallGraphMap();

    CompilerContext duplicateContext(){
        var c = new CompilerContext();
        c.currentProcess = this.currentProcess;
        c.currentRecVar = this.currentRecVar;
        c.processes.addAll(this.processes);
        c.recvar2proc.putAll(this.recvar2proc);
        c.phantomGraph = ProceduresCallGraphMap
                .mergeCalledProceduresVertical(this.phantomGraph.duplicate(), this.calledProceduresGraph.duplicate());
        c.calledProceduresGraph = this.calledProceduresGraph.duplicateRootsOnly();
        return c;
    }

    public static CompilerContext mergeContexts(CompilerContext superCtx, CompilerContext context,
                                          BiFunction<
                                                  ProceduresCallGraphMap,
                                                  ProceduresCallGraphMap,
                                                  ProceduresCallGraphMap
                                                  > calledGraphMerger,
                                          ParserRuleContext ctx){
        superCtx.errors.addAll(context.errors);
        superCtx.calledProceduresGraph = calledGraphMerger
                .apply(superCtx.calledProceduresGraph, context.calledProceduresGraph);
        //merge recvar2proc
        for (String key : context.recvar2proc.keySet()){
            if (superCtx.recvar2proc.containsKey(key)){
                var superValue = superCtx.recvar2proc.get(key);
                var value = context.recvar2proc.get(key);
                if(!superValue.equals(value)){
                    superCtx.errors.add(ERROR_RECVAR_ADD(key, superValue, value, ctx));
                }
            }else{
                superCtx.recvar2proc.put(key, context.recvar2proc.get(key));
            }
        }
        //merge calledProceduresGraph
        return superCtx;
    }
}
