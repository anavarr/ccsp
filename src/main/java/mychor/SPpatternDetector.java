package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SPpatternDetector extends SPparserRichBaseVisitor<List<String>>{

    List<Session> sessions = new ArrayList<>();
    String currentProcess;
    @Override
    public List<String> visitProgram(SPparserRich.ProgramContext ctx) {
        return super.visitProgram(ctx);
    }

    @Override
    public List<String> visitNetwork(SPparserRich.NetworkContext ctx) {
        // 0 : first process name
        // 1 : [
        // 2 : behaviour
        // 3 : ]
        // 4 : |
        // 5 : second process name
        // 6 : [
        // 7 : behaviour
        // 8 : ]
        var children = ctx.children.size();
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
    public List<String> visitRecdef(SPparserRich.RecdefContext ctx) {
        // 0 : name
        // 1 : ':'
        // 2 : behaviour
        return super.visitRecdef(ctx);
    }
}
