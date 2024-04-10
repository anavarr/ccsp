import mychor.PatternDetector;
import mychor.Session;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternDetectionTest extends ProgramReaderTest{
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNRestCallsWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcUnUnWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcUnStrWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcStrUnWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
    }
    @Test
    public void threeBuyersAliceStoreCanBeImplementedUsingGrpcStrStrWithAliceClient() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp").compilerCtx;
        PatternDetector detector = new PatternDetector(ctx);
        var cf = detector.detectCompatibleFrameworks();
        assertTrue(frameworkIsCompatibleForSessionPeers(ctx.sessions, "alice", "store",
                "GRPC_st_st_client", cf));
        assertTrue(frameworkIsCompatibleForSessionPeers(ctx.sessions, "store","alice",
                "GRPC_st_st_server", cf));
    }
    @Test
    public void threeBuyersAliceStoreCanBeImplementedUsingReactiveStreamsWithAliceClient() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp").compilerCtx;
        PatternDetector detector = new PatternDetector(ctx);
        var cf = detector.detectCompatibleFrameworks();
        assertTrue(frameworkIsCompatibleForSessionPeers(ctx.sessions, "alice", "store",
                "ReactiveStreams_client", cf));
        assertTrue(frameworkIsCompatibleForSessionPeers(ctx.sessions, "store","alice",
                "ReactiveStreams_server", cf));
    }
    boolean frameworkIsCompatibleForSessionPeers(List<Session> sessions, String peerA, String peerB,
                                                 String framework, Map<Session, List<String>> frameworks){
        var sessionOptional = sessions.stream().filter(s -> s.peerA().equals(peerA) && s.peerB().equals(peerB))
                .findFirst();
        if(sessionOptional.isEmpty()) return false;
        var session = sessionOptional.get();
        return (frameworks.get(session).contains(framework));
    }
}
