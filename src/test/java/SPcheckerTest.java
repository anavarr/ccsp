import mychor.Communication;
import mychor.SPcheckerRich;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SPcheckerTest {
    SPcheckerRich spr = new SPcheckerRich();
    SPcheckerRich.Context ctx = new SPcheckerRich.Context();

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

    @Test
    public void exampleNoEmptyAnn(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("server", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)))));
        assertEquals(spr.unknownProcesses().size(), 0);
    }
    @Test
    public void exampleEmptyAnn(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("server", "client",
                new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)))));
        ctx.sessions.add(new Session("client", "auth",
                new ArrayList<>(List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE),
                        new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)))));
        assertEquals(spr.unknownProcesses().size(),1);
    }
}
