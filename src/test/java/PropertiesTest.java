import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertiesTest extends ProgramReaderTest{

    @Test
    public void OAuth2IsSafe() throws IOException {
        var spc = testFile("OAuth2_fragment.sp");
        assertFalse(spc.typeSafety().contains(false));
    }
    @Test
    public void OAuth2NonSafeIsNotSafe() throws IOException {
        var spc = testFile("OAuth2_fragment_nonsafe.sp");
        var tsResults = spc.typeSafety();
        assertEquals(tsResults.size(), 1);
        assertTrue(tsResults.contains(false));
    }
    @Test
    public void OAuth2DeadlockFreeAsync() throws IOException {
        var spc = testFile("OAuth2_fragment_async.sp");
        assertFalse(spc.deadlockFreedom().contains(false));
    }

    @Test
    public void ThreeBuyerProtoolIsSafe() throws IOException {
        var spc = testFile("Three_buyer_protocol.sp");
        assertFalse(spc.typeSafety().contains(false));
    }

    @Test
    public void ThreeBuyerProtoolIsDeadlockFree() throws IOException {
        var spc = testFile("Three_buyer_protocol.sp");
        assertFalse(spc.deadlockFreedom().contains(false));
    }
}
