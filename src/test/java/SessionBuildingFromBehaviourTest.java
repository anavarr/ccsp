import mychor.Communication;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionBuildingFromBehaviourTest extends ProgramReaderTest{

    @Test
    public void simpleChainShouldGenerateCorrectSession() throws IOException {
        var spc = testFile("simple_message_chain.sp");
        var sessions = Session.fromBehaviours(spc.compilerCtx.behaviours);
        var sessionClient = new Session("client", "server",
                new Communication(Utils.Direction.SEND,
                        new Communication(Utils.Direction.RECEIVE,
                                new Communication(Utils.Direction.RECEIVE,
                                        new Communication(Utils.Direction.SEND)))));
        var sessionServer = new Session("server", "client",
                new Communication(Utils.Direction.RECEIVE,
                        new Communication(Utils.Direction.SEND,
                                new Communication(Utils.Direction.SEND,
                                        new Communication(Utils.Direction.RECEIVE)))));
        assertTrue(sessions.containsAll(List.of(sessionClient, sessionServer)));
        assertEquals(sessions.size(), 2);
    }
}
