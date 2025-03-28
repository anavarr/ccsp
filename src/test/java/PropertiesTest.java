import mychor.types.LocalType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertiesTest extends ProgramReaderTest{

    @Test
    public void knowledgeofChoiceOfAsymmetricConditionShouldReturnFalse() throws IOException{
        var spc = testFile("branching_paths/asymmetric_branching.sp");
        var knowledgeTable = spc.knowledgeOfChoice();
        knowledgeTable.forEach((pr, type) -> {
            System.out.println(pr+":"+type);
        });
    }

    @Test
    public void asymmetricCdtShouldBeTypeSafe() throws IOException {
        var spc = testFile("branching_paths/asymmetric_branching.sp");
        assertTrue(spc.typeSafetyLocalType());
    }
    @Test
    public void asymmetricCdtShouldNotBeDeadlockFree() throws IOException {
        var spc = testFile("branching_paths/asymmetric_branching.sp");
        assertFalse(spc.deadlockFreedomLocalType());
    }

    @Test
    public void test() throws IOException {
        var spc = testFile("branching_paths/asymmetric_branching.sp");
        var res = spc.deadlockFreedom();
        System.out.println(res);
    }

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

    // Local Types
    @Nested
    public class LocalTypeTests{

        @Test
        public void OAuth2LocalTypeExists() throws IOException {
            var spc = testFile("OAuth2_fragment.sp");
            spc.compilerCtx.behaviours.forEach((pr, bev) -> {
                assertDoesNotThrow(()  -> LocalType.extractLocalType(bev));
            });
        }
        @Test
        public void OAuth2LocalTypeSafe() throws IOException {
            var spc = testFile("OAuth2_fragment.sp");
            assertTrue(spc.typeSafetyLocalType());
        }

        @Test
        public void OAuth2NonSafeIsTypable() throws IOException {
            var spc = testFile("OAuth2_fragment_nonsafe.sp");
            spc.compilerCtx.behaviours.forEach((pr, bev) -> {
                assertDoesNotThrow(() -> LocalType.extractLocalType(bev));
            });
        }
        @Test
        public void OAuth2NonSafeIsNotTypeSafe() throws IOException {
            var spc = testFile("OAuth2_fragment_nonsafe.sp");
            assertFalse(spc.typeSafetyLocalType());

        }
        @Test
        public void OAuth2AsyncIsTypable() throws IOException {
            var spc = testFile("OAuth2_fragment_async.sp");
            spc.compilerCtx.behaviours.forEach((pr, bev) -> {
                assertDoesNotThrow(() -> LocalType.extractLocalType(bev));
            });
        }
        @Test
        public void OAuth2AsyncIsTypeSafe() throws IOException {
            var spc = testFile("OAuth2_fragment_async.sp");
            assertTrue(spc.typeSafetyLocalType());
        }

        @Test
        public void ThreeBuyerProtoolIsTypable() throws IOException {
            var spc = testFile("Three_buyer_protocol.sp");
            spc.compilerCtx.behaviours.forEach((pr, bev) -> {
                assertDoesNotThrow(() -> LocalType.extractLocalType(bev));
            });
        }
        @Test
        public void ThreeBuyerProtoolIsTypeSafe() throws IOException {
            var spc = testFile("Three_buyer_protocol.sp");
            assertTrue(spc.typeSafetyLocalType());
        }
        @Test
        public void ThreeBuyerProtoolIsDeadlockFree() throws IOException {
            var spc = testFile("Three_buyer_protocol.sp");
            assertTrue(spc.deadlockFreedomLocalType());
        }
    }

    @Test
    public void nReceiveVSArbitrarySendShouldDeadlock() throws IOException {
        var spc = testFile("recursion/asymmetric_deadlock.sp");
        assertFalse(spc.deadlockFreedomLocalType());
    }

    @Test
    public void asymmetricRecursionShouldDeadlock() throws IOException {
        var spc = testFile("recursion/asymmetric_recursion.sp");
        assertFalse(spc.deadlockFreedomLocalType());
    }

    @Test
    public void testComplementaryComplexSessionShouldBeDeadLockFree() throws IOException {
        var spc = testFile("complementaryComplexSession.sp");
        assertTrue(spc.deadlockFreedomLocalType());
    }

    @Test
    public void IPProtocolShouldBeDeadlockFree() throws IOException{
        var spc = testFile("IP_protocol.sp");
        assertTrue(spc.deadlockFreedomLocalType());
    }
}
