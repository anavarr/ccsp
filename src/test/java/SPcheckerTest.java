import mychor.Communication;
import mychor.SPcheckerRich;
import mychor.SPlexer;
import mychor.SPparserRich;
import mychor.Session;
import mychor.Utils;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SPcheckerTest {
    SPcheckerRich spr = new SPcheckerRich();
    SPcheckerRich.Context ctx = new SPcheckerRich.Context();

    String path_prefix = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/test/antlr4/SP_programs/";

    @Test
    public void exampleSelfComm(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("server", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("client", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)))));

        assertFalse(spr.noSelfCom());
    }
    @Test
    public void exampleNoSelfComm(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("server", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)))));

        assertTrue(spr.noSelfCom());
    }

    private SPcheckerRich testFile(String filename) throws IOException {
        var path = Path.of(path_prefix, filename);
        SPlexer spl = new SPlexer(CharStreams.fromPath(path));
        var spp = new SPparserRich(new CommonTokenStream(spl));
        var spc = new SPcheckerRich();
        spp.program().accept(spc);
        return spc;
    }
    @Test
    public void exampleNoUnknownProcess(){
        try{
            var spc = testFile("noEmptyAnn.sp");
            assertEquals(spc.unknownProcesses().size(), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void exampleUnknownProcess(){
        try{
            var spc = testFile("emptyAnn.sp");
            assertEquals(spc.unknownProcesses().size(), 1);
            assertEquals(spc.unknownProcesses().get(0), "proxy");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exampleUnknownVariable(){
        assertTrue(false);
    }

    @Test
    public void exampleNoUnknownVariable(){
        assertTrue(false);
    }
}
