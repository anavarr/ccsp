import mychor.SPcheckerRich;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

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

    @Test
    public void IntricateBranchingEqualsSetOfSimpleProcesses() throws IOException{
        var spc = testFile("branching_paths/intricate_branching.sp");
        var subbranches  = new ArrayList<SPcheckerRich>();
        var i = 13;
        for (int i1 = 1; i1 < i; i1++) {
            subbranches.add(testFile("branching_paths/intricate_branching_comb_"+i1+".sp"));
            assertTrue(spc.getExecutionPaths().containsAll(subbranches.get(i1-1).getExecutionPaths()));
        }
    }
}
