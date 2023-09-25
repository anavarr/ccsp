package mychor;

import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


public class SPCheckerRich extends SPparserRichBaseVisitor<List<String>>{
    HashMap<String, String> recvar2proc = new HashMap();
    HashMap comms = new HashMap<String, List<String>>();
    String currentProcess;
    ArrayList<String> recVars = new ArrayList<>();

    //
    public HashMap<String, List<String>> processCommunicationsMap(){
        return comms;
    }

    // check that no process sends or receive a message to or from itself
    public boolean noSelfCom() {
        Set<String> keys = comms.keySet();
        for (String k : keys) {
            if (((List<String>)comms.get(k)).contains(k)){
                return false;
            }
        }
        return true;
    }

    // check that all the processes mentioned are defined in the network
    public List<String> unknownProcesses(){
        return comms.values()
                .stream()
                .flatMap(v -> ((List<String>) v).stream().distinct())
                .filter(v -> !comms.containsKey(v)).toList();
    }

    // check that all the recursive variables called in behaviours are defined
    public List<String> unknownVariables(){
        return recvar2proc.keySet().stream().filter(vari -> !recVars.contains(vari)).toList();
    }

    @Override
    public List<String> visitRecdef(SPparserRich.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        recVars.add(ctx.getChild(0).getText());
        String process = recvar2proc.get(ctx.getChild(0).getText());
        currentProcess = process;
        var processes = ctx.getChild(2).accept(this);
        ((List<String>) comms.get(process)).addAll(processes);
        currentProcess = null;
        return super.visitRecdef(ctx);
    }

    @Override
    public List<String> visitNetwork(SPparserRich.NetworkContext ctx) {
        // 0 : first process name
        // 1 : [
        // 2 : behaviour
        // 3 : ]
        // 4 : |
        // 5 : second process name
        var children = ctx.getChildCount();
        for(int i =0; i < children -1; i+=5){
            var name = ctx.getChild(i).getText();
            currentProcess = name;
            var processes = ctx.getChild(i +2).accept(this);
            comms.put(name, processes);
        }
        currentProcess = null;
        return null;
    }

    @Override
    public List<String> visitNon(SPparserRich.NonContext ctx) {
        return new ArrayList<>();
    }

    @Override
    public List<String> visitSom(SPparserRich.SomContext ctx) {
        return ctx.getChild(2).accept(this);
    }

    @Override
    public List<String> visitCdt(SPparserRich.CdtContext ctx) {
        // 0: If
        // 1: expr
        // 2: Then
        // 3: behaviour
        // 4: Else
        // 5: behaviour
        return super.visitCdt(ctx);
    }

    @Override
    public List<String> visitSnd(SPparserRich.SndContext ctx) {
        return visitComm(ctx);
    }
    @Override
    public List<String> visitRcv(SPparserRich.RcvContext ctx) {
        return visitComm(ctx);
    }
    public <T extends SPparserRich.BehaviourContext> List<String> visitComm(T ctx){
        // 0: dest
        // 1: !
        // 2: expr
        // 3: @
        // 4: !
        // 5: ann
        // 6: seq
        // 7: behaviour
        var procs = ctx.getChild(7).accept(this);
        procs.add(ctx.getChild(0).getText());
        return procs;
    }

    @Override
    public List<String> visitSel(SPparserRich.SelContext ctx) {
        // 0: dest
        // 1: &
        // 2: LABEL
        // 3: @
        // 4: +
        // 5: ann
        // 6: seq
        // 7: behaviour
        var procs = ctx.getChild(7).accept(this);
        procs.add(ctx.getChild(0).getText());
        return procs;
    }
    @Override
    public List<String> visitBra(SPparserRich.BraContext ctx) {
        // proc '&' '{' BLABEL ':' mBehaviour '}'  ('//'  '{' BLABEL ':' mBehaviour '}')+
        // 0: dest
        // 1: &
        // 2: {
        // 3: BLABEL
        // 4: :
        // 5: mBe
        // 6: }
        var procs = new ArrayList<String>();
        procs.add(ctx.getChild(0).getText());
        for(int i=5; i< ctx.getChildCount();i+=6){
            var a = ctx.getChild(i).accept(this);
            procs.addAll(a);
        }
        return procs;
    }


    @Override
    public List<String> visitCal(SPparserRich.CalContext ctx) {
        // 0 : Call
        // 1 : name
        this.recvar2proc.put(ctx.getChild(1).getText(),currentProcess);
        return new ArrayList<>();
    }

    @Override
    public List<String> visitEnd(SPparserRich.EndContext ctx) {
        return new ArrayList<>();
    }
}
