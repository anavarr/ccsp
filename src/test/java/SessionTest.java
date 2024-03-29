import mychor.Communication;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    //SESSION EQUALITY
    @Test
    public void isEqualTrivially(){
        assertEquals(a,a);
    }
    @Test
    public void isEqualSingleCom(){
        assertEquals(a,b);
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

    // SESSION SIZE
    @Test
    public void checkSize1(){
        assertEquals(a.getCommunicationsSize(), 1);
    }
    @Test
    public void checkSizeN(){
        assertEquals(longSession.getCommunicationsSize(), 6);
    }

    // SESSION ENDS VALIDITY
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

    // INITIATORS
    @Test
    public void sessionFirstSenderIsInitiator(){
        var presence = b.getInitiator();
        assertEquals(presence, "client");
    }
    @Test
    public void sessionFirstReceiverIsInitiated(){
        var presence = b.getInitiated();
        assertEquals(presence, "server");
    }

    // COMPLEMENTARITY
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

    // BRANCHING VALIDITY
    @Test
    public void branchingValidityTrivial(){
        Session s = new Session("client", "server", new ArrayList<>());
        assertTrue(s.isBranchingValid());
    }
    @Test
    public void branchingValidityAllSelect(){
        Session s = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                        new Communication(Utils.Direction.SELECT,
                                new ArrayList<>(List.of(
                                        new Communication(Utils.Direction.SEND)
                                )), "GET"),
                        new Communication(Utils.Direction.SELECT,"POST"),
                        new Communication(Utils.Direction.SELECT, "DELETE")
                )))
        )));
        assertTrue(s.isBranchingValid());
    }
    @Test
    public void branchingValidityAllBranch(){
        Session s = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                        new Communication(Utils.Direction.BRANCH,
                                new ArrayList<>(List.of(
                                        new Communication(Utils.Direction.SEND)
                                )), "GET"),
                        new Communication(Utils.Direction.BRANCH,"POST"),
                        new Communication(Utils.Direction.BRANCH, "DELETE")
                )))
        )));
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

    // SESSION EXPANSION
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
        Communication c2 = new Communication(Utils.Direction.RECEIVE);
        s.addLeafCommunicationRoots(new ArrayList<>(List.of(c1,c2)));
        Session totalSession = new Session("client", "server", new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE, new ArrayList<>(List.of(
                        c1,c2
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
