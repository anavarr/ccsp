import mychor.MessagePatternLexer;
import mychor.MessagePatternParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

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
}
