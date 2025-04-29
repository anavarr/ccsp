import mychor.types.LocalType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertiesTest extends ProgramReaderTest{


    // Local Types
    @Nested
    public class LocalTypeTests{
//        @Test
//        public void knowledgeofChoiceOfAsymmetricConditionShouldReturnFalse() throws IOException{
//            var spc = testFile("branching_paths/asymmetric_branching.sp");
//            var knowledgeTable = spc.knowledgeOfChoice();
//            assertTrue(knowledgeTable.get("server"));
//            assertFalse(knowledgeTable.get("client"));
//            assertTrue(knowledgeTable.get("service"));
//        }
//
        @Test
        public void OAuth2LocalTypeExists() throws IOException {
            var spc = testFile("OAuth2_fragment.sp");
            spc.compilerCtx.behaviours.forEach((pr, bev) -> {
                assertDoesNotThrow(()  -> LocalType.extractLocalType(bev));
            });
        }
//        @Test
//        public void OAuth2LocalTypeSafe() throws IOException {
//            var spc = testFile("OAuth2_fragment.sp");
//            assertTrue(spc.typeSafetyLocalType());
//        }
//
        @Test
        public void OAuth2NonSafeIsTypable() throws IOException {
            var spc = testFile("OAuth2_fragment_nonsafe.sp");
            spc.compilerCtx.behaviours.forEach((pr, bev) -> {
                assertDoesNotThrow(() -> LocalType.extractLocalType(bev));
            });
        }
//        @Test
//        public void OAuth2NonSafeIsNotTypeSafe() throws IOException {
//            var spc = testFile("OAuth2_fragment_nonsafe.sp");
//            assertFalse(spc.typeSafetyLocalType());
//
//        }
        @Test
        public void OAuth2AsyncIsTypable() throws IOException {
            var spc = testFile("OAuth2_fragment_async.sp");
            spc.compilerCtx.behaviours.forEach((pr, bev) -> {
                assertDoesNotThrow(() -> LocalType.extractLocalType(bev));
            });
        }
//
//        @Test
//        public void OAuth2AsyncIsTypeSafe() throws IOException {
//            var spc = testFile("OAuth2_fragment_async.sp");
//            assertTrue(spc.typeSafetyLocalType());
//        }
//
        @Test
        public void ThreeBuyerProtoolIsTypable() throws IOException {
            var spc = testFile("Three_buyer_protocol.sp");
            spc.compilerCtx.behaviours.forEach((pr, bev) -> {
                assertDoesNotThrow(() -> LocalType.extractLocalType(bev));
            });
        }
//        @Test
//        public void ThreeBuyerProtoolIsTypeSafe() throws IOException {
//            var spc = testFile("Three_buyer_protocol.sp");
//            assertTrue(spc.typeSafetyLocalType());
//        }
//
//        @Test
//        public void programSelectingEndFirstShouldBeDeadlocked() throws IOException {
//            var spc = testFile("recurse_vs_static_end_first.sp");
//            assertFalse(spc.deadlockFreedomLocalType());
//        }
//
//        @Test
//        public void largeCombinatoryShouldNotImpactTraversal() throws IOException {
//            var spc = testFile("too_many_selections_can_lead_to_uncomplete_unraveling.sp");
//            spc.deadlockFreedomLocalType();
//        }
//
//        @Test
//        public void starvingBranchShouldGiveMeAnError() throws IOException{
//            var spc = testFile("recursion/starving_one_branch.sp");
//            spc.deadlockFreedomLocalType();
//        }
//
//        @Test
//        public void nestedRecursiveCallsShouldBeAbleToResumeSuspension() throws IOException {
//            var spc = testFile("small_nested_select_resumption.sp");
//
//            spc.deadlockFreedomLocalType();
//            var qs = spc.getQs();
//            assertTrue(qs.get("client-server").contains(new Message(Utils.Direction.SELECT, "\"choice1\"")));
//            assertTrue(qs.get("client-server").contains(new Message(Utils.Direction.SEND, null)));
//            assertTrue(qs.get("client-server").contains(new Message(Utils.Direction.SELECT, "\"1choice2\"")));
//            assertTrue(qs.get("client-server").contains(new Message(Utils.Direction.SELECT, "\"choice2\"")));
//            assertTrue(qs.get("client-server").contains(new Message(Utils.Direction.SELECT, "\"2choice1\"")));
//            assertTrue(qs.get("client-server").contains(new Message(Utils.Direction.SELECT, "\"2choice2\"")));
//            assertTrue(qs.get("client-server").contains(new Message(Utils.Direction.SELECT, "\"end\"")));
//            assertTrue(qs.get("client-server").contains(new Message(Utils.Direction.SELECT, "\"1choice1\"")));
//        }
    }


    @Nested
    public class ExactAlgorithm{

        @Nested
        public class TypeSafety{
            @Test
            public void OAuth2NotSafeShouldNotBeSafe() throws IOException {
                var spc = testFile("OAuth2_fragment_nonsafe.sp");
                assertThrows(RuntimeException.class, spc::reduceNetwork);
            }
        }

        @Nested
        public class DeadlockFreedom{
            @Test
            public void testComplementaryComplexSessionShouldBeDeadLockFree() throws IOException {
                var spc = testFile("complementaryComplexSession.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void IPProtocolShouldBeDeadlockFree() throws IOException{
                var spc = testFile("IP_protocol.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void ThreeBuyerProtoolIsDeadlockFree() throws IOException {
                var spc = testFile("Three_buyer_protocol.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void OAuth2LocalIsDeadlockFree() throws IOException {
                var spc = testFile("OAuth2_fragment.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void OAuth2AsyncLocalIsDeadlockFree() throws IOException {
                var spc = testFile("OAuth2_fragment_async.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void nReceiveVSArbitrarySendIsDeadlockFree() throws IOException {
                var spc = testFile("recursion/asymmetric_send_waste.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void doubleSendVSSimpleReceiveShouldBeDeadlockFree() throws IOException{
                var spc = testFile("recursion/2_vs_1.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void recurseVsStaticIsDeadlockFree() throws IOException {
                var spc = testFile("recurse_vs_static.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void recurseVSStaticEndIsDeadlockFree() throws IOException {
                var spc = testFile("recurse_vs_static_end_first.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void asymmetricRecursionShouldBeDeadlockFree() throws IOException {
                var spc = testFile("recursion/asymmetric_recursion.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void recursionSelectionSecondShouldBeDeadlockFree() throws IOException {
                var spc = testFile("recursion/recursion_selection_second.sp");
                spc.reduceNetwork();
                assertTrue(spc.deadlockFreedom());
            }
            @Test
            public void asymmetricRecursionReceiveRecurseShouldDeadlock() throws IOException{
                var spc = testFile("recursion/asymmetric_recursion_receive_recurse.sp");
                spc.reduceNetwork();
                assertFalse(spc.deadlockFreedom());
            }
        }

        @Nested
        public class Termination{

        }

        @Nested
        public class Liveness{

        }

        @Nested
        public class Livelock{

        }

        @Nested
        public class WasteGeneration{
            @Test
            public void IPProtocolShouldBeNotGenerateWaste() throws IOException{
                var spc = testFile("IP_protocol.sp");
                spc.reduceNetwork();
                assertFalse(spc.generateWaste());
            }
            @Test
            public void nReceiveVSArbitrarySendShouldGenerateWaste() throws IOException {
                var spc = testFile("recursion/asymmetric_send_waste.sp");
                spc.reduceNetwork();
                assertTrue(spc.generateWaste());
            }

            @Test
            public void asymmetricRecursionShouldGenerateWaste() throws IOException {
                var spc = testFile("recursion/asymmetric_recursion.sp");
                spc.reduceNetwork();
                assertTrue(spc.generateWaste());
            }
            @Test
            public void recursionSelectionSecondShouldNotGenerateWaste() throws IOException {
                var spc = testFile("recursion/recursion_selection_second.sp");
                spc.reduceNetwork();
                assertFalse(spc.generateWaste());
            }
            @Test
            public void OAuth2ShouldNotGenerateWaste() throws IOException {
                var spc = testFile("OAuth2_fragment.sp");
                spc.reduceNetwork();
                assertFalse(spc.generateWaste());
            }
        }

        @Nested
        public class DeadBranches{
            @Test
            public void lessUnreachableThanUnvisitedShouldHaveDeadBranches() throws IOException{
                var spc = testFile("less_unreachable_than_unvisited.sp");
                spc.reduceNetwork();
                assertEquals(spc.getHistory().size(), 2);
            }

            @Test
            public void IPProtocolShouldNotHaveDeadBranches() throws IOException{
                var spc = testFile("IP_protocol.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().stream().allMatch(List::isEmpty));
            }
            @Test
            public void OAuth2LocalHasDeadBranches() throws IOException {
                var spc = testFile("OAuth2_fragment.sp");
                spc.reduceNetwork();
                var unreachableNodes = spc.getUnreachableNodes();
                assertFalse(unreachableNodes.stream().allMatch(List::isEmpty));
                assertEquals(unreachableNodes.size(), 1);
            }
            @Test
            public void exampleFredHasNoUnreachableNodes() throws IOException {
                var spc = testFile("example_fred.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().stream().allMatch(List::isEmpty));
                assertTrue(spc.deadlockFreedom());
            }

            @Test
            public void twoVSoneHasNoDeadBranches() throws IOException {
                var spc = testFile("recursion/2_vs_1.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().stream().allMatch(List::isEmpty));
            }

            @Test
            public void recurseVsStaticHasNoDeadBranches() throws IOException {
                var spc = testFile("recurse_vs_static.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().stream().allMatch(List::isEmpty));
            }

            @Test
            public void recurseVSStaticEndHasDeadBranches() throws IOException {
                var spc = testFile("recurse_vs_static_end_first.sp");
                spc.reduceNetwork();
                assertFalse(spc.getUnreachableNodes().stream().allMatch(List::isEmpty));
            }

            @Test
            public void starvingBranchShouldGiveMeAnError() throws IOException{
                var spc = testFile("recursion/starving_one_branch.sp");
                spc.reduceNetwork();
                assertFalse(spc.getUnreachableNodes().stream().allMatch(List::isEmpty));
            }
            @Test
            public void recursionSelectionSecondShouldNotHaveDeadBranches() throws IOException {
                var spc = testFile("recursion/recursion_selection_second.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().stream().allMatch(List::isEmpty));
            }
        }
    }
}
