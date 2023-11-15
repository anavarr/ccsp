package mychor;

import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
public class SPcodeGenerator extends SPparserRichBaseVisitor<ArrayList<String>>{
    HashMap<String, HashMap<String, ArrayList<String>>> applicationd = new HashMap<>();
    HashMap<String, String> recvar2proc = new HashMap<>();

    HashMap<String, HashMap<String, ArrayList<String>>> resourceRecvars = new HashMap<>();
    HashMap<String, ArrayList<String>> codeRecvars = new HashMap<>();
    // recvar -> { resource -> code }
    private String outputPath = "";
    String currentProcess;
    String currentRecvar;
    // for now : 1 application per process, files are decided using labels

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
    @Override
    public ArrayList<String> visitProgram(SPparserRich.ProgramContext ctx) {
        try{
            Files.createDirectories(Paths.get(outputPath));
        } catch (IOException e) {
            System.err.println("Problem while creating the distributed application folder");
        }
        // network (recdef)+

        //compile the networks -> get the different processes involved and the non-resource code
        ctx.children.get(0).accept((this));
        //compile the Recursive variables
        for(ParseTree pt : ctx.children.subList(1, ctx.getChildCount()-1)){
            var rec = pt.accept(this);
        }
        System.out.println("\n\n\n===== Initial content after reading code ===== \n");
        System.out.println(recvar2proc);
        System.out.println(resourceRecvars);
        System.out.println(applicationd);
        boolean pastedCode = true;

        System.out.println("===== embedding variables =====");
        //we have compiled recursive variables as well as a compiled network,
        while(pastedCode){
            pastedCode = false;
            // iterate over the processes in the distributed application
            for (Map.Entry<String, HashMap<String, ArrayList<String>>> processEntry : applicationd.entrySet()) {
                var resources = processEntry.getValue();
                // iterate over the resource files in the distributed application
                for (Map.Entry<String, ArrayList<String>> resourceEntry : resources.entrySet()) {
                    var code = resourceEntry.getValue();
                    if(code.size() == 0){
                        applicationd.remove(resourceEntry.getKey());
                        ;
                    }
                    // this is not a Resource but a piece of code that is not a handler
                    // it is not in a label, it needs to go in its own file
                    // iterate over the instructions in the Resource file
                    for(int i=0;i<code.size();i++){
                        var s = code.get(i);
                        if(s.contains("<calling")){
                            //The non-resource code ends with a Call
                            var recvar = s.split(" ")[1];
                            recvar = recvar.substring(0, recvar.length()-1);
                            code.remove(i);
                            if(resourceRecvars.keySet().contains(recvar)){
                                //it calls a recursive variable representing a resource
                                if(resourceEntry.getKey() == ""){
                                    resourceRecvars.get(recvar).entrySet().forEach(ent -> {
                                        applicationd.get(processEntry.getKey()).put(ent.getKey(), ent.getValue());
                                    });
                                    pastedCode = true;
                                }else{
                                    if(resourceRecvars.keySet().contains(recvar)){
                                        System.err.println("can't paste a resource into a resource");
                                    }
                                }
                            }
                            if (codeRecvars.keySet().contains(recvar)) {
                                //it calls a recursive variable representing a simple piece of code
                                code.addAll(codeRecvars.get(recvar));
                                pastedCode = true;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("\n\n\n ===== content after pasting code ===== \n");
        System.out.println(resourceRecvars);
        System.out.println(applicationd);
        return new ArrayList<>();
    }

    public void createQuarkusApplication(String service){
        try {
            Files.createDirectories(Paths.get(outputPath, service));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<String> visitNetwork(SPparserRich.NetworkContext ctx) {
        // IDENTIFIER '[' behaviour ']' ( '|' IDENTIFIER '[' behaviour ']' )*;
        var l = new ArrayList<String>();
        for(int i = 0; i<ctx.getChildCount();i+=5){
            currentProcess = ctx.getChild(i).getText();
            var cc = ctx.getChild(i+2).accept(this);
            var hm = new HashMap<String, ArrayList<String>>();
            hm.put("", cc);
            applicationd.put(currentProcess, hm);
            currentProcess = null;
        }
        return new ArrayList<>();
    }

    HashMap<String, HashMap<String, ArrayList<String>>>  distinguishEndpoints(HashMap<String, ArrayList<String>> code){
        var labels=new ArrayList<String>();
        var sortedLabels = code
                .entrySet().stream()
                .collect(groupingBy(entry -> {
                    var s = entry.getKey();
                    var s2 = s.substring(2, s.length()-1);
                    var s2end = s2.indexOf("/");
                    if(s2end  == -1 ){
                        return s2.substring(0, s2.indexOf(":"));
                    }
                    return s2.substring(0, s2end);
                }));
        HashMap<String, HashMap<String, ArrayList<String>>> hm = new HashMap<>();
        sortedLabels.forEach((file, endpoints) -> {
            var h = new HashMap<String, ArrayList<String>>();
            endpoints.forEach(e -> {
                h.put(e.getKey(), e.getValue());
            });
            hm.put(file,h);
        });
        return hm;
    }

    HashMap<String, ArrayList<String>> linearizedCode(HashMap<String, HashMap<String, ArrayList<String>>>nlinearCode){
        HashMap<String, ArrayList<String>> hm = new HashMap<>();
        for (String s : nlinearCode.keySet()) {
            var endpoints = nlinearCode.get(s).keySet();
            ArrayList<String> l = new ArrayList<>();
            endpoints.forEach(e -> {
                var httpVerb = e.substring(e.indexOf(":")+1, e.length()-1);
                var path =e.substring(1, e.indexOf(":"));
                var pathCamelCase = String.join("",
                        Arrays.stream(path.split("/"))
                        .filter(word -> word.length()>0)
                        .map(word ->
                            word.substring(0,1).toUpperCase()+word.substring(1)
                        )
                        .map(w -> {
                            if(w.startsWith("<")){
                                return w.substring(1,2).toUpperCase() + w.substring(2, w.length()-1);
                            }
                            return w;
                        })
                        .toList()
                );
                l.add("@"+httpVerb);
                l.add("@Path(\""+path+"\")");
                l.add("public Object "+httpVerb.toLowerCase()+pathCamelCase+"() {");
                l.addAll(nlinearCode.get(s).get(e));
                l.add("}");
                hm.put(s, l);
            });
        }
        return hm;
    }

    @Override
    public ArrayList<String> visitBra(SPparserRich.BraContext ctx) {
        //proc '&' '{' BLABEL ':' mBehaviour '}'  ('//'  '{' BLABEL ':' mBehaviour '}')+ #Bra
        var l = new ArrayList<String>();
        var h = new HashMap<String, ArrayList<String>>();
        for(int i = 3; i<ctx.children.size(); i+=6){
            var cod = ctx.getChild(i+2).accept(this);
            h.put(ctx.getChild(i).getText(), cod);
        }
        var hm = linearizedCode(distinguishEndpoints(h));
        if(currentRecvar != null){
            //we are reading a Recvar
            resourceRecvars.put(currentRecvar, hm);
        }else{
            for (String resource : hm.keySet()) {
                applicationd.get(currentProcess).put(resource, hm.get(resource));
            }
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> visitRecdef(SPparserRich.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        currentRecvar = ctx.getChild(0).getText();
        String process = recvar2proc.get(currentRecvar);
        currentProcess = process;
        var l = new ArrayList<String>();
        l.add("<start "+currentRecvar+">");
        l.addAll(ctx.getChild(2).accept(this));
        currentProcess = null;
        currentRecvar = null;
        return l;
    }

    @Override
    public ArrayList<String> visitNon(SPparserRich.NonContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<String> visitSnd(SPparserRich.SndContext ctx) {
        var l = new ArrayList<String>();
        l.add(String.format("sendStuff(%s, %s);",
                ctx.getChild(2).getText(),
                ctx.getChild(0).getText())
        );
        var continuation = ctx.getChild(7).accept(this);
        l.addAll(continuation);
        return l;
    }

    @Override
    public ArrayList<String> visitRcv(SPparserRich.RcvContext ctx) {
        var l = new ArrayList<String>();
        l.add(String.format("var %s = receiveStuff(%s);",
                ctx.getChild(2).getText(),
                ctx.getChild(0).getText())
        );
        var continuation = ctx.getChild(7).accept(this);
        l.addAll(continuation);
        return l;
    }

    @Override
    public ArrayList<String> visitCdt(SPparserRich.CdtContext ctx) {
        // IF
        // expr
        // THEN
        // behaviour
        // ELSE
        // behaviour
        var hm = new HashMap<String, ArrayList<String>>();
        var l = new ArrayList<String>();
        l.add(String.format("if (%s) {",ctx.getChild(1).getText()));
        var thenBranch = ctx.getChild(3).accept(this);
        l.addAll(thenBranch);
        l.add("} else {");
        var elseBranch = ctx.getChild(5).accept(this);
        l.addAll(elseBranch);
        l.add("}");
        return l;
    }

    @Override
    public ArrayList<String> visitSel(SPparserRich.SelContext ctx) {
        // proc
        // SEL
        // BLABEL
        // AT
        // '+'
        // ann
        // SEQ
        // behaviour
        var l = new ArrayList<String>();
        l.add(String.format("sendLabel(%s, %s);",
                ctx.getChild(2).getText(),
                ctx.getChild(0).getText())
        );

        var continuation = (ctx.getChild(7).accept(this));
        System.out.println(continuation);
        l.addAll(continuation);
        return l;
    }

    private List<String> generateEndpoints(String procName, HashMap<String, List<String>> labelledBehaviours) {
        var labels = labelledBehaviours.keySet();
        var l = labels.stream().map(s -> s.substring(1, s.length()-1)).collect(groupingBy(s -> {
            var index = s.indexOf("/", s.indexOf("/") + 1);;
            if (index == -1){
                index = s.indexOf(':');
                if (index == -1){
                    return s;
                }
            }
            return s.substring(0, index);
        }));
        return null;
    }

    @Override
    public ArrayList<String> visitSom(SPparserRich.SomContext ctx) {
        return ctx.getChild(2).accept(this);
    }

    @Override
    public ArrayList<String> visitCal(SPparserRich.CalContext ctx) {
        // 0: Call
        // 1: Variable
        var recvar = ctx.getChild(1).getText();
        recvar2proc.put(recvar, currentProcess);
        if(recvar.equals(currentRecvar)){
            //do stuff to say that there is a loop
            //don't do anything in the case of REST services
        }
        var l = new ArrayList<String>();
        l.add("<calling "+recvar+">");
        return l;
    }
}