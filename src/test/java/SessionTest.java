import mychor.Communication;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionTest {

    Session a = new Session("client", "server", new ArrayList<>());
    Session b = new Session("client", "server", new ArrayList<>(List.of(
            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE))
    ));

    Session clientSide = new Session("client", "server", new ArrayList<>(List.of(
            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE),
            new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)
    )));
    Session serverSide = new Session("server", "client", new ArrayList<>(List.of(
            new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE),
            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)
    )));

    @Test
    public void sessionValidityIsCommutative(){
        assertTrue(a.areEnds("client", "server"));
        assertTrue(a.areEnds("client", "server"));
        assertTrue(a.areEnds("server", "client"));
    }

    @Test
    public void sessionValidityIsNotReflexive(){
        assertFalse(a.areEnds("client", "client"));
        assertFalse(a.areEnds("server", "server"));
    }

    @Test
    public void sessionValidityRejects(){
        assertFalse(a.areEnds("client", "proxy"));
        assertFalse(a.areEnds("proxy", "server"));
        assertFalse(a.areEnds("server", "proxy"));
        assertFalse(a.areEnds("proxy", "client"));
    }

    @Test
    public void sessionFirstSenderIsInitiator(){
        var presence = b.getInitiator();
        assertTrue(presence.isPresent() & presence.get().equals("client"));
    }

    @Test
    public void nullCommunicationsReturnEmptyInitiator(){
        assertTrue(a.getInitiator().isEmpty());
    }

    @Test
    public void sessionComplementarityExample(){
        assertTrue(clientSide.isComplementary(serverSide));
    }

    @Test
    public void sessionNonComplementarityExample(){
        assertFalse(b.isComplementary(serverSide));
    }
}
