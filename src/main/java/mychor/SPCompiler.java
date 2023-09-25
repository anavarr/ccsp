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

import static java.util.stream.Collectors.groupingBy;
public class SPCompiler extends SPparserRichBaseVisitor<ArrayList<String>>{

    HashMap<String, HashMap<String, List<String>>> applications;
    HashMap<String, List<String>> code = new HashMap<>();
    HashMap<String, String> recvar2proc = new HashMap<>();
    private String outputPath = "";
    String currentProcess;
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
        for(ParseTree pt : ctx.children){
            pt.accept(this);
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
    public ArrayList<String> visitNetwork(SPparserRich.NetworkContext ctx) {
        // IDENTIFIER '[' behaviour ']' ( '|' IDENTIFIER '[' behaviour ']' )*;
        var l = new ArrayList<String>();
        for(int i = 0; i<ctx.getChildCount();i+=5){
            currentProcess = ctx.getChild(i).getText();
            createQuarkusApplication(currentProcess);
            var cc = ctx.getChild(i+2).accept(this);
            System.out.println(cc);
            code.put(currentProcess, cc);
            l.addAll(code.get(currentProcess));
        }
        return l;
    }

    @Override
    public ArrayList<String> visitBra(SPparserRich.BraContext ctx) {
        HashMap<String, List<String>> labelledBehaviours = new HashMap<>();
        //proc '&' '{' BLABEL ':' mBehaviour '}'  ('//'  '{' BLABEL ':' mBehaviour '}')+ #Bra
        var procName = ctx.getChild(0).getText();
        for(int i = 3; i<ctx.children.size(); i+=6){
            var cod = ctx.getChild(i+2).accept(this);
            labelledBehaviours.put(ctx.getChild(i).getText(), cod);
        }
        var code = generateEndpoints(procName, labelledBehaviours);
        return new ArrayList<>(
                labelledBehaviours.values()
                .stream()
                .flatMap(v -> v.stream()).toList()
        );
    }

    @Override
    public ArrayList<String> visitRecdef(SPparserRich.RecdefContext ctx) {
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
        l.addAll(ctx.getChild(7).accept(this));
        return l;
    }

    @Override
    public ArrayList<String> visitRcv(SPparserRich.RcvContext ctx) {
        var l = new ArrayList<String>();
        l.add(String.format("var %s = receiveStuff(%s);",
                ctx.getChild(2).getText(),
                ctx.getChild(0).getText())
        );
        l.addAll(ctx.getChild(7).accept(this));
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
        var l = new ArrayList<String>();
        l.add(String.format("if (%s) {",ctx.getChild(1).getText()));
        l.addAll(ctx.getChild(3).accept(this));
        l.add("} else {");
        l.addAll(ctx.getChild(5).accept(this));
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
        l.addAll(ctx.getChild(7).accept(this));
        return l;
    }

    Map<String, List<String>> getSmallestEndpoints(List<String> uris){
        var l = uris.stream().map(s -> s.substring(1, s.length()-1)).collect(groupingBy(s -> {
            var index = s.indexOf("/", s.indexOf("/") + 1);;
            if (index == -1){
                index = s.indexOf(':');
                if (index == -1){
                    return s;
                }
            }
            return s.substring(0, index);
        }));
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



        System.out.println(l);
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
        recvar2proc.put(ctx.getChild(1).getText(), currentProcess);
        return new ArrayList<>();
    }
}