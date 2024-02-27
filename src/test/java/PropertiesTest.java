import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertiesTest extends ProgramReaderTest{

    @Test
    public void OAuth2IsSafe() throws IOException {
        var spc = testFile("OAuth2_fragment.sp");
        assertTrue(spc.typeSafety());
    }
    @Test
    public void OAuth2NonSafeIsNotSafe() throws IOException {
        var spc = testFile("OAuth2_fragment_nonsafe.sp");
        assertFalse(spc.typeSafety());
    }
}
