import mychor.Comm;
import mychor.Communication;
import mychor.MessagePatternLexer;
import mychor.MessagePatternMaker;
import mychor.MessagePatternParser;
import mychor.Session;
import mychor.Utils;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MessagePatternParsingTest {
    String path_prefix = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/test/antlr4/Patterns/";
    protected MessagePatternParser testFile(String filename) throws IOException {
        var path = Path.of(path_prefix, filename);
        var spl = new MessagePatternLexer(CharStreams.fromPath(path));
        var spp = new MessagePatternParser(new CommonTokenStream(spl));
        return spp;
    }

    @Test
    public void testMiscPatternParsing() throws IOException {
        var spp = testFile("misc_pattern_test.txt");
        spp.pattern();
        assertEquals(spp.getNumberOfSyntaxErrors(), 0);
    }
    @Test
    public void wrongPatternTitleShouldFail() throws IOException {
        var spp = testFile("pattern_title_error.txt");
        spp.pattern();
        assertEquals(spp.getNumberOfSyntaxErrors(), 1);
    }
    @Test
    public void simpleSequentShouldGiveLinearGraphTwoNodes() throws IOException{
        var spp = testFile("pattern_sequent.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        assertTrue(map.containsKey("REST_client"));
        assertTrue(map.containsKey("REST_server"));
        var clientCommunications = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE))));
        var serverCommunications = new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND))));

        Session client = new Session("client", "server", new ArrayList<>(List.of(clientCommunications)));
        Session server = new Session("server","client",  new ArrayList<>(List.of(serverCommunications)));
        assertEquals(map.get("REST_client"), client);
        assertEquals(map.get("REST_server"), server);
    }
    @Test
    public void wrongPatternSequentShouldFail() throws IOException {
        var spp = testFile("pattern_sequent_error.txt");
        spp.pattern();
        assertEquals(spp.getNumberOfSyntaxErrors(), 1);
    }
    @Test
    public void simpleExchangeGivesOneElementSessions() throws IOException {
        Session sa = new Session("a", "b", new Communication(Utils.Direction.SEND));
        Session sb = new Session("b", "a", new Communication(Utils.Direction.RECEIVE));
        var spp = testFile("simple_exchange.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        assertTrue(map.containsKey("SIMPLE_EXCHANGE_a"));
        assertTrue(map.containsKey("SIMPLE_EXCHANGE_b"));
        assertEquals(map.get("SIMPLE_EXCHANGE_a"), sa);
        assertEquals(map.get("SIMPLE_EXCHANGE_b"), sb);
    }
    @Test
    public void patternsListShouldReturnAllPatternNamesTwice() throws IOException {
        var spp = testFile("misc_pattern_test.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        assertEquals(map.keySet().size(), 16);
        assertTrue(map.containsKey("REST_client"));
        assertTrue(map.containsKey("REST_server"));
        assertTrue(map.containsKey("ReactiveStreams_client"));
        assertTrue(map.containsKey("ReactiveStreams_server"));
        assertTrue(map.containsKey("GRPC_un_un_client"));
        assertTrue(map.containsKey("GRPC_un_un_server"));
        assertTrue(map.containsKey("GRPC_un_st_client"));
        assertTrue(map.containsKey("GRPC_un_st_server"));
        assertTrue(map.containsKey("GRPC_st_un_client"));
        assertTrue(map.containsKey("GRPC_st_un_server"));
        assertTrue(map.containsKey("GRPC_st_st_client"));
        assertTrue(map.containsKey("GRPC_st_st_server"));
        assertTrue(map.containsKey("RandomlyIntricateOne_a"));
        assertTrue(map.containsKey("RandomlyIntricateOne_b"));
        assertTrue(map.containsKey("RandomlyIntricateOne2_a"));
        assertTrue(map.containsKey("RandomlyIntricateOne2_b"));
    }
    @Test
    public void choicePatternShouldReturnMultiRootSessions() throws IOException {
        var spp = testFile("choice_pattern.txt");
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        Session choice_a = new Session("a", "b", new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND),
                new Communication(Utils.Direction.RECEIVE)
        )));
        Session choice_b = new Session("b", "a", new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE),
                new Communication(Utils.Direction.SEND)
        )));
        assertTrue(map.containsKey("CHOICE_a"));
        assertTrue(map.containsKey("CHOICE_b"));
        assertEquals(map.get("CHOICE_a"), choice_a);
        assertEquals(map.get("CHOICE_b"), choice_b);
    }
    @Test
    public void repetitionPatternShouldReturnMultiRootSessions() throws IOException {
        var spp = testFile("repetition_pattern.txt");
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        var send = new Communication(Utils.Direction.SEND);
        send.addLeafCommunicationRoots(new ArrayList<>(List.of(send)));
        var recv = new Communication(Utils.Direction.RECEIVE);
        recv.addLeafCommunicationRoots(new ArrayList<>(List.of(recv)));
        Session repetition_a = new Session("a", "b", send);
        repetition_a.expandTopCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.VOID))));
        Session repetition_b = new Session("b", "a", recv);
        repetition_b.expandTopCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.VOID))));
        assertTrue(map.containsKey("Repetition_a"));
        assertTrue(map.containsKey("Repetition_b"));
        assertEquals(map.get("Repetition_a"), repetition_a);
        assertEquals(map.get("Repetition_b"), repetition_b);
    }
    @Test
    public void atLeastOncePatternShouldReturnMultiRootSessions() throws IOException {
        var spp = testFile("repetition_at_least_once.txt");
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        var send = new Communication(Utils.Direction.SEND);
        send.addLeafCommunicationRoots(new ArrayList<>(List.of(send)));
        var recv = new Communication(Utils.Direction.RECEIVE);
        recv.addLeafCommunicationRoots(new ArrayList<>(List.of(recv)));
        Session repetition_a = new Session("alice", "bob", send);
        Session repetition_b = new Session("bob", "alice", recv);
        assertTrue(map.containsKey("AtLeastOnce_alice"));
        assertTrue(map.containsKey("AtLeastOnce_bob"));
        assertEquals(map.get("AtLeastOnce_alice"), repetition_a);
        assertEquals(map.get("AtLeastOnce_bob"), repetition_b);
    }
    @Test
    public void checkRESTParsing() throws IOException {
        //same as simpleSequent
        var spp = testFile("REST_pattern.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        assertTrue(map.containsKey("REST_client"));
        assertTrue(map.containsKey("REST_server"));
        var clientCommunications = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE))));
        var serverCommunications = new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND))));

        Session client = new Session("client", "server", new ArrayList<>(List.of(clientCommunications)));
        Session server = new Session("server","client",  new ArrayList<>(List.of(serverCommunications)));
        assertEquals(map.get("REST_client"), client);
        assertEquals(map.get("REST_server"), server);
    }

    @Test
    public void checkGRPC_un_unParsing() throws IOException {
        //same as simpleSequent
        var spp = testFile("GRPC_un_un_pattern.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        assertTrue(map.containsKey("GRPC_un_un_client"));
        assertTrue(map.containsKey("GRPC_un_un_server"));
        var clientCommunications = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE))));
        var serverCommunications = new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND))));

        Session client = new Session("client", "server", new ArrayList<>(List.of(clientCommunications)));
        Session server = new Session("server","client",  new ArrayList<>(List.of(serverCommunications)));
        assertEquals(map.get("GRPC_un_un_client"), client);
        assertEquals(map.get("GRPC_un_un_server"), server);
    }
    @Test
    public void checkGRPC_st_unParsing() throws IOException {
        var spp = testFile("GRPC_st_un_pattern.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        assertTrue(map.containsKey("GRPC_st_un_client"));
        assertTrue(map.containsKey("GRPC_st_un_server"));
        var clientCommunications = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE))));
        clientCommunications.addRecursiveCallers(clientCommunications);

        var serverCommunications = new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND))));
        serverCommunications.addRecursiveCallers(serverCommunications);

        Session client = new Session("client", "server", new ArrayList<>(List.of(clientCommunications)));
        Session server = new Session("server","client",  new ArrayList<>(List.of(serverCommunications)));
        assertEquals(map.get("GRPC_st_un_client"), client);
        assertEquals(map.get("GRPC_st_un_server"), server);
    }

    @Test
    public void checkGRPC_un_stParsing() throws IOException {
        var spp = testFile("GRPC_un_st_pattern.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        for (Session value : map.values()) {
            value.cleanVoid();
        }
        assertTrue(map.containsKey("GRPC_un_st_client"));
        assertTrue(map.containsKey("GRPC_un_st_server"));
        var clientCommunications = new Communication(Utils.Direction.SEND);
        var clientCommunicationsContinuations = new Communication(Utils.Direction.RECEIVE);
        clientCommunicationsContinuations.addRecursiveCallers(clientCommunicationsContinuations);
        clientCommunications.addLeafCommunicationRoots(new ArrayList<>(List.of(
                clientCommunicationsContinuations,
                new Communication(Utils.Direction.VOID))));

        var serverCommunications = new Communication(Utils.Direction.RECEIVE);
        var serverCommunicationsContinuation = new Communication(Utils.Direction.SEND);
        serverCommunicationsContinuation.addRecursiveCallers(serverCommunicationsContinuation);
        serverCommunications.addLeafCommunicationRoots(new ArrayList<>(List.of(
                serverCommunicationsContinuation,
                new Communication(Utils.Direction.VOID))));

        Session client = new Session("client", "server", new ArrayList<>(List.of(clientCommunications)));
        Session server = new Session("server","client",  new ArrayList<>(List.of(serverCommunications)));
        assertEquals(map.get("GRPC_un_st_client"), client);
        assertEquals(map.get("GRPC_un_st_server"), server);
    }
    @Test
    public void checkGRPC_st_stParsing() throws IOException {
        var spp = testFile("GRPC_st_st_pattern.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        for (Session value : map.values()) {
            value.cleanVoid();
        }
        assertTrue(map.containsKey("GRPC_st_st_client"));
        assertTrue(map.containsKey("GRPC_st_st_server"));

        var commClient1 = new Communication(Utils.Direction.SEND);
        var commClient21 = new Communication(Utils.Direction.SEND);
        var commClient31 = new Communication(Utils.Direction.RECEIVE);
        var commClientVoid = new Communication(Utils.Direction.VOID);
        commClient21.addLeafCommunicationRoots(new ArrayList<>(List.of(commClient31, commClient21, commClientVoid)));
        commClient31.addLeafCommunicationRoots(new ArrayList<>(List.of(commClient21, commClient31)));
        commClient1.addLeafCommunicationRoots(new ArrayList<>(List.of(commClient21, commClient31, commClientVoid)));

        var commServer1 = new Communication(Utils.Direction.RECEIVE);
        var commServer21 = new Communication(Utils.Direction.RECEIVE);
        var commServer31 = new Communication(Utils.Direction.SEND);
        var commServerVoid = new Communication(Utils.Direction.VOID);
        commServer21.addLeafCommunicationRoots(new ArrayList<>(List.of(commServer31, commServer21, commServerVoid)));
        commServer31.addLeafCommunicationRoots(new ArrayList<>(List.of(commServer21, commServer31)));
        commServer1.addLeafCommunicationRoots(new ArrayList<>(List.of(commServer21, commServer31, commServerVoid)));


        Session client = new Session("client", "server", new ArrayList<>(List.of(commClient1)));
        Session server = new Session("server","client",  new ArrayList<>(List.of(commServer1)));
        assertEquals(map.get("GRPC_st_st_client"), client);
        assertEquals(map.get("GRPC_st_st_server"), server);
    }
    @Test
    public void checkReactiveStreamsParsing() throws IOException {
        var spp = testFile("ReactiveStreams_pattern.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        for (Session value : map.values()) {
            value.cleanVoid();
        }
        assertTrue(map.containsKey("ReactiveStreams_client"));
        assertTrue(map.containsKey("ReactiveStreams_server"));

        var commClient1 = new Communication(Utils.Direction.SEND);
        var commClient21 = new Communication(Utils.Direction.RECEIVE);
        var commClient31 = new Communication(Utils.Direction.SEND);
        var commClient41 = new Communication(Utils.Direction.RECEIVE);
        var commClientVoid = new Communication(Utils.Direction.VOID);
        commClient31.addLeafCommunicationRoots(new ArrayList<>(List.of(commClient31, commClient41, commClientVoid)));
        commClient41.addLeafCommunicationRoots(new ArrayList<>(List.of(commClient31, commClient41)));
        commClient21.addLeafCommunicationRoots(new ArrayList<>(List.of(commClient31, commClient41, commClientVoid)));
        commClient1.addLeafCommunicationRoots(new ArrayList<>(List.of(commClient21)));

        var commServer1 = new Communication(Utils.Direction.RECEIVE);
        var commServer21 = new Communication(Utils.Direction.SEND);
        var commServer31 = new Communication(Utils.Direction.RECEIVE);
        var commServer41 = new Communication(Utils.Direction.SEND);
        var commServerVoid = new Communication(Utils.Direction.VOID);
        commServer31.addLeafCommunicationRoots(new ArrayList<>(List.of(commServer31, commServer41, commServerVoid)));
        commServer41.addLeafCommunicationRoots(new ArrayList<>(List.of(commServer31, commServer41)));
        commServer21.addLeafCommunicationRoots(new ArrayList<>(List.of(commServer31, commServer41, commServerVoid)));
        commServer1.addLeafCommunicationRoots(new ArrayList<>(List.of(commServer21)));

        Session client = new Session("client", "server", new ArrayList<>(List.of(commClient1)));
        Session server = new Session("server","client",  new ArrayList<>(List.of(commServer1)));
        assertEquals(map.get("ReactiveStreams_client"), client);
        assertEquals(map.get("ReactiveStreams_server"), server);
    }
}