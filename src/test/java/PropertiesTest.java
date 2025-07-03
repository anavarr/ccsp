import com.opencsv.CSVWriter;
import mychor.SPcheckerRich;
import mychor.SPlexer;
import mychor.SPparserRich;
import mychor.Utils;
import mychor.types.LocalType;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertiesTest extends ProgramReaderTest{
//    @Test
    public void testFeasibility() throws IOException{
        var path = Path.of("/", "tmp", "choreoGen", "system.sp");
        SPlexer spl = new SPlexer(CharStreams.fromPath(path));
        var spp = new SPparserRich(new CommonTokenStream(spl));
        var spc = new SPcheckerRich();
        spp.program().accept(spc);
        int complexity = spc.computeBiggestComplexity();
        try(CSVWriter writer = new CSVWriter(new FileWriter("header.csv"),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)){
            // Write header line
            writer.writeNext(new String[]{"max complexity"});
            writer.writeNext(new String[]{
                    String.valueOf(complexity)
            });
        }
    }
//    @Test
    public void testGeneratedSystem() throws IOException {
        var path = Path.of("/", "tmp", "choreoGen", "system.sp");
        SPlexer spl = new SPlexer(CharStreams.fromPath(path));
        var spp = new SPparserRich(new CommonTokenStream(spl));
        var spc = new SPcheckerRich();
        spp.program().accept(spc);
        int complexity = spc.computeBiggestComplexity();
        System.out.println(complexity);
        var tstart = Instant.now().toEpochMilli();
        spc.reduceNetwork(complexity);
        var tend = Instant.now().toEpochMilli();
        long duration = tend - tstart;
        var dn = spc.deadlockedNodes();
        var ln = spc.livelockedNodes();
        var un = spc.getUnreachableNodes();
        var branches = spc.getBranches();
        var branches_data = new ArrayList<>(branches.values().stream().map(ArrayList::size).toList());
        var quartiles = Utils.quartiles(branches_data);
        float average = (float) branches_data.stream().reduce(0, Integer::sum) /branches_data.size();
        int maxBranching=branches_data.getLast();
        int minBranching=branches_data.getFirst();
        double stdev = Utils.stdev(branches_data);

        List<String> processesByName = new ArrayList<>(branches.keySet());
        Collections.sort(processesByName);
        try(CSVWriter writer = new CSVWriter(new FileWriter("subnets.csv"),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)){
            writer.writeNext(new String[]{"subnetworks"});
            for (HashSet<String> computeSubNetwork : spc.computeSubNetworks()) {
                writer.writeNext(computeSubNetwork.toArray(new String[0]));
            }
        }

        try(CSVWriter writer = new CSVWriter(new FileWriter("properties.csv"),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)){
        // Write header line
            writer.writeNext(new String[]{"deadlocked nodes", "livelocked nodes", "unreachable nodes", "reduction duration", "max complexity"});
            writer.writeNext(new String[]{
                    dn.values().stream().allMatch(ArrayList::isEmpty) ? "no":"yes",
                    ln.values().stream().allMatch(HashSet::isEmpty) ? "no":"yes",
                    un.values().stream().allMatch(List::isEmpty) ? "no":"yes",
                    String.valueOf(duration),
                    String.valueOf(complexity)
            });
        }
        try(CSVWriter writer = new CSVWriter(new FileWriter("results.csv"),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)){
            String[] headers = processesByName.toArray(new String[0]);
            writer.writeNext(headers);
            for (int i = 0; i < maxBranching; i++) {
                ArrayList<String> line = new ArrayList<>();
                for (String s : processesByName) {
                    if(i>=branches.get(s).size()) line.add("");
                    else {
                        var b = branches.get(s).get(i).stream().map(el -> el.replaceAll("\"", "")).toList();
                        line.add(String.join("|", b));
                    }
                }
                String[] lineA = line.toArray(new String[0]);
                writer.writeNext(lineA);
            }
            // Write data lines
        }catch(IOException e){
            System.err.println(e);
        }
    }

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

        @Test
        public void UnvisitedPathsBlockedByImpossibleReceiveShouldDeadlock() throws IOException {
            var path = Path.of("/", "home","arnavarr","Documents","thesis", "prog", "antlr4", "system.sp");
            SPlexer spl = new SPlexer(CharStreams.fromPath(path));
            var spp = new SPparserRich(new CommonTokenStream(spl));
            var spc = new SPcheckerRich();
            spp.program().accept(spc);
            spc.reduceNetwork();
            var dln = spc.deadlockedNodes();
            var lln = spc.livelockedNodes();
            var urn = spc.getUnreachableNodes();
            assertFalse(spc.deadlockFreedom());
            assertFalse(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
        }

        @Test
        public void TestGeneration() throws IOException {
            var path = Path.of("/", "tmp", "choreoGen", "system.sp");
            SPlexer spl = new SPlexer(CharStreams.fromPath(path));
            var spp = new SPparserRich(new CommonTokenStream(spl));
            var spc = new SPcheckerRich();
            spp.program().accept(spc);
            spc.reduceNetwork();
            var dln = spc.deadlockedNodes();
            var lln = spc.livelockedNodes();
            var urn = spc.getUnreachableNodes();
            assertTrue(spc.deadlockFreedom());
            assertTrue(urn.values().stream().allMatch(List::isEmpty));
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
    public class BigSystems{
        @Test
        public void testDepthGenerated() throws IOException {
            var spc = testFile("generation/400_many_branches.sp");
            spc.reduceNetwork();
            spc.deadlockFreedom();
            spc.livelockedNodes();
        }
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
        public class CountingElements{

            public void testComposition(int nBranchings, int nSelections,
                                        List<Integer> bDistr, List<Integer> sDistr, String label, SPcheckerRich spc){
                assertEquals(bDistr,spc.branchingDistribution().get(label));
                assertEquals(sDistr,spc.selectionDistribution().get(label));

                assertEquals(nBranchings, spc.numberOfBranching().get(label));
                assertEquals(nSelections, spc.numberOfSelections().get(label));
                assertEquals(nBranchings+nSelections, spc.numberOfBranchingAndSelections().get(label));
            }

            @Test
            public void starvedHasOneBranch() throws IOException {
                var spc = testFile("recursion/starving_one_branch.sp");
                spc.reduceNetwork();
                testComposition(1, 0, List.of(1), List.of(), "starved", spc);
                testComposition(0, 1, List.of(), List.of(1), "starver", spc);
            }


        }

        @Nested
        public class Livelock{
            @Test
            public void oauthHasNoLiveLock() throws IOException{
                var spc = testFile("OAuth2_fragment.sp");
                spc.reduceNetwork();
                assertTrue(spc.livelockedNodes().values().stream().allMatch(HashSet::isEmpty));
            }

            @Test
            public void twoBuyerHasLiveLock()throws IOException{
                    var spc = testFile("Three_buyer_protocol.sp");
                spc.reduceNetwork();
                assertFalse(spc.livelockedNodes().values().stream().allMatch(HashSet::isEmpty));
            }

            @Test
            public void IPProtocolHasNoLiveLock() throws IOException {
                var spc=testFile("IP_protocol.sp");
                spc.reduceNetwork();
                assertTrue(spc.livelockedNodes().values().stream().allMatch(HashSet::isEmpty));
            }

            @Test
            public void exampleFredHasLiveLocks() throws IOException{
                var spc = testFile("example_fred.sp");
                spc.reduceNetwork();
                assertTrue(spc.livelockedNodes().values().stream().anyMatch(list -> !list.isEmpty()));
            }



            @Test
            public void singleExchangeHasNoLiveLock() throws IOException {
                var spc = testFile("complementarySessionsSndRcv.sp");
                spc.reduceNetwork();
                assertTrue(spc.livelockedNodes().values().stream().allMatch(HashSet::isEmpty));
            }

            @Test
            public void exampleFredCutHasNoLiveLock() throws IOException{
                var spc = testFile("example_fred_cut.sp");
                spc.reduceNetwork();
                assertTrue(spc.livelockedNodes().values().stream().allMatch(HashSet::isEmpty));
            }

            @Test
            public void livelockedContinuation() throws IOException {
                var spc = testFile("recursion/livelockedContinuation.sp");
                spc.reduceNetwork();
                assertFalse(spc.livelockedNodes().isEmpty());
            }
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
            public void unbalancedTreeShouldVisitAllNodes() throws IOException {
                var spc = testFile("unbalanced_graphs.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
            }

            @Test
            public void lessUnreachableThanUnvisitedShouldHaveDeadBranches() throws IOException{
                var spc = testFile("less_unreachable_than_unvisited.sp");
                spc.reduceNetwork();
                assertFalse(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
            }

            @Test
            public void IPProtocolShouldNotHaveDeadBranches() throws IOException{
                var spc = testFile("IP_protocol.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
            }
            @Test
            public void OAuth2LocalHasDeadBranches() throws IOException {
                var spc = testFile("OAuth2_fragment.sp");
                spc.reduceNetwork();
                var unreachableNodes = spc.getUnreachableNodes();
                assertFalse(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
                assertEquals(unreachableNodes.size(), 3);
            }
            @Test
            public void exampleFredHasNoUnreachableNodes() throws IOException {
                var spc = testFile("example_fred.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
                assertTrue(spc.deadlockFreedom());
            }

            @Test
            public void twoVSoneHasNoDeadBranches() throws IOException {
                var spc = testFile("recursion/2_vs_1.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
            }

            @Test
            public void recurseVsStaticHasNoDeadBranches() throws IOException {
                var spc = testFile("recurse_vs_static.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
            }

            @Test
            public void recurseVSStaticEndHasDeadBranches() throws IOException {
                var spc = testFile("recurse_vs_static_end_first.sp");
                spc.reduceNetwork();
                assertFalse(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
            }

            @Test
            public void starvingBranchShouldGiveMeAnError() throws IOException{
                var spc = testFile("recursion/starving_one_branch.sp");
                spc.reduceNetwork();
                assertFalse(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
            }
            @Test
            public void recursionSelectionSecondShouldNotHaveDeadBranches() throws IOException {
                var spc = testFile("recursion/recursion_selection_second.sp");
                spc.reduceNetwork();
                assertTrue(spc.getUnreachableNodes().values().stream().allMatch(List::isEmpty));
            }
        }
    }
}
