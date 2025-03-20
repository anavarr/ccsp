import mychor.CompilerContext;
import mychor.SPcheckerRich;
import mychor.SPlexer;
import mychor.SPparserRich;
import mychor.Session;
import mychor.types.LocalType;
import mychor.types.LocalTypeParser;
import mychor.types.LocalTypeWriter;
import mychor.types.LocalTypesLexer;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class ProgramReaderTest {
    SPcheckerRich spr = new SPcheckerRich();
    CompilerContext ctx = new CompilerContext();
    String path_prefix = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/test/antlr4/SP_programs/";
    String path_prefix_types = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/test/antlr4/types/";
    protected SPcheckerRich testFile(String filename) throws IOException {
        var path = Path.of(path_prefix, filename);
        SPlexer spl = new SPlexer(CharStreams.fromPath(path));
        var spp = new SPparserRich(new CommonTokenStream(spl));
        var spc = new SPcheckerRich();
        spp.program().accept(spc);
        return spc;
    }

    protected LocalType readType(String filename) throws IOException {
        var path = Path.of(path_prefix_types, filename);
        LocalTypesLexer ltl = new LocalTypesLexer(CharStreams.fromPath(path));
        var ltp = new LocalTypeParser(new CommonTokenStream(ltl));
        var writer = new LocalTypeWriter();
        return ltp.localtype().accept(writer);
    }
}
