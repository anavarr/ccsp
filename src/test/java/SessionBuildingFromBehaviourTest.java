import mychor.Communication;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionBuildingFromBehaviourTest extends ProgramReaderTest{
    @Test
    public void messageChainShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/simple_message_chain.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionClient = new Session("client", "server",
                new Communication(Utils.Direction.SEND,
                        new Communication(Utils.Direction.RECEIVE,
                                new Communication(Utils.Direction.RECEIVE,
                                        new Communication(Utils.Direction.SEND)))));
        var sessionServer = new Session("server", "client",
                new Communication(Utils.Direction.RECEIVE,
                        new Communication(Utils.Direction.SEND,
                                new Communication(Utils.Direction.SEND,
                                        new Communication(Utils.Direction.RECEIVE)))));
        assertTrue(sessions.containsAll(List.of(sessionClient, sessionServer)));
        assertEquals(sessions.size(), 2);
    }
    @Test
    public void branchingShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/simple_branching_1_layer.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionClient = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.SELECT, "\"left\""),
                new Communication(Utils.Direction.SELECT, "\"right\"")))
        );
        var sessionServer = new Session("server", "client", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH, "\"left\""),
                new Communication(Utils.Direction.BRANCH, "\"right\"")))
        );
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client") && s.peerB().equals("server")).findFirst().get(), sessionClient);
        assertEquals(sessions.stream().filter(s->s.peerA().equals("server") && s.peerB().equals("client")).findFirst().get(), sessionServer);
    }
    @Test
    public void messageThenBranchingShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/message_branching.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionServer = new Session("server", "client", new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                    new Communication(Utils.Direction.BRANCH, "\"left\""),
                    new Communication(Utils.Direction.BRANCH, "\"right\"")))
                )
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("server") && s.peerB().equals("client")).findFirst().get(), sessionServer);
    }
    @Test
    public void branchingThenMessageLeftShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/branching_message_left.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionServer = new Session("server", "client", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,
                        "\"left\"", new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE)))
                ),
                new Communication(Utils.Direction.BRANCH, "\"right\"")))
        );
        assertEquals(sessions.stream().filter(s->s.peerA().equals("server") && s.peerB().equals("client")).findFirst().get(), sessionServer);
    }
    @Test
    public void branchingThenMessageRightShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/branching_message_right.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionServer = new Session("server", "client", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,
                        "\"right\"", new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE)))
                ),
                new Communication(Utils.Direction.BRANCH, "\"left\"")))
        );
        assertEquals(sessions.stream().filter(s->s.peerA().equals("server") && s.peerB().equals("client")).findFirst().get(), sessionServer);
    }
    @Test
    public void branchingThenMessageBothShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/branching_message_both.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionServer = new Session("server", "client", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,
                        "\"right\"", new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE)))
                ),
                new Communication(Utils.Direction.BRANCH,
                        "\"left\"", new ArrayList<>(List.of(new Communication(Utils.Direction.SEND)))
                )
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("server")).findFirst().get(), sessionServer);
    }
    @Test
    public void cdtThenSelectBothShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_select_both.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
            new Communication(Utils.Direction.SELECT,"\"right\""),
            new Communication(Utils.Direction.SELECT,"\"left\"")
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void cdtThenSelectRightShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_select_right.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.SELECT,"\"success\"")
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void cdtThenSelectLeftShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_select_left.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.SELECT,"\"success\"")
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void cdtThenBranchingRightShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_branching_right.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,"\"right\""),
                new Communication(Utils.Direction.BRANCH,"\"left\"")
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void cdtThenBranchingLeftShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_branching_left.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,"\"right\""),
                new Communication(Utils.Direction.BRANCH,"\"left\"")
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void cdtThenBranchingBothShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_branching_both.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,"\"trueright\""),
                new Communication(Utils.Direction.BRANCH,"\"trueleft\""),
        new Communication(Utils.Direction.BRANCH,"\"falseright\""),
                new Communication(Utils.Direction.BRANCH,"\"falseleft\"")
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void messageThenCdtThenBranchingShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/msg_cdt_branching_msg.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new Communication(Utils.Direction.SEND,
                new ArrayList<>(List.of(
                    new Communication(Utils.Direction.BRANCH, "\"trueLeft\"", new ArrayList<>(List.of(new Communication(Utils.Direction.SEND)))),
                    new Communication(Utils.Direction.BRANCH, "\"trueRight\"", new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE)))),
                    new Communication(Utils.Direction.BRANCH, "\"falseLeft\"", new ArrayList<>(List.of(new Communication(Utils.Direction.SEND)))),
                    new Communication(Utils.Direction.BRANCH, "\"falseRight\"", new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE))))
                ))));
        assertEquals(sessions.get(0), session);
    }
    @Test
    public void branchingThenCdtThenMessageShouldGenerateCorrectSession() throws IOException{
        var spc = testFile("behavioursCombinations/branching_cdt_msg.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH, "\"left\"", new ArrayList<>(List.of(
                        new Communication(Utils.Direction.SEND),
                        new Communication(Utils.Direction.RECEIVE)
                ))),
                new Communication(Utils.Direction.BRANCH, "\"right\"", new ArrayList<>(List.of(
                        new Communication(Utils.Direction.SEND),
                        new Communication(Utils.Direction.RECEIVE)
                )))
        )));
        assertEquals(sessions.get(0), session);
    }
}
