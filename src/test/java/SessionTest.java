import mychor.Communication;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionTest {

    Session refl = new Session("client", "client",
            new Communication(Utils.Direction.SEND));
    Session a = new Session("client", "server",
            new Communication(Utils.Direction.SEND));
    Session b = new Session("client", "server",
            new Communication(Utils.Direction.SEND));

    Session clientSide = new Session("client", "server", new ArrayList<>(List.of(
            new Communication(Utils.Direction.SEND),
            new Communication(Utils.Direction.RECEIVE)
    )));
    Session serverSide = new Session("server", "client", new ArrayList<>(List.of(
            new Communication(Utils.Direction.RECEIVE),
            new Communication(Utils.Direction.SEND)
    )));

    Session longSession = new Session("server", "client", new ArrayList<>(List.of(
            new Communication(Utils.Direction.RECEIVE,new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE),
                            new Communication(Utils.Direction.SEND)
                    ))),
                    new Communication(Utils.Direction.SEND)
            ))),
                new Communication(Utils.Direction.SEND)
    )));

    @Nested
    class EqualityTest{
        @Test
        public void notEqualIfDifferentClass(){
            var s1 = new Session("a", "b", new Communication(Utils.Direction.VOID));
            var s2 = new Communication(Utils.Direction.VOID);
            assertNotEquals(s1, s2);
        }
        @Test
        public void isEqualTrivially(){
            assertEquals(a,a);
        }
        @Test
        public void isEqualSingleCom(){
            assertEquals(a,b);
        }
        @Test
        public void equalExample(){
            var s1 = new Session("a", "b", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.RECEIVE)
            )));
            var s2 = new Session("a", "b", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.RECEIVE)
            )));
            assertEquals(s1,s2);
        }
        @Test
        public void notEqualIfDifferentRootsSize(){
            var s1 = new Session("a", "b", new Communication(Utils.Direction.SEND));
            var s2 = new Session("a", "b", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.RECEIVE)
            )));
            assertNotEquals(s1,s2);
        }
        @Test
        public void notEqualIfDifferentEnds(){
            var s1 = new Session("a", "c", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.RECEIVE)
            )));
            var s2 = new Session("a", "b", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.RECEIVE)
            )));
            assertNotEquals(s1,s2);
        }
        @Test
        public void notEqualsIfChildrenNotEquals(){
            var s1 = new Session("a", "b", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.RECEIVE, new Communication(Utils.Direction.RECEIVE))
            )));
            var s2 = new Session("a", "b", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.RECEIVE, new Communication(Utils.Direction.SEND))
            )));
            assertNotEquals(s1,s2);

        }

        // REFLEXIVITY
        @Test
        public void isReflexive(){
            assertTrue(refl.isSelfComm());
        }
        @Test
        public void isNotReflexive(){
            assertFalse(a.isSelfComm());
        }
    }
    @Nested
    class SizeTest{
        @Test
        public void checkSize1(){
            assertEquals(a.getCommunicationsSize(), 1);
        }
        @Test
        public void checkSizeN(){
            assertEquals(longSession.getCommunicationsSize(), 6);
         }
    }
    @Nested
    class SessionEndsValidityTest{
        @Test
        public void sessionValidityIsCommutative(){
            assertTrue(a.areEnds("client", "server"));
            assertTrue(a.areEnds("server", "client"));
        }

        @Test
        public void sessionValidityIsNotReflexive(){
            assertFalse(a.areEnds("client", "client"));
            assertFalse(a.areEnds("server", "server"));
        }
        @Test
        public void sessionValidityRejects(){
            assertFalse(a.areEnds("client", "proxy"));
            assertFalse(a.areEnds("proxy", "server"));
            assertFalse(a.areEnds("server", "proxy"));
            assertFalse(a.areEnds("proxy", "client"));
        }
        @Test
        public void sessionDoesntHaveSameEnds(){
            assertFalse(serverSide.hasSameEnds(clientSide));
        }
        @Test
        public void sessionHasSameEnds(){
            assertTrue(a.hasSameEnds(clientSide));
        }
    }
    @Nested
    class InitiatorsTest{
        @Test
        public void sessionFirstSenderIsInitiator(){
            assertEquals(b.getInitiator(), "client");
        }
        @Test
        public void sessionFirstReceiverIsInitiated(){
            assertEquals(b.getInitiated(), "server");
        }
        @Test
        public void sessionFirstSelectorIsInitiator(){
            var s = new Session("a", "b", new Communication(Utils.Direction.SELECT,
                    "GET", new ArrayList<>(List.of(new Communication(Utils.Direction.RECEIVE)))));
            assertEquals(s.getInitiator(), "a");
        }
        @Test
        public void sessionFirstBranchingIsInitiated(){
            var s = new Session("a", "b", new Communication(Utils.Direction.BRANCH,
                    "GET", new ArrayList<>(List.of(new Communication(Utils.Direction.SEND)))));
            assertEquals(s.getInitiated(), "a");
        }
        @Test
        public void sessionFirstDirectReceiverIsInitiated(){
            var s = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            assertEquals(s.getInitiated(), "a");
        }
    }
    @Nested
    class ComplementarityTest{

        @Test
        public void sessionComplementarityExample(){
            assertTrue(clientSide.isComplementary(serverSide));
        }
        @Test
        public void sessionNonComplementaryPeers(){
            Session serverSide = new Session("server", "client2", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE),
                    new Communication(Utils.Direction.SEND)
            )));
            assertFalse(clientSide.isComplementary(serverSide));
        }
        @Test
        public void sessionNonComplementaryInitiators(){
            Session serverSide = new Session("server", "client", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.SEND)
            )));
            assertFalse(clientSide.isComplementary(serverSide));
        }
        @Test
        public void sessionNonComplementarySize(){
            Session serverSide = new Session("server", "client", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE),
                    new Communication(Utils.Direction.SEND),
                    new Communication(Utils.Direction.SEND)
            )));
            assertFalse(clientSide.isComplementary(serverSide));
        }
        @Test
        public void sessionNonComplementaryLate(){
            Session clientSide = new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND)
                    )))
            )));
            Session serverSide = new Session("server", "client", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND)
                    )))
            )));
            assertFalse(clientSide.isComplementary(serverSide));
        }
    }
    @Nested
    class BranchingValidityTest{
        @Test
        public void branchingValidityTrivial(){
            Session s = new Session("client", "server", new ArrayList<>());
            assertTrue(s.isBranchingValid());
        }
        @Test
        public void branchingValidityAllSelect(){
            Session s = new Session("client", "server", new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SELECT,
                                    "GET", new ArrayList<>(List.of(
                                            new Communication(Utils.Direction.SEND)
                                    ))),
                            new Communication(Utils.Direction.SELECT,"POST"),
                            new Communication(Utils.Direction.SELECT, "DELETE")
                    ))
            );
            assertTrue(s.isBranchingValid());
        }
        @Test
        public void branchingValidityAllBranch(){
            Session s = new Session("client", "server", new ArrayList<>(List.of(
                            new Communication(Utils.Direction.BRANCH,
                                    "GET", new ArrayList<>(List.of(
                                            new Communication(Utils.Direction.SEND)
                                    ))),
                            new Communication(Utils.Direction.BRANCH,"POST"),
                            new Communication(Utils.Direction.BRANCH, "DELETE")
                    ))
            );
            assertTrue(s.isBranchingValid());
        }
        @Test
        public void branchingValidityAllSame(){
            Session s = new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND),
                            new Communication(Utils.Direction.SEND),
                            new Communication(Utils.Direction.SEND)
                    )))
            )));
            assertTrue(s.isBranchingValid());
        }
        @Test
        public void branchingInvalidity(){
            Session s = new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND,
                                    new ArrayList<>(List.of(
                                            new Communication(Utils.Direction.SEND)
                                    ))),
                            new Communication(Utils.Direction.SEND),
                            new Communication(Utils.Direction.SEND)
                    )))
            )));
            assertFalse(s.isBranchingValid());
        }
        @Test
        public void branchingInvalidityDirect(){
            Session s = new Session("a", "b", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE),
                    new Communication(Utils.Direction.SEND)
            )));
            assertFalse(s.isBranchingValid());
        }
    }
    @Nested
    class SessionExpansionTest{
        @Test
        public void testExpandCommunication(){
            Session s = new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE)
            )));
            Communication c1 = new Communication(Utils.Direction.SEND);
            Communication c2 = new Communication(Utils.Direction.RECEIVE);
            s.expandTopCommunicationRoots(new ArrayList<>(List.of(c1,c2)));
            Session totalSession = new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE),
                    c1,
                    c2
            )));
            assertEquals(s, totalSession);
        }
        @Test
        public void compareAddLeafCommunication(){
            Session s = new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE)
            )));
            Communication c1 = new Communication(Utils.Direction.SEND);
            Communication c1prime = new Communication(Utils.Direction.SEND);
            Communication c2 = new Communication(Utils.Direction.RECEIVE);
            Communication c2prime = new Communication(Utils.Direction.RECEIVE);
            s.addLeafCommunicationRoots(new ArrayList<>(List.of(c1,c2)));
            Session totalSession = new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                            c1prime, c2prime
                    )))
            )));
            assertEquals(s, totalSession);
        }

        @Test
        public void addLeafEquivExpandWhenEmptySession(){
            var s1 = new Session("client", "server", new ArrayList<>());
            s1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SELECT, "left"),
                    new Communication(Utils.Direction.SELECT, "right")
            )));
            var s2 = new Session("client", "server", new ArrayList<>());
            s2.expandTopCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SELECT, "left"),
                    new Communication(Utils.Direction.SELECT, "right")
            )));
            assertEquals(s1, s2);
        }
    }
    @Nested
    class MergingTest{

        // MERGING
        @Test
        public void compareHorizontalMergingSameEnds(){
            Session s1 = new Session("client", "server", new ArrayList<>());
            s1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND))
            ));
            s1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE)
            )));

            Session s2 = new Session("client", "server", new ArrayList<>());
            s2.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND))
            ));
            s2.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.BRANCH,"left")
            )));

            var totalSessions = new ArrayList<Session>();
            totalSessions.add(new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE)
                    ))),
                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.BRANCH, "left")
                    )))
            ))));

            var merged = Session.mergeSessionsHorizontal(
                    new ArrayList<>(List.of(s1)),
                    new ArrayList<>(List.of(s2)));

            assertEquals(merged, totalSessions);
        }
        @SuppressWarnings("ExtractMethodRecommender")
        @Test
        public void compareHorizontalStubMerging(){
            var sessionSet1 = new ArrayList<Session>();
            Session s1 = new Session("client", "server", new ArrayList<>());
            s1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND))
            ));
            s1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE)
            )));
            Session s1b = new Session("client", "bus", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE)
                    )))
            )));
            sessionSet1.add(s1);
            sessionSet1.add(s1b);



            var sessionSet2 = new ArrayList<Session>();
            Session s2 = new Session("client", "proxy", new ArrayList<>());
            s2.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND))
            ));
            s2.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.BRANCH,"left")
            )));
            Session s2b = new Session("client", "bus", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND)
                    )))
            )));
            sessionSet2.add(s2);
            sessionSet2.add(s2b);

            var totalSessions = new ArrayList<Session>();
            totalSessions.add(new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE)
                    ))),
                    new Communication(Utils.Direction.VOID)
            ))));
            totalSessions.add(new Session("client", "proxy", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.BRANCH, "left")
                    ))),
                    new Communication(Utils.Direction.VOID)
            ))));
            totalSessions.add(new Session("client", "bus", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE)
                    ))),
                    new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND)
                    )))
            ))));

            var merged = Session.mergeSessionsHorizontalStub(
                    sessionSet1,
                    sessionSet2);

            assertTrue(merged.containsAll(totalSessions) && totalSessions.containsAll(merged));
        }
        @Test
        public void compareVerticalMergingSameEnds(){
            Session s1 = new Session("client", "server", new ArrayList<>());
            s1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND))
            ));
            s1.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE)
            )));

            Session s2 = new Session("client", "server", new ArrayList<>());
            s2.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND))
            ));
            s2.addLeafCommunicationRoots(new ArrayList<>(List.of(
                    new Communication(Utils.Direction.BRANCH,"left")
            )));

            var totalSessions = new ArrayList<Session>();
            totalSessions.add(new Session("client", "server", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                                    new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                                            new Communication(Utils.Direction.BRANCH, "left")
                                    )))
                            )))
                    )))
            ))));

            var merged = Session.mergeSessionsVertical(
                    new ArrayList<>(List.of(s1)),
                    new ArrayList<>(List.of(s2)));

            assertEquals(merged, totalSessions);
        }
    }
    @Nested
    class LeavesAccessTest{
        @Test
        public void singleNodeSessionShouldReturnNodeAsLeaves(){
            var leaves = a.getLeaves();
            assertEquals(leaves.size(), 1);
            assertEquals(leaves.get(0), new Communication(Utils.Direction.SEND));
        }
        @Test
        public void fiveNodesChainShouldReturnLastOneAsLeaf(){
            var c5 = new Communication(Utils.Direction.SELECT,"PUT");
            var c4 = new Communication(Utils.Direction.SEND, c5);
            var c3 = new Communication(Utils.Direction.RECEIVE, c4);
            var c2 = new Communication(Utils.Direction.RECEIVE, c3);
            var c1 = new Communication(Utils.Direction.SEND, c2);
            var s = new Session("a", "b", c1);
            var leaves = s.getLeaves();
            assertEquals(leaves.size(),1);
            assertEquals(leaves.get(0), c5);
        }
        @Test
        public void noCommunicationRootShouldReturnEmptyLeavesListe(){
            var s = new Session("a", "b", new ArrayList<>());
            assertEquals(s.getLeaves().size(), 0);
        }
    }
    @Nested
    class VoidCleaningTest{
        @Test
        public void voidCleaningCheckExample(){
            var c1 = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE,
                            new ArrayList<>(List.of(
                                    new Communication(Utils.Direction.SEND, new Communication(Utils.Direction.VOID)),
                                    new Communication(Utils.Direction.RECEIVE),
                                    new Communication(Utils.Direction.VOID)
                            ))),
                    new Communication(Utils.Direction.SEND,
                            new Communication(Utils.Direction.VOID))
            )));

            var c2 = new Communication(Utils.Direction.SEND, new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE,
                            new ArrayList<>(List.of(
                                    new Communication(Utils.Direction.SEND),
                                    new Communication(Utils.Direction.RECEIVE),
                                    new Communication(Utils.Direction.VOID)
                            ))),
                    new Communication(Utils.Direction.SEND)
            )));
            var s1 = new Session("a","b", c1);
            var s2 = new Session("a", "b", c2);
            s1.cleanVoid();
            assertEquals(s1, s2);
        }
    }
    @Nested
    class SupportTest{
        @Test
        public void sessionSupportsAllPossiblePaths(){

            Session longSession = new Session("server", "client", new ArrayList<>(List.of(
                    new Communication(Utils.Direction.RECEIVE,new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                                    new Communication(Utils.Direction.RECEIVE),
                                    new Communication(Utils.Direction.SEND)
                            ))),
                            new Communication(Utils.Direction.SEND)
                    ))),
                    new Communication(Utils.Direction.SEND)
            )));
            Session s1 = new Session("server", "client",
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND)));
            Session s2 = new Session("server", "client",
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.RECEIVE,
                                    new Communication(Utils.Direction.RECEIVE))));
            Session s3 = new Session("server", "client",
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.RECEIVE,
                                    new Communication(Utils.Direction.SEND))));
            Session sFalse = new Session("server", "client",
                    new Communication(Utils.Direction.RECEIVE));
            assertTrue(longSession.supports(s1));
            assertTrue(longSession.supports(s2));
            assertTrue(longSession.supports(s3));
            assertFalse(longSession.supports(sFalse));
        }
    }

}
