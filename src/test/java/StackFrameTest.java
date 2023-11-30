import mychor.ProceduresCallGraph;
import mychor.ProceduresCallGraphMap;
import mychor.StackFrame;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import static mychor.ProceduresCallGraphMap.mergeCalledProceduresHorizontal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackFrameTest {
    @Test
    public void varNameIsPresent(){
        var sf = new StackFrame("method", new ArrayList<>());
        assertEquals(sf.isVarNameInGraph("method"), true);
    }

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
}
