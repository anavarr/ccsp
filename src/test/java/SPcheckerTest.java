import mychor.Communication;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SPcheckerTest extends ProgramReaderTest{


    // SELF COMM
    @Test
    public void exampleSelfComm(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND)))));
        ctx.sessions.add(new Session("server", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE)))));
        ctx.sessions.add(new Session("client", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND)))));

        assertFalse(spr.noSelfCom());
    }
    @Test
    public void exampleNoSelfComm(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND)))));
        ctx.sessions.add(new Session("server", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE)))));

        assertTrue(spr.noSelfCom());
    }


    // UNKNOWN PROCESSES
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
    public void twiceMappedVariable() throws IOException {
        var spc = testFile("twiceMappedVariable.sp");
        System.out.println(spc.compilerCtx.errors);
        assertEquals(spc.compilerCtx.errors.size(), 1);
    }



    // SESSION RELATED TESTS
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


    // RECURSION TESTS
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
    @Test
    public void loopBraCall() throws IOException {
        var spc = testFile("loopBraCdt.sp");
    }

}
