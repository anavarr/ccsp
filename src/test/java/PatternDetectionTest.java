import mychor.PatternDetector;
import mychor.Session;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternDetectionTest extends ProgramReaderTest{
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNRestCallsWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertFalse(detector.detectCompatibleFrameworks().containsValue("REST_PATTERN"));
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcUnUnWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertFalse(detector.detectCompatibleFrameworks().containsValue("gRPC_un_un"));
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcUnStrWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertFalse(detector.detectCompatibleFrameworks().containsValue("gRPC_un_st"));
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcStrUnWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertFalse(detector.detectCompatibleFrameworks().containsValue("gRPC_st_un"));
    }


    @Test
    public void threeBuyersAliceStoreCanBeImplementedUsingGrpcStrStrWithAliceClient() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp").compilerCtx;
        PatternDetector detector = new PatternDetector(ctx);
        var cf = detector.detectCompatibleFrameworks();
        for (Session session : ctx.sessions) {
            if(session.peerA().equals("alice") && session.peerB().equals("store")){
                assertTrue(cf.get(session).contains("GRPC_st_st_client"));
            }
            if(session.peerA().equals("store") && session.peerB().equals("alice")){
                assertTrue(cf.get(session).contains("GRPC_st_st_server"));
            }
        }
    }
    @Test
    public void threeBuyersAliceStoreCanBeImplementedUsingReactiveStreamsWithAliceClient() throws IOException {
        var ctx = testFile("Three_buyer_protocol.sp");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        var cf = detector.detectCompatibleFrameworks();
        assertTrue(cf.containsValue("REACTIVE_MESSAGING"));
    }
}
