import mychor.Communication;
import mychor.SPcheckerRich;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class SPcheckerTest {
    SPcheckerRich spr = new SPcheckerRich();
    SPcheckerRich.Context ctx = new SPcheckerRich.Context();

    @Test
    public void exampleNoEmptyAnn(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE))));
        ctx.sessions.add(new Session("server", "client",
                List.of(new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE))));
        assert spr.unknownProcesses().size() == 0;
    }
    @Test
    public void exampleEmptyAnn(){
        spr.compilerCtx = ctx;
        ctx.sessions.add(new Session("client", "server",
                List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE))));
        ctx.sessions.add(new Session("server", "client",
                List.of(new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE))));
        ctx.sessions.add(new Session("client", "auth",
                List.of(new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE),
                        new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE))));
        assert spr.unknownProcesses().size() == 1;
    }
}
