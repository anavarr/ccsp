package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

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
    public HashMap<String,Stack<String>> calledProceduresStacks = new HashMap<>();
}
