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
import java.util.HashMap;

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
        Session sa = new Session("a", "b", new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE));
        Session sb = new Session("b", "a", new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE));
        var spp = testFile("simple_exchange.txt");
        //turn spp into a using a MessagePatternVisitor
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        var a = mpm.getSessionsMap();
        assertTrue(a.containsKey("SIMPLE_EXCHANGE_a"));
        assertTrue(a.containsKey("SIMPLE_EXCHANGE_b"));
        assertEquals(a.get("SIMPLE_EXCHANGE_a"), sa);
        assertEquals(a.get("SIMPLE_EXCHANGE_b"), sb);
    }
}
