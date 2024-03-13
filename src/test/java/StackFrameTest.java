import mychor.ProceduresCallGraph;
import mychor.ProceduresCallGraphMap;
import mychor.StackFrame;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static mychor.ProceduresCallGraphMap.mergeCalledProceduresHorizontal;
import static mychor.ProceduresCallGraphMap.mergeCalledProceduresVertical;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StackFrameTest {

    //TEST DISPLAY
    @Test
    public void displayStackFrame(){
        var sf = new StackFrame("MyProcedure");
        assertEquals(sf.toString(), "MyProcedure{}");
    }
    @Test
    public void displayStackFrames(){
        var sf = new StackFrame("MyProcedure",new ArrayList<>(
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
        var txt =
                """
                MyProcedure{
                \tMyNextProcedure{
                \t\tMyNextNextProcedure{},
                \t\tMyNextNextProcedure2{},
                \t\tMyNextNextProcedure3{},
                \t},
                \tMyNextProcedure2{
                \t\tMyNextProcedure2Next{},
                \t},
                }""";
        assertEquals(sf.toString(), txt);
    }

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
        System.out.println(totalSf);
        assertEquals(sf, totalSf);
    }



    //MERGING TESTS
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
