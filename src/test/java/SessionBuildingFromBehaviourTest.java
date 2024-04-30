import mychor.Behaviour;
import mychor.Call;
import mychor.Cdt;
import mychor.Comm;
import mychor.Communication;
import mychor.Session;
import mychor.Session.SmallContext;
import mychor.Utils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
                new Communication(Utils.Direction.SELECT,"\"success\""),
                new Communication(Utils.Direction.VOID)
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void cdtThenSelectLeftShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_select_left.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.SELECT,"\"success\""),
                new Communication(Utils.Direction.VOID)
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void cdtThenBranchingRightShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_branching_right.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,"\"right\""),
                new Communication(Utils.Direction.BRANCH,"\"left\""),
                new Communication(Utils.Direction.VOID)
        )));
        assertEquals(sessions.stream().filter(s->s.peerA().equals("client")).findFirst().get(), session);
    }
    @Test
    public void cdtThenBranchingLeftShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_branching_left.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var session = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.BRANCH,"\"right\""),
                new Communication(Utils.Direction.BRANCH,"\"left\""),
                new Communication(Utils.Direction.VOID)
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
    @Nested
    class SmallContextTest{
        @Test
        public void simpleMergeHorizontalSameSession(){
            var session1 = new Session("a","b", new Communication(Utils.Direction.BRANCH, "GET"));
            var session2 = new Session("a","b", new Communication(Utils.Direction.BRANCH, "POST"));
            var session3 = new Session("a","b", new Communication(Utils.Direction.BRANCH, "DELETE"));
            var session4 = new Session("a","b", new Communication(Utils.Direction.BRANCH, "PUT"));
            var ctx1 = new SmallContext(session1);
            var ctx2 = new SmallContext(session2);
            var ctx3 = new SmallContext(session3);
            var ctx4 = new SmallContext(session4);
            var newCtx = SmallContext.mergeHorizontal(new ArrayList<>(List.of(ctx1, ctx2, ctx3, ctx4)));
            assertNotNull(newCtx);
            assertEquals(newCtx.sessions.size(), 1);
            var session = newCtx.sessions.get(0);
            assertEquals(session.getInitiator(), "b");
            assertEquals(session.getInitiated(), "a");
            assertEquals(session.communicationsRoots().size(), 4);
            assertTrue(session.communicationsRoots().contains(new Communication(Utils.Direction.BRANCH, "GET")));
            assertTrue(session.communicationsRoots().contains(new Communication(Utils.Direction.BRANCH, "POST")));
            assertTrue(session.communicationsRoots().contains(new Communication(Utils.Direction.BRANCH, "DELETE")));
            assertTrue(session.communicationsRoots().contains(new Communication(Utils.Direction.BRANCH, "PUT")));
        }
        @Test
        public void simpleMergeHorizontalDifferentSessions(){
            var session1 = new Session("a","b", new Communication(Utils.Direction.BRANCH, "GET"));
            var session2 = new Session("a","b", new Communication(Utils.Direction.BRANCH, "POST"));
            var session3 = new Session("a","b", new Communication(Utils.Direction.BRANCH, "DELETE"));
            var session31 = new Session("a", "c", new Communication(Utils.Direction.SEND, new Communication(Utils.Direction.RECEIVE)));
            var session4 = new Session("a","b", new Communication(Utils.Direction.BRANCH, "PUT"));
            var ctx1 = new SmallContext(session1);
            var ctx2 = new SmallContext(session2);
            var ctx3 = new SmallContext(List.of(session3, session31));
            var ctx4 = new SmallContext(session4);
            var newCtx = SmallContext.mergeHorizontal(new ArrayList<>(List.of(ctx1, ctx2, ctx3, ctx4)));
            assertNotNull(newCtx);
            assertEquals(newCtx.sessions.size(), 2);
            var sessions = newCtx.sessions.stream().filter(s -> s.peerB().equals("b")).findFirst().get();
            var sessions2 = newCtx.sessions.stream().filter(s -> s.peerB().equals("c")).findFirst().get();
            assertEquals(sessions.getInitiator(), "b");
            assertEquals(sessions.getInitiated(), "a");
            assertEquals(sessions2.getInitiator(), "a");
            assertEquals(sessions2.getInitiated(),"c");

            assertEquals(sessions.communicationsRoots().size(), 4);
            assertEquals(sessions2.communicationsRoots().size(), 1);
            assertTrue(sessions.communicationsRoots().contains(new Communication(Utils.Direction.BRANCH, "GET")));
            assertTrue(sessions.communicationsRoots().contains(new Communication(Utils.Direction.BRANCH, "POST")));
            assertTrue(sessions.communicationsRoots().contains(new Communication(Utils.Direction.BRANCH, "DELETE")));
            assertTrue(sessions.communicationsRoots().contains(new Communication(Utils.Direction.BRANCH, "PUT")));
            assertEquals(sessions2.communicationsRoots().get(0), new Communication(Utils.Direction.SEND, new Communication(Utils.Direction.RECEIVE)));
        }
        @Test
        public void simpleChainBranchMergePrevious(){
            var parentSm = new SmallContext(new Session("a","b", new Communication(Utils.Direction.SEND)));
            var newSessions = new ArrayList<>(List.of(
                    new Session("a","b", new Communication(Utils.Direction.BRANCH, "GET"))));
            var sm = new SmallContext(newSessions, null, null, parentSm);
            sm.mergeWithPrevious();
            assertEquals(parentSm.sessions.size(), 1);
            assertEquals(parentSm.sessions.get(0), new Session("a","b", new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.BRANCH, "GET"))));
        }
        @Test
        public void simpleMergeHorizontalSeveralVarCalls(){
            var ctx1 = new SmallContext();
            ctx1.calledVariables.add("X1");
            var ctx2 = new SmallContext();
            ctx2.calledVariables.addAll(List.of("X1", "X2", "X3"));
            var ctx3 = new SmallContext();
            ctx3.calledVariables.addAll(List.of("X5", "X4"));
            var newCtx=SmallContext.mergeHorizontal(new ArrayList<>(List.of(ctx1, ctx2, ctx3)));
            assertEquals(newCtx.calledVariables.size(), 5);
            assertTrue(newCtx.calledVariables.containsAll(List.of("X1", "X2", "X3", "X4", "X5")));
        }
        @Test
        public void simpleMergeVerticalSeveralVarCalls(){
            var ctx1 = new SmallContext();
            ctx1.calledVariables.add("X1");
            var ctx2 = new SmallContext();
            ctx2.calledVariables.addAll(List.of("X4", "X2", "X3"));
            ctx2.previousContext = ctx1;
            ctx2.mergeWithPrevious();
            assertEquals(ctx1.calledVariables.size(), 4);
            assertTrue(ctx1.calledVariables.containsAll(List.of("X1", "X2", "X3", "X4")));
        }
    }
    @Test
    public void recursiveCallOnBothBranchesConditionalShouldProduceCorrectSessions() throws IOException {
        var spc = testFile("behavioursCombinations/cdt_recursive_both.sp");
        var sessions = spc.compilerCtx.sessions;
        var com = new Communication(Utils.Direction.SEND);
        com.addRecursiveCallee(com);
        assertEquals(sessions.get(0), new Session("proc", "client", new ArrayList(List.of(
                new Communication(Utils.Direction.RECEIVE, com),
                com
        ))));
    }
}
