import mychor.ProceduresCallGraph;
import mychor.ProceduresCallGraphMap;
import mychor.StackFrame;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static mychor.ProceduresCallGraphMap.mergeCalledProceduresHorizontal;
import static mychor.ProceduresCallGraphMap.mergeCalledProceduresVertical;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StackFrameTest {

    @Nested
    class ConstructorTest{
        @Test
        public void varnameConstructorShouldCreateEmptyNextFramesList(){
            String varname = "SERVER_ROUTINE";
            var sf = new StackFrame(varname);
            assertEquals(sf.varName, varname);
            assertEquals(sf.getNextFramesSize(), 0);
        }
        @Test
        public void nextFramesConstructorShouldCreateStackFrameWithNextFramesList(){
            var sf11 = new StackFrame("METHOD2");
            var sf12 = new StackFrame("METHOD3");
            String varname = "SERVER_ROUTINE";
            var sf = new StackFrame(varname, new ArrayList<>(List.of(sf11, sf12)));
            assertEquals(sf.varName, varname);
            assertEquals(sf.getNextFramesSize(), 2);
            assertTrue(sf.containsStackFrames(List.of(sf11, sf12)));
        }
    }
    @Nested
    class EqualityTest{
        //EQUALITY TESTS
        @Test
        public void equalityTrivial(){
            var sf1 = new StackFrame("MyProcedure");
            var sf2 = new StackFrame("MyProcedure");
            assertEquals(sf1, sf2);
        }
        @Test
        public void equalityNextFrames(){
            var sf1 = new StackFrame("MyProcedure", new ArrayList<>(
                    List.of(
                            new StackFrame("myNextProcedure")
                    )
            ));
            var sf2 = new StackFrame("MyProcedure", new ArrayList<>(
                    List.of(
                            new StackFrame("myNextProcedure")
                    )
            ));
            assertEquals(sf1, sf2);
        }
        @Test
        public void unequalityClass(){
            var sf1 = new StackFrame("MyProcedure");
            var sf2 = "MyProcedure";
            assertNotEquals(sf1, sf2);
        }
        @Test
        public void unequalityName(){
            var sf1 = new StackFrame("MyProcedure");
            var sf2 = new StackFrame("MyProcedure2");
            assertNotEquals(sf1, sf2);
        }
        @Test
        public void unequalityNextFrames(){
            var sf1 = new StackFrame("MyProcedure", new ArrayList<>(
                    List.of(
                            new StackFrame("myNextProcedure")
                    )
            ));
            var sf2 = new StackFrame("MyProcedure", new ArrayList<>(
                    List.of(
                            new StackFrame("myNextProcedure2")
                    )
            ));
            assertNotEquals(sf1, sf2);
        }
    }
    @Nested
    class VarNamesTest{
        //FINDING VARNAMES
        @Test
        public void varNameIsTriviallyPresent(){
            var sf = new StackFrame("method", new ArrayList<StackFrame>());
            assertTrue(sf.isVarNameInGraph("method"));
        }
        @Test
        public void varNameIsPresent(){
            var sf = new StackFrame("MyProcedure",new ArrayList<>(
                    List.of(
                            new StackFrame("MyNextProcedure"),
                            new StackFrame("MyOtherProcedure")
                    )
            ));
            assertTrue(sf.isVarNameInGraph("MyOtherProcedure"));
        }

        //ADDING VARNAMES
        @Test
        public void addedNextFramesMakeVariablesFound(){
            var varName = "MyOtherProcedure";
            var sf = new StackFrame("MyProcedure",new ArrayList<>(
                    List.of(
                            new StackFrame("MyNextProcedure")
                    )
            ));
            assertFalse(sf.isVarNameInGraph(varName));
            sf.addNextFrames(new ArrayList<>(List.of(new StackFrame(varName))));
            assertTrue(sf.isVarNameInGraph(varName));
        }
        @Test
        public void addLeafFrameAddsFrameLast(){
            var sf = new StackFrame("MyProcedure",new ArrayList<>(
                    List.of(
                            new StackFrame("MyNextProcedure"),
                            new StackFrame("MyNextProcedure2", new ArrayList<>(
                                    List.of(
                                            new StackFrame("MyNextNextProcedure2")
                                    )
                            ))
                    )
            ));
            sf.addLeafFrame(new StackFrame("MyNextNextProcedure"));
            var totalSf = new StackFrame("MyProcedure",new ArrayList<>(
                    List.of(
                            new StackFrame("MyNextProcedure", new ArrayList<>(
                                    List.of(
                                            new StackFrame("MyNextNextProcedure")
                                    )
                            )),
                            new StackFrame("MyNextProcedure2", new ArrayList<>(
                                    List.of(
                                            new StackFrame("MyNextNextProcedure2")
                                    )
                            ))
                    )
            ));
        }
        @Test
        public void addLeafFramesAddsFramesLast(){
            var sf = new StackFrame("MyProcedure",new ArrayList<>(
                    List.of(
                            new StackFrame("MyNextProcedure"),
                            new StackFrame("MyNextProcedure2", new ArrayList<>(
                                    List.of(
                                            new StackFrame("MyNextProcedure2Next")
                                    )
                            ))
                    )
            ));
            sf.addLeafFrames(new ArrayList<>(List.of(
                    new StackFrame("MyNextNextProcedure"),
                    new StackFrame("MyNextNextProcedure2"),
                    new StackFrame("MyNextNextProcedure3"))
            ));
            var totalSf = new StackFrame("MyProcedure",new ArrayList<>(
                    List.of(
                            new StackFrame("MyNextProcedure", new ArrayList<>(
                                    List.of(
                                            new StackFrame("MyNextNextProcedure"),
                                            new StackFrame("MyNextNextProcedure2"),
                                            new StackFrame("MyNextNextProcedure3")
                                    )
                            )),
                            new StackFrame("MyNextProcedure2", new ArrayList<>(
                                    List.of(
                                            new StackFrame("MyNextProcedure2Next")
                                    )
                            ))
                    )
            ));
            assertEquals(sf, totalSf);
        }

        //Finding Self ?
        @Test
        public void recursiveCallContainsVarName(){
            var sf = new StackFrame("OPX");
            var sf2 = new StackFrame("OPY");
            sf.addLeafFrame(sf2);
            sf2.addNextFrames(new ArrayList<>(List.of(sf)));
            assertTrue(sf.containsSelf());
        }

        //finding looped
        @Test
        public void simpleStackShouldReturnNoLoopedOneCalled(){
            var sf = new StackFrame("OPX");
            var variables = sf.getLoopedVariables();
            assertEquals(variables.get(0).size(), 1);
            assertEquals(variables.get(1).size(), 0);
        }
        @Test
        public void simpleNChainStackShouldReturnNoLoopedNCalled(){
            var sf1 = new StackFrame("OPX1");
            var sf2 = new StackFrame("OPX2", new ArrayList<>(List.of(sf1)));
            var sf3 = new StackFrame("OPX3", new ArrayList<>(List.of(sf2)));
            var sf = new StackFrame("OPX", new ArrayList<>(List.of(sf3)));
            var variables = sf.getLoopedVariables();
            assertEquals(variables.get(0).size(), 4);
            assertTrue(variables.get(0).containsAll(List.of("OPX", "OPX1", "OPX2", "OPX3")));
            assertEquals(variables.get(1).size(), 0);
        }
        @Test
        public void simpleRecursiveStackShouldReturn1Looped2Called(){
            var sf = new StackFrame("OPX");
            var sf2 = new StackFrame("OPY");
            sf.addLeafFrame(sf2);
            sf2.addNextFrames(new ArrayList<>(List.of(sf)));
            var variables = sf.getLoopedVariables();
            var called = variables.get(0);
            var looped = variables.get(1);
            assertEquals(called.size(),2);
            assertEquals(looped.size(),1);
        }
    }
    @Nested
    class MergingTest{
        @Test
        public void exampleHorizontalMergingSameProcess(){
            var callGraphs1 = new ProceduresCallGraphMap();
            var callGraphs2 = new ProceduresCallGraphMap();
            callGraphs1.put(
                    "client",
                    new ProceduresCallGraph(
                            new ArrayList<>(List.of(
                                    new StackFrame(
                                            "X_Client_GET",
                                            new ArrayList<>(List.of(
                                                    new StackFrame("X_CLient_GET_success"),
                                                    new StackFrame("X_Client_GET_failure")
                                            ))
                                    )
                            ))
                    )
            );
            callGraphs2.put(
                    "client",
                    new ProceduresCallGraph(
                            new ArrayList<>(List.of(
                                    new StackFrame(
                                            "X_Client_POST",
                                            new ArrayList<>(List.of(
                                                    new StackFrame("X_CLient_POST_success"),
                                                    new StackFrame("X_Client_POST_failure")
                                            ))
                                    ))
                            )
                    )
            );
            var callGraphsMerged = new ProceduresCallGraphMap();
            callGraphsMerged.put("client", new ProceduresCallGraph(new ArrayList<>(List.of(
                    new StackFrame(
                            "X_Client_GET",
                            new ArrayList<>(List.of(
                                    new StackFrame("X_CLient_GET_success"),
                                    new StackFrame("X_Client_GET_failure")
                            ))
                    ),
                    new StackFrame(
                            "X_Client_POST",
                            new ArrayList<>(List.of(
                                    new StackFrame("X_CLient_POST_success"),
                                    new StackFrame("X_Client_POST_failure")
                            ))
                    ))))
            );
            assertEquals(mergeCalledProceduresHorizontal(callGraphs1, callGraphs2), callGraphsMerged);
        }


        @Test
        public void exampleHorizontalMergingDifferentProcess(){
            var callGraphs1 = new ProceduresCallGraphMap();
            var callGraphs2 = new ProceduresCallGraphMap();
            callGraphs1.put(
                    "client",
                    new ProceduresCallGraph(
                            new ArrayList<>(List.of(
                                    new StackFrame(
                                            "X_Client_GET",
                                            new ArrayList<>(List.of(
                                                    new StackFrame("X_CLient_GET_success"),
                                                    new StackFrame("X_Client_GET_failure")
                                            ))
                                    )
                            ))
                    )
            );
            callGraphs2.put(
                    "server",
                    new ProceduresCallGraph(
                            new ArrayList<>(List.of(
                                    new StackFrame(
                                            "X_Server_POST",
                                            new ArrayList<>(List.of(
                                                    new StackFrame("X_Server_POST_success"),
                                                    new StackFrame("X_Server_POST_failure")
                                            ))
                                    ))
                            )
                    )
            );
            var callGraphsMerged = new ProceduresCallGraphMap();
            callGraphsMerged
                    .put("client", new ProceduresCallGraph(new ArrayList<>(List.of(
                            new StackFrame(
                                    "X_Client_GET",
                                    new ArrayList<>(List.of(
                                            new StackFrame("X_CLient_GET_success"),
                                            new StackFrame("X_Client_GET_failure")
                                    ))
                            )
                    ))));
            callGraphsMerged
                    .put("server", new ProceduresCallGraph(new ArrayList<>(List.of(
                            new StackFrame(
                                    "X_Server_POST",
                                    new ArrayList<>(List.of(
                                            new StackFrame("X_Server_POST_success"),
                                            new StackFrame("X_Server_POST_failure")
                                    ))
                            )
                    ))));
            assertEquals(mergeCalledProceduresHorizontal(callGraphs1, callGraphs2), callGraphsMerged);
        }

        public void exampleVerticalMergingSameProcess(){
            var callGraphs1 = new ProceduresCallGraphMap();
            var callGraphs2 = new ProceduresCallGraphMap();
            callGraphs1.put(
                    "client",
                    new ProceduresCallGraph(
                            new ArrayList<>(List.of(
                                    new StackFrame(
                                            "X_Client_GET",
                                            new ArrayList<>(List.of(
                                                    new StackFrame("X_CLient_GET_success"),
                                                    new StackFrame("X_Client_GET_failure")
                                            ))
                                    )
                            ))
                    )
            );
            callGraphs2.put(
                    "client",
                    new ProceduresCallGraph(
                            new ArrayList<>(List.of(
                                    new StackFrame(
                                            "X_Client_POST",
                                            new ArrayList<>(List.of(
                                                    new StackFrame("X_CLient_POST_success"),
                                                    new StackFrame("X_Client_POST_failure")
                                            ))
                                    ))
                            )
                    )
            );
            var callGraphsMerged = new ProceduresCallGraphMap();
            callGraphsMerged.put("client", new ProceduresCallGraph(new ArrayList<>(List.of(
                            new StackFrame(
                                    "X_Client_GET",
                                    new ArrayList<>(List.of(
                                            new StackFrame("X_CLient_GET_success",
                                                    new ArrayList<>(List.of(
                                                            new StackFrame(
                                                                    "X_Client_POST",
                                                                    new ArrayList<>(List.of(
                                                                            new StackFrame("X_CLient_POST_success"),
                                                                            new StackFrame("X_Client_POST_failure")
                                                                    ))
                                                            ))))
                                    ))),
                            new StackFrame("X_Client_GET_failure")
                    ))
                    )
            );
            assertEquals(mergeCalledProceduresVertical(callGraphs1, callGraphs2), callGraphsMerged);
        }
        @Test
        public void exampleVerticalMergingDifferentProcess(){
            var callGraphs1 = new ProceduresCallGraphMap();
            var callGraphs2 = new ProceduresCallGraphMap();
            callGraphs1.put(
                    "client",
                    new ProceduresCallGraph(
                            new ArrayList<>(List.of(
                                    new StackFrame(
                                            "X_Client_GET",
                                            new ArrayList<>(List.of(
                                                    new StackFrame("X_Client_GET_success"),
                                                    new StackFrame("X_Client_GET_failure")
                                            ))
                                    )
                            ))
                    )
            );
            callGraphs2.put(
                    "server",
                    new ProceduresCallGraph(
                            new ArrayList<>(List.of(
                                    new StackFrame(
                                            "X_Server_POST",
                                            new ArrayList<>(List.of(
                                                    new StackFrame("X_Server_POST_success"),
                                                    new StackFrame("X_Server_POST_failure")
                                            ))
                                    ))
                            )
                    )
            );
            var callGraphsMerged = new ProceduresCallGraphMap();
            callGraphsMerged
                    .put("client", new ProceduresCallGraph(new ArrayList<>(List.of(
                            new StackFrame(
                                    "X_Client_GET",
                                    new ArrayList<>(List.of(
                                            new StackFrame("X_Client_GET_success"),
                                            new StackFrame("X_Client_GET_failure")
                                    ))
                            )
                    ))));
            callGraphsMerged
                    .put("server", new ProceduresCallGraph(new ArrayList<>(List.of(
                            new StackFrame(
                                    "X_Server_POST",
                                    new ArrayList<>(List.of(
                                            new StackFrame("X_Server_POST_success"),
                                            new StackFrame("X_Server_POST_failure")
                                    ))
                            )
                    ))));
            assertEquals(mergeCalledProceduresVertical(callGraphs1, callGraphs2), callGraphsMerged);
        }
    }
    @Nested
    class DuplicationTest{
        @Test
        public void duplicateStackFrameShouldBeEqualButNotSameToOriginal(){
            var sf11=new StackFrame("OPXY");
            sf11.addLeafFrame(new StackFrame("OPXYZ"));
            var sf12=new StackFrame("OPXZ");
            sf12.addLeafFrame(new StackFrame("OPXZY"));
            var sf = new StackFrame("OPX", new ArrayList<>(List.of(sf11, sf12)));
            var sfClone = sf.duplicate();
            assertEquals(sf, sfClone);
            assertNotSame(sf, sfClone);
        }
    }
}
