import mychor.Communication;
import mychor.Utils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CommunicationTest {

    ArrayList<Communication> cSelect = new ArrayList<>(List.of(
            new Communication(Utils.Direction.SELECT,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND)
                    )), "left"),
            new Communication(Utils.Direction.SELECT,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE))
                    )), "left"),
            new Communication(Utils.Direction.SELECT,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE,
                                    new Communication(Utils.Direction.SEND))
                    )), "left")
    ));
    ArrayList<Communication> cBranch = new ArrayList<>(List.of(
            new Communication(Utils.Direction.BRANCH,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE)
                    )), "left"),
            new Communication(Utils.Direction.BRANCH,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE,
                                    new Communication(Utils.Direction.SEND))
                    )), "left"),
            new Communication(Utils.Direction.BRANCH,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE))
                    )), "left")
    ));

    // EQUALITY
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

    // SIZE
    @Test
    public void checkSize1(){
        var c = new Communication(Utils.Direction.SEND);
        assertEquals(c.getBranchesSize(), 1);
    }
    @Test
    public void checkSizeN(){
        assertEquals(cSelect.get(0).getBranchesSize(), 2);
    }

    // COMPLEMENTARITY
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
    public void complexComplementarity(){
        var c1 = new Communication(Utils.Direction.RECEIVE, cSelect);
        var c2 = new Communication(Utils.Direction.SEND, cBranch);
        assertTrue(c1.isComplementary(c2) && c2.isComplementary(c1));
    }

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

    // ADDING NODES
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
                                new ArrayList<>(List.of(c1)), "left"),
                        new Communication(Utils.Direction.SELECT,
                                new ArrayList<>(List.of(c2)), "left"),
                        c3
                )));
        var leaves = c.getLeaves();
        var leavesExpected = new ArrayList<>(List.of(c1,c2, c3));
        assertTrue(leaves.containsAll(leavesExpected) && leavesExpected.containsAll(leaves));
    }
}
