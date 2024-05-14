import mychor.Comm;
import mychor.Communication;
import mychor.Utils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CommunicationTest {

    ArrayList<Communication> cSelect = new ArrayList<>(List.of(
            new Communication(Utils.Direction.SELECT,
                    "left", new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND)
                    ))),
            new Communication(Utils.Direction.SELECT,
                    "left", new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE))
                    ))),
            new Communication(Utils.Direction.SELECT,
                    "left", new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE,
                                    new Communication(Utils.Direction.SEND))
                    )))
    ));
    ArrayList<Communication> cBranch = new ArrayList<>(List.of(
            new Communication(Utils.Direction.BRANCH,
                    "left", new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE)
                    ))),
            new Communication(Utils.Direction.BRANCH,
                    "left", new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE,
                                    new Communication(Utils.Direction.SEND))
                    ))),
            new Communication(Utils.Direction.BRANCH,
                    "left", new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE))
                    )))
    ));

    @Nested
    class ConstructorsTest{
        @Test
        public void createSend(){
            var c1 = new Communication(Utils.Direction.SEND);
            assertEquals(c1.getDirection(), Utils.Direction.SEND);
            assertNull(c1.getLabel());
            assertTrue(c1.isSend());
            assertFalse(c1.isSelect());
            assertFalse(c1.isBranch());
            assertFalse(c1.isReceive());
        }
        @Test
        public void createReceive(){
            var c1 = new Communication(Utils.Direction.RECEIVE);
            assertEquals(c1.getDirection(), Utils.Direction.RECEIVE);
            assertNull(c1.getLabel());
            assertTrue(c1.isReceive());
            assertFalse(c1.isSelect());
            assertFalse(c1.isBranch());
            assertFalse(c1.isSend());
        }
        @Test
        public void createSelect(){
            var c1 = new Communication(Utils.Direction.SELECT, "GET");
            assertEquals(c1.getDirection(), Utils.Direction.SELECT);
            assertEquals(c1.getLabel(), "GET");
            assertTrue(c1.isSelect());
            assertFalse(c1.isSend());
            assertFalse(c1.isBranch());
            assertFalse(c1.isReceive());
        }
        @Test
        public void createSelectDoesntWorkIfNoLabel(){
            assertThrows(NullPointerException.class, () -> new Communication(Utils.Direction.SELECT));
        }
        @Test
        public void createBranching(){
            var c1 = new Communication(Utils.Direction.BRANCH, "GET");
            assertEquals(c1.getDirection(), Utils.Direction.BRANCH);
            assertTrue(c1.isBranch());
            assertFalse(c1.isSelect());
            assertFalse(c1.isSend());
            assertFalse(c1.isReceive());
        }
        @Test
        public void createBranchingDoesntWorkIfNoLabel(){
            assertThrows(NullPointerException.class, () -> new Communication(Utils.Direction.BRANCH));
        }
        @Test
        public void createCommunicationWithBranchesPossessingPreviousShouldWork(){
            var cChild = new Communication(Utils.Direction.SEND);
            var cParent = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(cChild)));
            var cParent2 = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(cChild)));
            assertTrue(cParent.containsDirectNextNode(cChild));
            assertTrue(cParent2.containsDirectNextNode(cChild));

            assertTrue(cChild.containsDirectPreviousNode(cParent2));
            assertTrue(cChild.containsDirectPreviousNode(cParent));
        }
        @Test
        public void createRecursive(){
            var c1 = new Communication(Utils.Direction.SEND);
            var c3 = new Communication(Utils.Direction.SELECT, "GET");
            c3.addLeafCommunicationRoots(new ArrayList<>(List.of(c1)));
            var c2 = new Communication(Utils.Direction.RECEIVE,
                    new ArrayList<>(List.of(c3)),
                    null,
                    new ArrayList<>(List.of(c1)));
            assertTrue(c2.nodeIsSelfOrAbove(c1));
            assertFalse(c2.nodeIsSelfOrBelow(c3));
            assertEquals(c2.getRecursiveCallees().size(),1);
            assertTrue(c2.getRecursiveCallees().contains(c3));
        }
    }
    @Nested
    class EqualityTest{
        @Test
        public void inequalityClass(){
            var c1 = new Communication(Utils.Direction.SEND);
            var c1Prime = new Comm("a", "b", Utils.Direction.SEND, "");
            assertNotEquals(c1, c1Prime);
        }
        @Test
        public void equalitySingleComm(){
            var c1 = new Communication(Utils.Direction.SEND);
            var c2 = new Communication(Utils.Direction.SEND);
            assertEquals(c1, c2);
        }
        @Test
        public void equalityMultiCommLine(){
            var c1 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE)
                            )
                    )
            );
            var c2 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE)
                            )
                    )
            );
            assertEquals(c1, c2);
        }
        @Test
        public void inequalityMultiCommLine(){
            var c1 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.SEND)
                            )
                    )
            );
            var c2 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE)
                            )
                    )
            );
            assertNotEquals(c1, c2);
        }
        @Test
        public void inequalityDifferentLabelsSelect(){
            var select1 = new Communication(Utils.Direction.SELECT, "GET");
            var select2 = new Communication(Utils.Direction.SELECT, "POST");
            assertNotEquals(select1, select2);
        }
        @Test
        public void inequalityDifferentLabelsBranch(){
            var br1 = new Communication(Utils.Direction.BRANCH, "GET");
            var br2 = new Communication(Utils.Direction.BRANCH, "POST");
            assertNotEquals(br1, br2);
        }
        @Test
        public void inequalityDifferentRecursiveCallersSize(){
            var send1 = new Communication(Utils.Direction.SEND);
            send1.addRecursiveCallee(new Communication(Utils.Direction.RECEIVE));
            send1.addRecursiveCallee(new Communication(Utils.Direction.SEND));
            var send2 = new Communication(Utils.Direction.SEND);
            send2.addRecursiveCallee(new Communication(Utils.Direction.RECEIVE));
            assertNotEquals(send1, send2);
        }
        @Test
        public void inequalityDifferentRecursiveCalls(){
            var comm1 = new Communication(Utils.Direction.SEND);
            var comm11 = new Communication(Utils.Direction.RECEIVE);
            var comm111 = new Communication(Utils.Direction.SEND);
            comm1.addLeafCommunicationRoots(new ArrayList<>(List.of(comm11)));
            comm11.addLeafCommunicationRoots(new ArrayList<>(List.of(comm111)));
            comm111.addLeafCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.VOID), comm1)));


            var comm2 = new Communication(Utils.Direction.SEND);
            var comm22 = new Communication(Utils.Direction.RECEIVE);
            var comm222 = new Communication(Utils.Direction.SEND);
            comm2.addLeafCommunicationRoots(new ArrayList<>(List.of(comm22)));
            comm22.addLeafCommunicationRoots(new ArrayList<>(List.of(comm222)));
            comm222.addLeafCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.VOID), comm22)));

            assertNotEquals(comm1, comm2);
        }
        @Test
        public void equalitySameRecursiveCalls(){
            var comm1 = new Communication(Utils.Direction.SEND);
            var comm11 = new Communication(Utils.Direction.RECEIVE);
            var comm111 = new Communication(Utils.Direction.SEND);
            comm1.addLeafCommunicationRoots(new ArrayList<>(List.of(comm11)));
            comm11.addLeafCommunicationRoots(new ArrayList<>(List.of(comm111)));
            comm111.addLeafCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.VOID), comm1)));


            var comm2 = new Communication(Utils.Direction.SEND);
            var comm22 = new Communication(Utils.Direction.RECEIVE);
            var comm222 = new Communication(Utils.Direction.SEND);
            comm2.addLeafCommunicationRoots(new ArrayList<>(List.of(comm22)));
            comm22.addLeafCommunicationRoots(new ArrayList<>(List.of(comm222)));
            comm222.addLeafCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.VOID), comm2)));

            assertEquals(comm1, comm2);
        }
    }
    @Nested
    class AboveOrBelowTest{
        @Test
        public void directParentIsAboveShouldReturnTrue(){
            var cParent = new Communication(Utils.Direction.SEND);
            var cChild = new Communication(Utils.Direction.RECEIVE);
            cParent.addLeafCommunicationRoots(new ArrayList<>(List.of(cChild)));
            assertTrue(cChild.nodeIsSelfOrAbove(cParent));
            assertTrue(cChild.nodeIsAbove(cParent));
        }
        @Test
        public void directChildIsBelowShouldReturnTrue(){
            var cParent = new Communication(Utils.Direction.SEND);
            var cChild = new Communication(Utils.Direction.RECEIVE);
            cParent.addLeafCommunicationRoots(new ArrayList<>(List.of(cChild)));
            assertTrue(cParent.nodeIsSelfOrBelow(cChild));
            assertTrue(cParent.nodeIsBelow(cChild));
        }
        @Test
        public void directParentIsBelowShouldReturnFalse(){
            var cParent = new Communication(Utils.Direction.SEND);
            var cChild = new Communication(Utils.Direction.RECEIVE);
            cParent.addLeafCommunicationRoots(new ArrayList<>(List.of(cChild)));
            assertFalse(cChild.nodeIsSelfOrBelow(cParent));
            assertFalse(cChild.nodeIsBelow(cParent));
        }
        @Test
        public void directChildIsAboveShouldReturnFalse(){
            var cParent = new Communication(Utils.Direction.SEND);
            var cChild = new Communication(Utils.Direction.RECEIVE);
            cParent.addLeafCommunicationRoots(new ArrayList<>(List.of(cChild)));
            assertFalse(cParent.nodeIsSelfOrAbove(cChild));
            assertFalse(cParent.nodeIsAbove(cChild));
        }
        @Test
        public void indirectParentIsAboveShouldReturnTrue(){
            var comm111 = new Communication(Utils.Direction.SELECT, "GET");
            var comm11 = new Communication(Utils.Direction.RECEIVE, comm111);
            var comm1 = new Communication(Utils.Direction.SEND, comm11);
            assertTrue(comm111.nodeIsAbove(comm1));
        }
        @Test
        public void indirectChildIsBelowShouldReturnTrue(){
            var comm111 = new Communication(Utils.Direction.SELECT, "GET");
            var comm11 = new Communication(Utils.Direction.RECEIVE, comm111);
            var comm1 = new Communication(Utils.Direction.SEND, comm11);
            assertTrue(comm1.nodeIsBelow(comm111));
        }
        @Test
        public void selfIsSelfOrAboveButNotAbove(){
            var n = new Communication(Utils.Direction.SEND);
            assertTrue(n.nodeIsSelfOrAbove(n));
            assertFalse(n.nodeIsAbove(n));
        }
        @Test
        public void selfIsSelfOrBelowButNotBelow(){
            var n = new Communication(Utils.Direction.SEND);
            assertTrue(n.nodeIsSelfOrBelow(n));
            assertFalse(n.nodeIsBelow(n));
        }
        @Test
        public void unRelatedNodesShouldNotBeSelfAboveOrBelow(){
            var s = new Communication(Utils.Direction.SEND);
            s.addLeafCommunicationRoots(new ArrayList<>(List.of(new Communication(Utils.Direction.SEND))));
            var s2 = new Communication(Utils.Direction.SEND);
            assertFalse(s.nodeIsSelfOrBelow(s2));
            assertFalse(s.nodeIsBelow(s2));
            assertFalse(s.nodeIsSelfOrAbove(s2));
            assertFalse(s.nodeIsAbove(s2));
        }
        @Test
        public void unRelatedNodesShouldNotBeSelfAboveOrBelowInLongChain(){
            var sEnd = new Communication(Utils.Direction.SEND);
            var s = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.SEND,
                            sEnd));
            var s2 = new Communication(Utils.Direction.SEND);
            assertFalse(sEnd.nodeIsSelfOrAbove(s2));
            assertFalse(sEnd.nodeIsAbove(s2));
        }
    }
    @Nested
    class SizeTest{
        @Test
        public void checkSize1(){
            var c = new Communication(Utils.Direction.SEND);
            assertEquals(c.getBranchesSize(), 1);
        }
        @Test
        public void checkSizeN(){
            assertEquals(cSelect.get(0).getBranchesSize(), 2);
        }
    }
    @Nested
    class ComplementarityTest{
        @Test
        public void simpleComplementaritySR(){
            var c1 = new Communication(Utils.Direction.SEND);
            var c2 = new Communication(Utils.Direction.RECEIVE);
            assertTrue(c1.isComplementary(c2) && c2.isComplementary(c1));
        }
        @Test
        public void simpleComplementaritySB(){
            var c1 = new Communication(Utils.Direction.SELECT, "left");
            var c2 = new Communication(Utils.Direction.BRANCH, "left");
            assertTrue(c1.isComplementary(c2));
        }
        @Test
        public void simpleNonComplementaritySBLabel(){
            var c1 = new Communication(Utils.Direction.SELECT, "left");
            var c2 = new Communication(Utils.Direction.BRANCH, "right");
            assertFalse(c1.isComplementary(c2));
        }
        @Test
        public void simpleNonComplementaritySR(){
            var c1 = new Communication(Utils.Direction.SEND);
            var c2 = new Communication(Utils.Direction.SEND);
            assertFalse(c1.isComplementary(c2) && c2.isComplementary(c1));
        }
        @Test
        public void simpleNonComplementarityRS(){
            var c1 = new Communication(Utils.Direction.RECEIVE);
            var c2 = new Communication(Utils.Direction.RECEIVE);
            assertFalse(c1.isComplementary(c2) && c2.isComplementary(c1));
        }
        @Test
        public void simpleNonComplementarityBS(){
            var c1 = new Communication(Utils.Direction.BRANCH, "GET");
            var c2 = new Communication(Utils.Direction.BRANCH, "GET");
            assertFalse(c1.isComplementary(c2) && c2.isComplementary(c1));
        }
        @Test
        public void simpleNonComplementaritySB(){
            var c1 = new Communication(Utils.Direction.SELECT, "GET");
            var c2 = new Communication(Utils.Direction.SELECT, "GET");
            assertFalse(c1.isComplementary(c2) && c2.isComplementary(c1));
        }
        @Test
        public void complexComplementarity(){
            var c1 = new Communication(Utils.Direction.RECEIVE, cSelect);
            var c2 = new Communication(Utils.Direction.SEND, cBranch);
            assertTrue(c1.isComplementary(c2) && c2.isComplementary(c1));
        }
        @Test
        public void differentNextNodesSizesNotComplementary(){
            var c1 = new Communication(Utils.Direction.RECEIVE, new Communication(Utils.Direction.SEND));
            var c2 = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE),
                    new Communication(Utils.Direction.SEND)
            )));
            assertFalse(c1.isComplementary(c2) || c2.isComplementary(c1));
        }
        @Test
        public void deepNonComplementarity(){
            var c1 = new Communication(Utils.Direction.RECEIVE,
                    new Communication(Utils.Direction.SEND,
                            new Communication(Utils.Direction.SEND)));
            var c2 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND)));
            assertFalse(c1.isComplementary(c2) || c2.isComplementary(c1));
        }
    }
    @Nested
    class BranchingTest{
        // BRANCHING
        @Test
        public void validBranchingTrivial(){
            var c1 = new Communication(Utils.Direction.SEND);
            assertTrue(c1.isBranchingValid());
            c1 = new Communication(Utils.Direction.RECEIVE);
            assertTrue(c1.isBranchingValid());
            c1 = new Communication(Utils.Direction.SELECT, "left");
            assertTrue(c1.isBranchingValid());
            c1 = new Communication(Utils.Direction.BRANCH, "left");
            assertTrue(c1.isBranchingValid());
        }
        @Test
        public void validBranchingAllSelect(){
            var c1 = new Communication(Utils.Direction.SEND, cSelect);
            assertTrue(c1.isBranchingValid());
        }
        @Test
        public void validBranchingAllBranch(){
            var c1 = new Communication(Utils.Direction.SEND, cBranch);
            assertTrue(c1.isBranchingValid());
        }
        @Test
        public void validBranchingAllSame(){
            var c1 = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.SEND)
            )));
            assertTrue(c1.isBranchingValid());
        }
        @Test
        public void invalidBranchingNotAllSame(){
            var c1 = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.RECEIVE),
                    new Communication(Utils.Direction.SEND)
            )));
            assertFalse(c1.isBranchingValid());
        }
    }
    @Nested
    class NodeAdditionTest{
        @Test
        public void addRootsFirstLayer(){
            var c = new Communication(Utils.Direction.SEND);
            c.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE))
            ));
            var cTotal = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE));

            assertEquals(c, cTotal);
        }
        @Test
        public void addRootsSecondLayer(){
            var c = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE));
            c.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND))
            ));
            var cTotal = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND)));
            assertEquals(c, cTotal);
        }
        @Test
        public void correctLeaves(){
            var c1 = new Communication(Utils.Direction.SEND);
            var c2 = new Communication(Utils.Direction.RECEIVE);
            var c3 = new Communication(Utils.Direction.SELECT, "left");
            var c = new Communication(Utils.Direction.SEND,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SELECT,
                                    "left", new ArrayList<>(List.of(c1))),
                            new Communication(Utils.Direction.SELECT,
                                    "left", new ArrayList<>(List.of(c2))),
                            c3
                    )));
            var leaves = c.getLeaves();
            var leavesExpected = new ArrayList<>(List.of(c1,c2, c3));
            assertTrue(leaves.containsAll(leavesExpected) && leavesExpected.containsAll(leaves));
        }
        @Test
        public void previousTest(){
            var cChild = new Communication(Utils.Direction.SEND);
            var cParent = new Communication(Utils.Direction.RECEIVE);
            var cPParent = new Communication(Utils.Direction.SEND);
            cParent.addLeafCommunicationRoots(new ArrayList<>(List.of(cChild)));
            cPParent.addLeafCommunicationRoots(new ArrayList<>(List.of(cParent)));
            assertTrue(cPParent.containsDirectNextNode(cParent));
            assertTrue(cParent.containsDirectNextNode(cChild));
            assertTrue(cChild.containsDirectPreviousNode(cParent));
            assertTrue(cParent.containsDirectPreviousNode(cPParent));
            assertTrue(cChild.nodeIsAbove(cPParent));
            assertTrue(cChild.nodeIsAbove(cParent));
            assertTrue(cPParent.nodeIsBelow(cChild));
        }
        @Test
        public void addingNextNodesToVoidGluesThemToVoidsPreviousNodes(){
            var v = new Communication(Utils.Direction.VOID);
            var p = new Communication(Utils.Direction.SEND);
            var c = new Communication(Utils.Direction.RECEIVE);
            p.addLeafCommunicationRoots(new ArrayList<>(List.of(v)));
            v.addLeafCommunicationRoots(new ArrayList<>(List.of(c)));
            assertTrue(p.containsDirectNextNode(c));
            assertTrue(c.containsDirectPreviousNode(p));
            assertFalse(p.containsDirectPreviousNode(v));
        }
        @Test
        public void addingNextNodesToVoidDoesNothingIfVoidHasNoPreviousNodes(){
            var v = new Communication(Utils.Direction.VOID);
            var c = new Communication(Utils.Direction.RECEIVE);
            v.addLeafCommunicationRoots(new ArrayList<>(List.of(c)));
            assertFalse(v.hasNextNodes());
            assertFalse(c.hasPreviousNodes());
            assertFalse(c.containsDirectPreviousNode(v));
        }

        @Test
        public void addingNextNodesShouldNotAppendLeaf(){
            var p = new Communication(Utils.Direction.SEND);
            var c1 = new Communication(Utils.Direction.RECEIVE);
            var c2 = new Communication(Utils.Direction.SEND);
            p.addLeafCommunicationRoots(new ArrayList<>(List.of(c1)));
            p.addNextCommunicationNodes(new ArrayList<>(List.of(c2)));
            assertTrue(p.hasNextNodes());
            assertFalse(c1.hasNextNodes());
            assertFalse(c2.hasNextNodes());
            assertTrue(p.containsDirectNextNode(c1));
            assertTrue(p.containsDirectNextNode(c2));
            assertTrue(c1.containsDirectPreviousNode(p));
            assertTrue(c2.containsDirectPreviousNode(p));
            assertFalse(c2.nodeIsSelfOrAbove(c1));
            assertFalse(c1.nodeIsSelfOrBelow(c2));
        }
    }
    @Nested
    class VoidCleaningTest{
        @Test
        public void cleaningVoidDoesNothingIfVoidHasSiblings(){
            var p = new Communication(Utils.Direction.SEND);
            var v = new Communication(Utils.Direction.VOID);
            var c = new Communication(Utils.Direction.RECEIVE);
            p.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    v, c
            )));
            p.cleanVoid();
            assertTrue(p.containsDirectNextNode(v));
            assertTrue(v.containsDirectPreviousNode(p));
        }
        @Test
        public void cleaningVoidRemovesVoidIfItIsOnlyChild(){
            var p = new Communication(Utils.Direction.SEND);
            var p1 = new Communication(Utils.Direction.RECEIVE);
            var v = new Communication(Utils.Direction.VOID);
            p1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    v
            )));
            p.addLeafCommunicationRoots(new ArrayList<>(List.of(p1)));
            p.cleanVoid();
            assertFalse(p.containsDirectNextNode(v));
            assertFalse(v.containsDirectPreviousNode(p));
        }
    }
    @Nested
    class FindDirectedLabelsTest{
        @Test
        public void sendShouldNotReturnAnyDirectedLabel(){
            var s = new Communication(Utils.Direction.SEND);
            var dls = s.getDirectedLabels(Utils.Direction.SEND);
            assertTrue(dls.isEmpty());
        }
        @Test
        public void receiveShouldNotReturnAnyDirectedLabel(){
            var s = new Communication(Utils.Direction.RECEIVE);
            var dls = s.getDirectedLabels(Utils.Direction.RECEIVE);
            assertTrue(dls.isEmpty());
        }
        @Test
        public void selectShouldReturnLabel(){
            var s = new Communication(Utils.Direction.SELECT, "GET");
            var dls = s.getDirectedLabels(Utils.Direction.SELECT);
            assertTrue(dls.contains("GET"));
        }
        @Test
        public void deepSelectSouldReturnLabel(){
            var p = new Communication(Utils.Direction.SEND);
            var s = new Communication(Utils.Direction.SELECT, "GET");
            p.addLeafCommunicationRoots(new ArrayList<>(List.of(s)));
            var dls = p.getDirectedLabels(Utils.Direction.SELECT);
            assertTrue(dls.contains("GET"));
        }
        @Test
        public void branchingShouldReturnLabel(){
            var s = new Communication(Utils.Direction.BRANCH, "GET");
            var dls = s.getDirectedLabels(Utils.Direction.BRANCH);
            assertTrue(dls.contains("GET"));
        }
        @Test
        public void branchingAndSelectShouldOnlyReturnQueriedDirectionLabels(){
            var select = new Communication(Utils.Direction.SELECT,"GETS");
            var branch = new Communication(Utils.Direction.BRANCH, "GET", new ArrayList<>(List.of(select)));
            assertTrue(branch.getDirectedLabels(Utils.Direction.BRANCH).contains("GET"));
            assertFalse(branch.getDirectedLabels(Utils.Direction.BRANCH).contains("GETS"));
            assertTrue(branch.getDirectedLabels(Utils.Direction.SELECT).contains("GETS"));
            assertFalse(branch.getDirectedLabels(Utils.Direction.SELECT).contains("GET"));
        }
    }
    @Nested
    class SupportTest{
        @Test
        public void sendSupportsSelect(){
            var send = new Communication(Utils.Direction.SEND);
            var select = new Communication(Utils.Direction.SELECT, "GET");
            assertTrue(send.supports(select));
        }
        @Test
        public void sendSupportsSend(){
            var send = new Communication(Utils.Direction.SEND);
            var send2 = new Communication(Utils.Direction.SEND);
            assertTrue(send.supports(send2));
        }
        @Test
        public void receiveSupportsReceive(){
            var rcv1 = new Communication(Utils.Direction.RECEIVE);
            var rcv2 = new Communication(Utils.Direction.RECEIVE);
            assertTrue(rcv1.supports(rcv2));
        }
        @Test
        public void receiveSupportsBranch(){
            var rcv1 = new Communication(Utils.Direction.RECEIVE);
            var rcv2 = new Communication(Utils.Direction.BRANCH, "GET");
            assertTrue(rcv1.supports(rcv2));
        }
        @Test
        public void sendDoesntSupportReceive(){
            var send = new Communication(Utils.Direction.SEND);
            var rcv = new Communication(Utils.Direction.RECEIVE);
            assertFalse(send.supports(rcv));
        }
        @Test
        public void sendDoesntSupportBranch(){
            var send = new Communication(Utils.Direction.SEND);
            var brch = new Communication(Utils.Direction.BRANCH, "GET");
            assertFalse(send.supports(brch));
        }
        @Test
        public void voidSupportsOnlyVoid(){
            var v = new Communication(Utils.Direction.VOID);
            var v2 = new Communication(Utils.Direction.VOID);
            var send = new Communication(Utils.Direction.SEND);
            var rcv = new Communication(Utils.Direction.RECEIVE);
            var select = new Communication(Utils.Direction.SELECT,"GET");
            var brch = new Communication(Utils.Direction.BRANCH, "GET");
            assertFalse(v.supports(send));
            assertFalse(v.supports(rcv));
            assertFalse(v.supports(select));
            assertFalse(v.supports(brch));
            assertTrue(v.supports(v2));
        }
        @Test
        public void singleSendShouldNotSupportRecursiveSend(){
            var singleSend = new Communication(Utils.Direction.SEND);
            var recursiveSend = new Communication(Utils.Direction.SEND);
            recursiveSend.addLeafCommunicationRoots(new ArrayList<>(List.of(recursiveSend)));
            assertFalse(singleSend.supports(recursiveSend));
        }
        @Test
        public void nonEmptyNonVoidNextNodesDoesntSupportEmpytNodes(){
            var com1 = new Communication(Utils.Direction.SEND);
            var com2 = new Communication(Utils.Direction.SEND);
            var cChild = new Communication(Utils.Direction.RECEIVE);
            com2.addLeafCommunicationRoots(new ArrayList<>(List.of(cChild)));
            assertFalse(com2.supports(com1));
        }
        @Test
        public void empytNodesDoesntSupportnonEmptyNonVoidNextNodes(){
            var com1 = new Communication(Utils.Direction.SEND);
            var com2 = new Communication(Utils.Direction.SEND);
            var cChild = new Communication(Utils.Direction.RECEIVE);
            com2.addLeafCommunicationRoots(new ArrayList<>(List.of(cChild)));
            assertFalse(com1.supports(com2));
        }
        @Test
        public void deepCommunicationsSupport(){
            var com1 = new Communication(Utils.Direction.SEND);
            var com2 = new Communication(Utils.Direction.SEND);
            var com11 = new Communication(Utils.Direction.RECEIVE);
            var com21 = new Communication(Utils.Direction.RECEIVE);
            com1.addLeafCommunicationRoots(new ArrayList<>(List.of(com11)));
            com2.addLeafCommunicationRoots(new ArrayList<>(List.of(com21)));
            assertTrue(com1.supports(com2));
        }
        @Test
        public void deepCommunicationsNonSupport(){
            var com1 = new Communication(Utils.Direction.SEND);
            var com2 = new Communication(Utils.Direction.SEND);
            var com11 = new Communication(Utils.Direction.RECEIVE);
            var com21 = new Communication(Utils.Direction.SEND);
            com1.addLeafCommunicationRoots(new ArrayList<>(List.of(com11)));
            com2.addLeafCommunicationRoots(new ArrayList<>(List.of(com21)));
            assertFalse(com1.supports(com2));
        }
        @Test
        public void recursiveSendShouldSupportsDoubleSend(){
            var com1 = new Communication(Utils.Direction.SEND);
            com1.addRecursiveCallee(com1);
            com1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                new Communication(Utils.Direction.VOID))
            ));
            var com2 = new Communication(Utils.Direction.SEND);
            var com21 = new Communication(Utils.Direction.SEND);
            com2.addLeafCommunicationRoots(new ArrayList<>(List.of(com21)));
            assertTrue(com1.supports(com2));
        }
        @Test
        public void recursiveSendDoesntSupportsPresenceOfReceive(){
            var com1 = new Communication(Utils.Direction.SEND);
            com1.addRecursiveCallee(com1);
            com1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.VOID))
            ));
            var com2 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.SEND,
                            new Communication(Utils.Direction.RECEIVE)));
            assertFalse(com1.supports(com2));
        }
        @Test
        public void recursiveSendWithoutVoidEscapeDoesntSupportsDoubleSend(){
            var com1 = new Communication(Utils.Direction.SEND);
            com1.addRecursiveCallee(com1);
            var com2 = new Communication(Utils.Direction.SEND);
            var com21 = new Communication(Utils.Direction.SEND);
            com2.addLeafCommunicationRoots(new ArrayList<>(List.of(com21)));
            assertFalse(com1.supports(com2));
        }
    }
}
