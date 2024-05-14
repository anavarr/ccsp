import mychor.ProceduresCallGraph;
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
            assertEquals(new ProceduresCallGraph(new ArrayList<>(List.of("a", "b","c"))).size(), 3);
        }
    }
    @Nested
    class EqualityTest {
        //EQUALITY
        @Test
        public void isTriviallyEqual() {
            assertEquals(new ProceduresCallGraph(), new ProceduresCallGraph());
        }

        @Test
        public void isReflexivelyEqual() {
            var pcg = new ProceduresCallGraph(new ArrayList<>(List.of("var", "var2")));
            assertEquals(pcg, pcg);
        }

        @Test
        public void isUnequalClass() {
            assertNotEquals(new ProceduresCallGraph(), new ArrayList<String>());
        }

        @Test
        public void isUnequalN0() {
            var pcg = new ProceduresCallGraph(new ArrayList<>(List.of("var", "var2")));
            assertNotEquals(pcg, new ProceduresCallGraph());
        }

        @Test
        public void isUnequalNN() {
            var pcg = new ProceduresCallGraph(new ArrayList<>(List.of("var", "var2")));
            var pcg2 = new ProceduresCallGraph(new ArrayList<>(List.of("var3", "var4")));
            assertNotEquals(pcg, pcg2);
        }
    }
    @Nested
    class VarnameFindingTest{
        @Test
        public void varNameNotInGraph(){
            var roots = new ProceduresCallGraph(new ArrayList<>(List.of("var","var2")));
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
            var roots = new ProceduresCallGraph(new ArrayList<>(List.of("var","var2")));
            var pcg = new ProceduresCallGraph(roots);
            assertTrue(pcg.isVarNameInGraph("var"));
            assertTrue(pcg.isVarNameInGraph("var2"));
        }
    }
    @Nested
    class LeavesAdditionTest {
        @Test
        public void addLeafFrameTrivial() {
            var sf = "var";
            var pcg = new ProceduresCallGraph();
            pcg.addLeafFrame(sf);
            assertEquals(pcg, new ProceduresCallGraph(new ArrayList<>(List.of(sf))));
        }

        @Test
        public void addLeafFramesTrivial() {
            var sf = "var";
            var sf1 = "var1";
            var pcg = new ProceduresCallGraph();
            pcg.addLeafFrames(new ArrayList<>(List.of(sf, sf1)));
            assertEquals(pcg, new ProceduresCallGraph(new ArrayList<>(List.of(sf, sf1))));
        }

        @Test
        public void addLeafFrameCompare() {
            var sf1 = new ArrayList<>(List.of("var1", "var11", "var12", "var13"));
            var sf1b = new ArrayList<>(List.of("var1", "var11", "var111", "var12", "var13"));
            var sf2 = new ArrayList<>(List.of("var2", "var21"));

            sf1.addAll(sf2);
            var pcg = new ProceduresCallGraph(sf1);
            pcg.addLeafFrame("var111");

            sf1b.addAll(sf2);
            var pcgComp = new ProceduresCallGraph(sf1b);
            assertEquals(pcg, pcgComp);
        }
    }
}
