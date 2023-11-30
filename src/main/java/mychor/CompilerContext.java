package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    //a list of errors
    public List<String> errors = new ArrayList<>();
    public HashMap<String,ArrayList<StackFrame>> calledProceduresGraph = new HashMap<>();

    public static class StackFrame{
        private ArrayList<StackFrame> nextFrames;
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
    }
}
