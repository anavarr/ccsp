package mychor;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SPChecker extends SPparserBaseVisitor<List<String>>{
    HashMap<String, String> recvar2proc = new HashMap();
    HashMap comms = new HashMap<String, List<String>>();
    String currentProcess;

    public HashMap<String, List<String>> processCommunicationsMap(){
        return comms;
    }

    @Override
    public List<String> visitRecdef(SPparser.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        String process = recvar2proc.get(ctx.getChild(0).getText());
        currentProcess = process;
        var processes = ctx.getChild(2).accept(this);
        ((List<String>) comms.get(process)).addAll(processes);
        currentProcess = null;
        return super.visitRecdef(ctx);
    }

    @Override
    public List<String> visitNetwork(SPparser.NetworkContext ctx) {
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
    public List<String> visitSnd(SPparser.SndContext ctx) {
        return visitComm(ctx);
    }
    @Override
    public List<String> visitRcv(SPparser.RcvContext ctx) {
        return visitComm(ctx);
    }
    public <T extends SPparser.BehaviourContext> List<String> visitComm(T ctx){
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
    public List<String> visitSel(SPparser.SelContext ctx) {
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
    public List<String> visitBra(SPparser.BraContext ctx) {
        // 0: dest
        // 1: &
        // 2: mBe1
        // 3: //
        // 4: mBe2
        var procs = ctx.getChild(2).accept(this);
        var procs2 = ctx.getChild(4).accept(this);
        procs.addAll(procs2);
        procs.add(ctx.getChild(0).getText());
        return procs;
    }


    @Override
    public List<String> visitCal(SPparser.CalContext ctx) {
        // 0 : Call
        // 1 : name
        this.recvar2proc.put(ctx.getChild(1).getText(),currentProcess);
        return new ArrayList<>();
    }

    @Override
    public List<String> visitEnd(SPparser.EndContext ctx) {
        return new ArrayList<>();
    }

}
