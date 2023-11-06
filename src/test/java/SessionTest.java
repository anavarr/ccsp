import mychor.Communication;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class SessionTest {

    Session a = new Session("client", "server", new ArrayList<>());
    Session b = new Session("client", "server", List.of(
            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)
    ));

    Session clientSide = new Session("client", "server", List.of(
            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE),
            new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)
    ));
    Session serverSide = new Session("server", "client", List.of(
            new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE),
            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)
    ));

    @Test
    public void sessionValidityIsCommutative(){
        assert a.areEnds("client", "server");
        assert a.areEnds("server", "client");
    }

    @Test
    public void sessionValidityIsNotReflexive(){
        assert !a.areEnds("client", "client");
        assert !a.areEnds("server", "server");
    }

    @Test
    public void sessionValidityRejects(){
        assert !a.areEnds("client", "proxy");
        assert !a.areEnds("proxy", "server");
        assert !a.areEnds("server", "proxy");
        assert !a.areEnds("proxy", "client");
    }

    @Test
    public void sessionFirstSenderIsInitiator(){
        var presence = b.getInitiator();
        assert presence.isPresent() & presence.get().equals("client");
    }

    @Test
    public void nullCommunicationsReturnEmptyInitiator(){
        assert a.getInitiator().isEmpty();
    }

    @Test
    public void sessionComplementarityExample(){
        assert clientSide.isComplementary(serverSide);
    }

    @Test
    public void sessionNonComplementarityExample(){
        assert !b.isComplementary(serverSide);
    }
}
