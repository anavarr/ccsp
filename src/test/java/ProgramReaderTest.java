import mychor.CompilerContext;
import mychor.SPcheckerRich;
import mychor.SPlexer;
import mychor.SPparserRich;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;

public class ProgramReaderTest {
    SPcheckerRich spr = new SPcheckerRich();
    CompilerContext ctx = new CompilerContext();
    String path_prefix = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/test/antlr4/SP_programs/";
    protected SPcheckerRich testFile(String filename) throws IOException {
        var path = Path.of(path_prefix, filename);
        SPlexer spl = new SPlexer(CharStreams.fromPath(path));
        var spp = new SPparserRich(new CommonTokenStream(spl));
        var spc = new SPcheckerRich();
        spp.program().accept(spc);
        return spc;
    }
}
