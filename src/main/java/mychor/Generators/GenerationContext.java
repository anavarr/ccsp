package mychor.Generators;

import java.util.ArrayList;
import java.util.HashMap;

public class GenerationContext{
    public ArrayList<String> code = new ArrayList<>();
    public HashMap<String, ArrayList<String>> functions= new HashMap<>();
    public HashMap<String, String> sessionsFrameworks = new HashMap<>();
    public HashMap<String, Generator> generators = new HashMap<>();
    public ArrayList<String> imports = new ArrayList<>();

    public GenerationContext(){}

    public GenerationContext(GenerationContext gc){
        code.addAll(gc.code);
        functions.putAll(gc.functions);
        sessionsFrameworks.putAll(gc.sessionsFrameworks);
        generators.putAll(gc.generators);
        imports.addAll(gc.imports);
    }
}