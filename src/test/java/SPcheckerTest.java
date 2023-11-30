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
    public void exampleNoUnknownProcess() throws IOException {
        var spc = testFile("noUnknownProcess.sp");
        assertEquals(spc.unknownProcesses().size(), 0);
    }
    @Test
    public void exampleUnknownProcess() throws IOException {
        var spc = testFile("unknownProcess.sp");
        assertEquals(spc.unknownProcesses().size(), 1);
        assertEquals(spc.unknownProcesses().get(0), "proxy");
    }

    // UNKNOWN AND UNUSED RECURSIVE VARIABLES
    @Test
    public void exampleUnknownVariable() throws IOException {
        var spc = testFile("unknownVariable.sp");
        assertEquals(spc.unknownVariables().size(), 1);
        assertEquals(spc.unknownVariables().get(0), "Client");
    }
    @Test
    public void exampleUnknownVariableBra() throws IOException {
        var spc = testFile("unknownVariableBra.sp");
        assertEquals(spc.unknownVariables().size(), 1);
        assertEquals(spc.unknownVariables().get(0), "Client");
    }
    @Test
    public void exampleUnknownVariableComm() throws IOException {
        var spc = testFile("unknownVariableComm.sp");
        assertEquals(spc.unknownVariables().size(), 1);
        assertEquals(spc.unknownVariables().get(0), "Routine");
    }
    @Test
    public void exampleUnknownVariableCdt() throws IOException {
        var spc = testFile("unknownVariableComm.sp");
        assertEquals(spc.unknownVariables().size(), 1);
        assertEquals(spc.unknownVariables().get(0), "Routine");
    }
    @Test
    public void exampleNoUnknownVariable() throws IOException {
        var spc = testFile("noUnknownVariable.sp");
        assertEquals(spc.unknownVariables().size(), 0);
    }
    @Test
    public void unusedVariableComm() throws IOException {
        var spc = testFile("nullProcessInComm.sp");
        assertEquals(spc.compilerCtx.errors.size(), 1);
    }
    @Test
    public void unusedVariableBraComm() throws IOException {
        var spc = testFile("nullProcessInBraComm.sp");
        for (String error : spc.compilerCtx.errors) {
            System.out.println(error);
        }
        assertEquals(spc.compilerCtx.errors.size(), 4);
    }



    @Test
    public void complementarySessionsSndRcv() throws IOException {
        var spc = testFile("complementarySessionsSndRcv.sp");
        assertEquals(spc.getNonComplementarySessions().size(), 0);
    }
    @Test
    public void complementarySessionsSelectBranch() throws IOException {
        var spc = testFile("complementarySessionsSelectBranch.sp");
        assertEquals(spc.getNonComplementarySessions().size(), 0);
    }
    @Test
    public void nonComplementarySessionsSelectBranchLabels() throws IOException {
            var spc = testFile("nonComplementarySessionsSelectBranchLabels.sp");
            assertEquals(spc.getNonComplementarySessions().size(), 2);
    }
    @Test
    public void nonComplementarySessionsSelectBranchNumbers() throws IOException {
            var spc = testFile("nonComplementarySessionsSelectBranchNumbers.sp");
            assertEquals(spc.getNonComplementarySessions().size(), 2);
    }
    @Test
    public void complementaryComplexSession() throws IOException {
            var spc = testFile("complementaryComplexSession.sp");
            assertEquals(spc.getNonComplementarySessions().size(), 2);
    }
    @Test
    public void sessionBranchingValidCdtBranch() throws IOException {
        var spc = testFile("sessionBranchingValid1.sp");
        assertTrue(spc.sessionsBranchingAreValid());
    }
    @Test
    public void sessionBranchingValidCdtSelect() throws IOException {
            var spc = testFile("sessionBranchingValid2.sp");
            assertTrue(spc.sessionsBranchingAreValid());
    }
    @Test
    public void sessionBranchingValidCdtSame() throws IOException {
        var spc = testFile("sessionBranchingValid3.sp");
        assertTrue(spc.sessionsBranchingAreValid());
    }
    @Test
    public void sessionBranchingValidNoBranching() throws IOException {
        var spc = testFile("sessionBranchingValid4.sp");
        assertTrue(spc.sessionsBranchingAreValid());
    }
    @Test
    public void sessionBranchingNotValid() throws IOException {
        var spc = testFile("sessionBranchingNotValid.sp");
        assertFalse(spc.sessionsBranchingAreValid());
    }



    @Test
    public void loop() throws IOException {
        var spc = testFile("loop.sp");
    }
    @Test
    public void loopBra() throws IOException {
        var spc = testFile("loopBra.sp");
    }
    @Test
    public void loopCdt() throws IOException {
        var spc = testFile("loopCdt.sp");
    }
}
