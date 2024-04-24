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
    public void simpleChainShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("simple_message_chain.sp");
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
    public void simpleBranchingShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("simple_branching_1_layer.sp");
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
    public void simpleMessageThenBranchingShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("simple_message_then_branching.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionClient = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SELECT, "\"left\""),
                    new Communication(Utils.Direction.SELECT, "\"right\"")))
                )
        )));
        var sessionServer = new Session("server", "client", new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                    new Communication(Utils.Direction.BRANCH, "\"left\""),
                    new Communication(Utils.Direction.BRANCH, "\"right\"")))
                )
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client") && s.peerB().equals("server")).findFirst().get(), sessionClient);
        assertEquals(sessions.stream().filter(s->s.peerA().equals("server") && s.peerB().equals("client")).findFirst().get(), sessionServer);
    }
    @Test
    public void simpleBranchingThenMessageShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("simple_branching_then_message.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionClient = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.SELECT,
                        new ArrayList<>(List.of(new Communication(Utils.Direction.SEND))),
                        "\"left\""),
                new Communication(Utils.Direction.SELECT, "\"right\"")))
        );
        var sessionServer = new Session("server", "client", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,
                        new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE))),
                        "\"left\""),
                new Communication(Utils.Direction.BRANCH, "\"right\"")))
        );
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client") && s.peerB().equals("server")).findFirst().get(), sessionClient);
        assertEquals(sessions.stream().filter(s->s.peerA().equals("server") && s.peerB().equals("client")).findFirst().get(), sessionServer);
    }
}
