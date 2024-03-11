import mychor.Comm;
import mychor.End;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExecutionsTest extends ProgramReaderTest{
    @Test
    public void BranchesTest()throws IOException {
        var spc = testFile("Branches.sp");
        assertEquals(spc.getExecutionPaths().size(), 12);
    }

    @Test
    public void ThreeBuyerProtocolHasFourPaths() throws IOException{
        var spc = testFile("Three_buyer_protocol.sp");
        assertEquals(spc.getExecutionPaths().size(), 4);
    }

    @Test
    public void OAuth2FragmentHasOnePaths() throws IOException{
        var spc = testFile("OAuth2_fragment.sp");
        assertEquals(spc.getExecutionPaths().size(), 1);
    }

    @Test
    public void OAuth2FragmentAsyncHasTwoPaths() throws IOException{
        var spc = testFile("OAuth2_fragment_async.sp");
        assertEquals(spc.getExecutionPaths().size(), 2);
    }
}
