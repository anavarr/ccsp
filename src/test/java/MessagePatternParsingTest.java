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
    public void testWrongPatternTitleShouldFail() throws IOException {
        var spp = testFile("pattern_title_error.txt");
        spp.pattern();
        assertEquals(spp.getNumberOfSyntaxErrors(), 1);
    }
    @Test
    public void testWrongPatternSequentShouldFail() throws IOException {
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
        assertEquals(map.keySet().size(), 14);
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
        assertEquals(map.get("CHOICE_a"), choice_b);
    }

    @Test
    public void repetitionPatternShouldReturnMultiRootSessions() throws IOException {
        var spp = testFile("repetition_pattern.txt");
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        var send = new Communication(Utils.Direction.SEND);
        send.addLeafCommunicationRoots(new ArrayList<>(List.of(
                new Communication(Utils.Direction.VOID),
                send
        )));
        var recv = new Communication(Utils.Direction.RECEIVE);
        recv.addLeafCommunicationRoots(new ArrayList<>(List.of(
                recv,
                new Communication(Utils.Direction.VOID)
        )));
        Session repetition_a = new Session("a", "b", send);
        repetition_a.expandTopCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.VOID))));
        Session repetition_b = new Session("b", "a", recv);
        repetition_b.expandTopCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.VOID))));
        assertTrue(map.containsKey("Repetition_a"));
        assertTrue(map.containsKey("Repetition_b"));
        assertEquals(map.get("Repetition_a"), repetition_a);
        assertEquals(map.get("Repetition_a"), repetition_b);
    }

    @Test
    public void atLeastOncePatternShouldReturnMultiRootSessions() throws IOException {
        var spp = testFile("repetition_at_least_once.txt");
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var map = mpm.getSessionsMap();
        var send = new Communication(Utils.Direction.SEND);
        send.addLeafCommunicationRoots(new ArrayList<>(List.of(send, new Communication(Utils.Direction.VOID))));
        System.out.println(send);
        var recv = new Communication(Utils.Direction.RECEIVE);
        recv.addLeafCommunicationRoots(new ArrayList<>(List.of(recv, new Communication(Utils.Direction.VOID))));
        Session repetition_a = new Session("alice", "bob", send);
        Session repetition_b = new Session("bob", "alice", recv);
        assertTrue(map.containsKey("AtLeastOnce_alice"));
        assertTrue(map.containsKey("AtLeastOnce_bob"));
        assertEquals(map.get("AtLeastOnce_alice"), repetition_a);
        assertEquals(map.get("AtLeastOnce_bob"), repetition_b);
    }
}