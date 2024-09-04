package mychor.Generators;

import java.util.ArrayList;
import java.util.HashMap;

public class GenerationContext{
    public ArrayList<String> code = new ArrayList<>();
    public HashMap<String, ArrayList<String>> functions= new HashMap<>();
    public HashMap<String, String> sessionsFrameworks = new HashMap<>();
    public ArrayList<String> staticInit = new ArrayList<>();
    public HashMap<String, Generator> generators = new HashMap<>();
    public ArrayList<String> imports = new ArrayList<>();
    public String lastVariable = null;
    public String service;

    public GenerationContext(){}

    public GenerationContext(String service){this.service = service;}

    public GenerationContext(GenerationContext gc){
        code.addAll(gc.code);
        functions.putAll(gc.functions);
        sessionsFrameworks.putAll(gc.sessionsFrameworks);
        generators.putAll(gc.generators);
        imports.addAll(gc.imports);
        staticInit.addAll(gc.staticInit);
        service = gc.service;
    }
}