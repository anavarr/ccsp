import mychor.Communication;
import mychor.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class CommunicationTest {

    ArrayList<Communication> cSelect = new ArrayList<>(List.of(
            new Communication(Utils.Direction.SELECT, Utils.Arity.SINGLE,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)
                    )), "left"),
            new Communication(Utils.Direction.SELECT, Utils.Arity.SINGLE,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                                    new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE))
                    )), "left"),
            new Communication(Utils.Direction.SELECT, Utils.Arity.SINGLE,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE,
                                    new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE))
                    )), "left")
    ));
    ArrayList<Communication> cBranch = new ArrayList<>(List.of(
            new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)
                    )), "left"),
            new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE,
                                    new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE))
                    )), "left"),
            new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE,
                    new ArrayList<>(List.of(
                            new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                                    new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE))
                    )), "left")
    ));

    // EQUALITY
    @Test
    public void equalitySingleComm(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        var c2 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        assertTrue(c1.isEqual(c2));
    }
    @Test
    public void equalityMultiCommLine(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE,
                        new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)
                        )
                )
        );
        var c2 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE,
                        new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)
                        )
                )
        );
        assertTrue(c1.isEqual(c2));
    }
    @Test
    public void inequalityMultiCommLine(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE,
                        new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                                new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)
                        )
                )
        );
        var c2 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE,
                        new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE)
                        )
                )
        );
        assertFalse(c1.isEqual(c2));
    }

    // SIZE
    @Test
    public void checkSize1(){
        var c = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        assertEquals(c.getBranchesSize(), 1);
    }
    @Test
    public void checkSizeN(){
        assertEquals(cSelect.get(0).getBranchesSize(), 2);
    }

    // COMPLEMENTARITY
    @Test
    public void simpleComplementaritySR(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        var c2 = new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE);
        assertTrue(c1.isComplementary(c2) && c2.isComplementary(c1));
    }
    @Test
    public void simpleComplementaritySB(){
        var c1 = new Communication(Utils.Direction.SELECT, Utils.Arity.SINGLE, "left");
        var c2 = new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE, "left");
        assertTrue(c1.isComplementary(c2));
    }
    @Test
    public void simpleNonComplementaritySBLabel(){
        var c1 = new Communication(Utils.Direction.SELECT, Utils.Arity.SINGLE, "left");
        var c2 = new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE, "right");
        assertFalse(c1.isComplementary(c2));
    }
    @Test
    public void simpleNonComplementaritySR(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        var c2 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        assertFalse(c1.isComplementary(c2) && c2.isComplementary(c1));
    }
    @Test
    public void complexComplementarity(){
        var c1 = new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE, cSelect);
        var c2 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE, cBranch);
        assertTrue(c1.isComplementary(c2) && c2.isComplementary(c1));
    }

    // BRANCHING
    @Test
    public void validBranchingTrivial(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        assertTrue(c1.isBranchingValid());
        c1 = new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE);
        assertTrue(c1.isBranchingValid());
        c1 = new Communication(Utils.Direction.SELECT, Utils.Arity.SINGLE, "left");
        assertTrue(c1.isBranchingValid());
        c1 = new Communication(Utils.Direction.BRANCH, Utils.Arity.SINGLE, "left");
        assertTrue(c1.isBranchingValid());
    }
    @Test
    public void validBranchingAllSelect(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE, cSelect);
        assertTrue(c1.isBranchingValid());
    }
    @Test
    public void validBranchingAllBranch(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE, cBranch);
        assertTrue(c1.isBranchingValid());
    }
    @Test
    public void validBranchingAllSame(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE, new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE),
                new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE),
                new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)
        )));
        assertTrue(c1.isBranchingValid());
    }
    @Test
    public void invalidBranchingNotAllSame(){
        var c1 = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE, new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE),
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE),
                new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)
        )));
        assertFalse(c1.isBranchingValid());
    }

    // ADDING NODES
    @Test
    public void addRootsFirstLayer(){
        var c = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE);
        c.addLeafCommunicationRoots(new ArrayList<>(List.of(
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE))
        ));
        var cTotal = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE));

        assertTrue(c.isEqual(cTotal));
    }
    @Test
    public void addRootsSecondLayer(){
        var c = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE));
        c.addLeafCommunicationRoots(new ArrayList<>(List.of(
                new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE))
        ));
        var cTotal = new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE,
                new Communication(Utils.Direction.RECEIVE, Utils.Arity.SINGLE,
                        new Communication(Utils.Direction.SEND, Utils.Arity.SINGLE)));
        assertTrue(c.isEqual(cTotal));
    }

}
