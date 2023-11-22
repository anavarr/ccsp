import mychor.Communication;
import mychor.CompilerContext;
import mychor.SPcheckerRich;
import mychor.SPlexer;
import mychor.SPparserRich;
import mychor.Session;
import mychor.Utils;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SPcheckerTest {
    SPcheckerRich spr = new SPcheckerRich();
    CompilerContext ctx = new CompilerContext();
    String path_prefix = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/test/antlr4/SP_programs/";
    private SPcheckerRich testFile(String filename) throws IOException {
        var path = Path.of(path_prefix, filename);
        SPlexer spl = new SPlexer(CharStreams.fromPath(path));
        var spp = new SPparserRich(new CommonTokenStream(spl));
        var spc = new SPcheckerRich();
        spp.program().accept(spc);
        return spc;
    }

    @Test
    public void exampleSelfComm(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("server", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("client", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)))));

        assertFalse(spr.noSelfCom());
    }
    @Test
    public void exampleNoSelfComm(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("server", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)))));

        assertTrue(spr.noSelfCom());
    }
    @Test
    public void exampleNoUnknownProcess(){
        try{
            var spc = testFile("noUnknownProcess.sp");
            assertEquals(spc.unknownProcesses().size(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void exampleUnknownProcess(){
        try{
            var spc = testFile("unknownProcess.sp");
            assertEquals(spc.unknownProcesses().size(), 1);
            assertEquals(spc.unknownProcesses().get(0), "proxy");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exampleUnknownVariable(){
        try {
            var spc = testFile("unknownVariable.sp");
            assertEquals(spc.unknownVariables().size(), 1);
            assertEquals(spc.unknownVariables().get(0), "Client");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void exampleNoUnknownVariable(){
        try {
            var spc = testFile("noUnknownVariable.sp");
            assertEquals(spc.unknownVariables().size(), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void exampleProcessNullInComm(){
        try {
            var spc = testFile("nullProcessInComm.sp");
            assertEquals(spc.compilerCtx.errors.size(), 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void exampleProcessNullInBraComm(){
        try {
            var spc = testFile("nullProcessInBraComm.sp");
            for (String error : spc.compilerCtx.errors) {
                System.out.println(error);
            }
            assertEquals(spc.compilerCtx.errors.size(), 4);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void complementarySessionsSndRcv(){
        try{
            var spc = testFile("complementarySessionsSndRcv.sp");
            assertEquals(spc.getNonComplementarySessions().size(), 0);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    @Test
    public void complementarySessionsSelectBranch(){
        try{
            var spc = testFile("complementarySessionsSelectBranch.sp");
            assertEquals(spc.getNonComplementarySessions().size(), 0);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    @Test
    public void nonComplementarySessionsSelectBranchLabels(){
        try{
            var spc = testFile("nonComplementarySessionsSelectBranchLabels.sp");
            assertEquals(spc.getNonComplementarySessions().size(), 2);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    @Test
    public void nonComplementarySessionsSelectBranchNumbers(){
        try{
            var spc = testFile("nonComplementarySessionsSelectBranchNumbers.sp");
            assertEquals(spc.getNonComplementarySessions().size(), 2);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    @Test
    public void complementaryComplexSession(){
        try{
            var spc = testFile("complementaryComplexSession.sp");
            assertEquals(spc.getNonComplementarySessions().size(), 2);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
