import mychor.ProceduresCallGraph;
import mychor.StackFrame;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ProceduresCallGraphTest {

    // CONSTRUCTORS AND SIZE
    @Nested
    class ConstructorsTest{
        @Test
        public void emptyConstructorSizeNil(){
            assertEquals(new ProceduresCallGraph().size(), 0);
        }
        @Test
        public void nonEmptyConstructorSizeN(){
            assertEquals(new ProceduresCallGraph(new ArrayList<>(List.of(
                    new StackFrame("a"),
                    new StackFrame("b"),
                    new StackFrame("c")
            ))).size(), 3);
        }
    }
    @Nested
    class EqualityTest{
        //EQUALITY
        @Test
        public void isTriviallyEqual(){
            assertEquals(new ProceduresCallGraph(), new ProceduresCallGraph());
        }
        @Test
        public void isReflexivelyEqual(){
            var pcg = new ProceduresCallGraph(new ArrayList<>(List.of(
                    new StackFrame("var"),
                    new StackFrame("var2")
            )));
            assertEquals(pcg, pcg);
        }
        @Test
        public void isUnequalClass(){
            assertNotEquals(new ProceduresCallGraph(), new ArrayList<StackFrame>());
        }
        @Test
        public void isUnequalN0(){
            var pcg = new ProceduresCallGraph(new ArrayList<>(List.of(
                    new StackFrame("var"),
                    new StackFrame("var2")
            )));
            assertNotEquals(pcg, new ProceduresCallGraph());
        }
        @Test
        public void isUnequalNN(){
            var pcg = new ProceduresCallGraph(new ArrayList<>(List.of(
                    new StackFrame("var"),
                    new StackFrame("var2")
            )));
            var pcg2 = new ProceduresCallGraph(new ArrayList<>(List.of(
                    new StackFrame("var3"),
                    new StackFrame("var4")
            )));
            assertNotEquals(pcg, pcg2);
        }
        @Test
        public void isEqualComplex(){
            var sf1 = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111"),
                            new StackFrame("var112"),
                            new StackFrame("var113")
                    ))),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf2 = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            var sf1b = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111"),
                            new StackFrame("var112"),
                            new StackFrame("var113")
                    ))),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf2b = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            assertEquals(
                    new ProceduresCallGraph(new ArrayList<>(List.of(sf1, sf2))),
                    new ProceduresCallGraph(new ArrayList<>(List.of(sf1b, sf2b)))
            );
        }
    }
    @Nested
    class VarnameFindingTest{
        @Test
        public void varNameNotInGraph(){
            var roots = new ArrayList<>(List.of(
                    new StackFrame("X"),
                    new StackFrame("Y"),
                    new StackFrame("Z")
            ));
            var pcg = new ProceduresCallGraph(roots);
            assertFalse(pcg.isVarNameInGraph("W"));
        }
        @Test
        public void varNameTriviallyNotInGraph(){
            var pcg = new ProceduresCallGraph();
            assertFalse(pcg.isVarNameInGraph("var"));
        }
        @Test
        public void varNameInGraph(){
            var roots = new ArrayList<>(List.of(
                    new StackFrame("X"),
                    new StackFrame("Y"),
                    new StackFrame("Z")
            ));
            var pcg = new ProceduresCallGraph(roots);
            assertTrue(pcg.isVarNameInGraph("X"));
        }
    }
    @Nested
    class LeavesAdditionTest{
        @Test
        public void addLeafFrameTrivial(){
            var sf = new StackFrame("var");
            var pcg = new ProceduresCallGraph();
            pcg.addLeafFrame(sf);
            assertEquals(pcg, new ProceduresCallGraph(new ArrayList<>(List.of(sf))));
        }
        @Test
        public void addLeafFramesTrivial(){
            var sf = new StackFrame("var");
            var sf1 = new StackFrame("var1");
            var pcg = new ProceduresCallGraph();
            pcg.addLeafFrames(new ArrayList<>(List.of(sf,sf1)));
            assertEquals(pcg, new ProceduresCallGraph(new ArrayList<>(List.of(sf, sf1))));
        }
        @Test
        public void addLeafFrameCompare(){
            var sf1 = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11"),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf1b = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111")
                    ))),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf2 = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            var pcg = new ProceduresCallGraph(new ArrayList<>(List.of(sf1, sf2)));
            pcg.addLeafFrame(new StackFrame("var111"));

            var pcgComp = new ProceduresCallGraph(new ArrayList<>(List.of(sf1b, sf2)));
            assertEquals(pcg, pcgComp);
        }
        @Test
        public void addLeafFramesCompare(){
            var sf1 = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11"),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf1b = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111"),
                            new StackFrame("var112"),
                            new StackFrame("var113")
                    ))),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf2 = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            var pcg = new ProceduresCallGraph(new ArrayList<>(List.of(sf1, sf2)));
            pcg.addLeafFrames(new ArrayList<>(List.of(
                    new StackFrame("var111"),
                    new StackFrame("var112"),
                    new StackFrame("var113")
            )));

            var pcgComp = new ProceduresCallGraph(new ArrayList<>(List.of(sf1b, sf2)));
            assertEquals(pcg, pcgComp);
        }
    }
    @Nested
    class DuplicationTest{
        @Test
        public void duplicateGraph(){
            var sf1 = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111"),
                            new StackFrame("var112"),
                            new StackFrame("var113")
                    ))),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf2 = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            var pcg=new ProceduresCallGraph(new ArrayList<>(List.of(sf1, sf2)));
            var pcgAlmost = pcg.duplicate();
            assertTrue(pcg != pcgAlmost && pcg.equals(pcgAlmost));
        }
    }
    @Nested
    class LoopingTest{
        @Test
        public void graphHasLoop(){
            var sf1 = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111"),
                            new StackFrame("var1"),
                            new StackFrame("var113")
                    ))),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf2 = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            var pcg=new ProceduresCallGraph(new ArrayList<>(List.of(sf1, sf2)));
            assertTrue(pcg.containsLoop());
        }
        @Test
        public void graphDoesntHaveLoop(){
            var sf1 = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111"),
                            new StackFrame("var112"),
                            new StackFrame("var113")
                    ))),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf2 = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            var pcg=new ProceduresCallGraph(new ArrayList<>(List.of(sf1, sf2)));
            assertFalse(pcg.containsLoop());
        }
        @Test
        public void graphDoesntHaveLoop2(){
            var sf1 = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111"),
                            new StackFrame("var111"),
                            new StackFrame("var111")
                    ))),
                    new StackFrame("var11"),
                    new StackFrame("var13")
            )));
            var sf2 = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            var pcg=new ProceduresCallGraph(new ArrayList<>(List.of(sf1, sf2)));
            assertFalse(pcg.containsLoop());
        }
        @Test
        public void recursiveVariableIsDetected(){
            var sf1 = new StackFrame("var1", new ArrayList<>(List.of(
                    new StackFrame("var11", new ArrayList<>(List.of(
                            new StackFrame("var111"),
                            new StackFrame("var1"),
                            new StackFrame("var113")
                    ))),
                    new StackFrame("var12"),
                    new StackFrame("var13")
            )));
            var sf2 = new StackFrame("var2", new ArrayList<>(List.of(
                    new StackFrame("var21")
            )));
            var pcg=new ProceduresCallGraph(new ArrayList<>(List.of(sf1, sf2)));
            assertEquals(pcg.getLoopedVariables().size(), 1);
            assertTrue(pcg.getLoopedVariables().contains("var1"));
        }
    }
}
