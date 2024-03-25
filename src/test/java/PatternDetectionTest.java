import mychor.PatternDetector;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternDetectionTest extends ProgramReaderTest{
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNRestCallsWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertFalse(detector.detectCompatibleFrameworks().contains("REST_PATTERN"));
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcUnUnWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertFalse(detector.detectCompatibleFrameworks().contains("gRPC_un_un"));
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcUnStrWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertFalse(detector.detectCompatibleFrameworks().contains("gRPC_un_st"));
    }
    @Test
    public void threeBuyersAliceStoreCantBeImplementedUsingNGrpcStrUnWithAliceClientNorServer() throws IOException {
        var ctx = testFile("Three_buyer_protocol");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertFalse(detector.detectCompatibleFrameworks().contains("gRPC_st_un"));
    }


    @Test
    public void threeBuyersAliceStoreCanBeImplementedUsingGrpcStrStrWithAliceClient() throws IOException {
        var ctx = testFile("Three_buyer_protocol");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertTrue(detector.detectCompatibleFrameworks().contains("gRPC_st_st"));
    }
    @Test
    public void threeBuyersAliceStoreCanBeImplementedUsingReactiveStreamsWithAliceClient() throws IOException {
        var ctx = testFile("Three_buyer_protocol");
        PatternDetector detector = new PatternDetector(ctx.compilerCtx);
        assertTrue(detector.detectCompatibleFrameworks().contains("REACTIVE_MESSAGING"));
    }


}
