package mychor;

import org.antlr.runtime.tree.TreeWizard;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.naming.Context;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
public class SPCompiler2 extends SPparserRichBaseVisitor<HashMap<String, ArrayList<String>>>{
    HashMap<String, HashMap<String, ArrayList<String>>> applicationd = new HashMap<>();
    HashMap<String, List<String>> code = new HashMap<>();
    HashMap<String, String> recvar2proc = new HashMap<>();
    private String outputPath = "";
    String currentProcess;
    // for now : 1 application per process, files are decided using labels

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public HashMap<String, ArrayList<String>> visitProgram(SPparserRich.ProgramContext ctx) {
        try{
            Files.createDirectories(Paths.get(outputPath));
        } catch (IOException e) {
            System.err.println("Problem while creating the distributed application folder");
        }
        // network (recdef)+
        ctx.children.get(0).accept((this));
        for(ParseTree pt : ctx.children.subList(1, ctx.getChildCount()-1)){
            var rec = pt.accept(this);
        }
        return null;
    }

    public void createQuarkusApplication(String service){
        try {
            Files.createDirectories(Paths.get(outputPath, service));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HashMap<String, ArrayList<String>> visitNetwork(SPparserRich.NetworkContext ctx) {
        // IDENTIFIER '[' behaviour ']' ( '|' IDENTIFIER '[' behaviour ']' )*;
        var l = new ArrayList<String>();
        for(int i = 0; i<ctx.getChildCount();i+=5){
            currentProcess = ctx.getChild(i).getText();
            var cc = ctx.getChild(i+2).accept(this);
            applicationd.put(currentProcess, cc);
            currentProcess = null;
        }
        return null;
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
        System.out.println(hm);
        return hm;
    }

    @Override
    public HashMap<String, ArrayList<String>> visitBra(SPparserRich.BraContext ctx) {
        //proc '&' '{' BLABEL ':' mBehaviour '}'  ('//'  '{' BLABEL ':' mBehaviour '}')+ #Bra
        var l = new ArrayList<String>();
        var h = new HashMap<String, ArrayList<String>>();
        for(int i = 3; i<ctx.children.size(); i+=6){
            var cod = ctx.getChild(i+2).accept(this);
            int finalI = i;
            cod.keySet().forEach(key ->
                {
                    h.put(ctx.getChild(finalI).getText(), cod.get(key));
//                    l.add("@Path(" + ctx.getChild(finalI).getText()+")");
//                    l.add("public Object endpoit_"+finalI+"() {");
//                    cod.keySet().forEach(k -> {
//                        l.addAll(cod.get(k));
//                    });
//                    l.add("}");
                }
            );
        }

        distinguishEndpoints(h);
        var hm = new HashMap<String, ArrayList<String>>();
        hm.put("", l);
        return hm;
    }

    @Override
    public HashMap<String, ArrayList<String>> visitRecdef(SPparserRich.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        String process = recvar2proc.get(ctx.getChild(0).getText());
        currentProcess = process;
        var behaviour = ctx.getChild(2).accept(this);
        currentProcess = null;
        return behaviour;
    }

    @Override
    public HashMap<String, ArrayList<String>> visitNon(SPparserRich.NonContext ctx) {
        return new HashMap();
    }

    @Override
    public HashMap<String, ArrayList<String>> visitSnd(SPparserRich.SndContext ctx) {
        var l = new ArrayList<String>();
        l.add(String.format("sendStuff(%s, %s);",
                ctx.getChild(2).getText(),
                ctx.getChild(0).getText())
        );
        var continuation = ctx.getChild(7).accept(this);
        var hm = new HashMap<String, ArrayList<String>>();
        continuation.keySet().forEach(k -> {
            l.addAll(continuation.get(k));
            hm.put(k, l);
        });
        return hm;
    }

    @Override
    public HashMap<String, ArrayList<String>> visitRcv(SPparserRich.RcvContext ctx) {
        var l = new ArrayList<String>();
        l.add(String.format("var %s = receiveStuff(%s);",
                ctx.getChild(2).getText(),
                ctx.getChild(0).getText())
        );
        var continuation = ctx.getChild(7).accept(this);
        var hm = new HashMap<String, ArrayList<String>>();
        continuation.keySet().forEach(k -> {
            l.addAll(continuation.get(k));
            hm.put(k, l);
        });
        return hm;
    }

    @Override
    public HashMap<String, ArrayList<String>> visitCdt(SPparserRich.CdtContext ctx) {
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
        thenBranch.keySet().forEach(k -> {
            //doesn't work if more than 1 k
            l.addAll(thenBranch.get(k));
            hm.put(k, l);
        });
        l.add("} else {");
        var elseBranch = ctx.getChild(5).accept(this);
        elseBranch.keySet().forEach(k -> {
            hm.get(k).addAll(elseBranch.get(k));
            hm.get(k).add("}");
        });
        System.out.println(hm);
        return hm;
    }

    @Override
    public HashMap<String, ArrayList<String>> visitSel(SPparserRich.SelContext ctx) {
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
        var hm = new HashMap<String, ArrayList<String>>();
        continuation.keySet().forEach(k -> {
            l.addAll(continuation.get(k));
            hm.put(k, l);
        });
        return hm;
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
    public HashMap<String, ArrayList<String>> visitSom(SPparserRich.SomContext ctx) {
        return ctx.getChild(2).accept(this);
    }

    @Override
    public HashMap<String, ArrayList<String>> visitCal(SPparserRich.CalContext ctx) {
        // 0: Call
        // 1: Variable
        recvar2proc.put(ctx.getChild(1).getText(), currentProcess);
        HashMap<String, ArrayList<String>> hm = new HashMap<>();
        hm.put("", new ArrayList<>());
        return hm;
    }
}