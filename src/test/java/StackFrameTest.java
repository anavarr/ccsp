import mychor.StackFrame;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StackFrameTest {
    @Test
    public void varNameIsPresent(){
        var sf = new StackFrame("method", new ArrayList<>());
        assertEquals(sf.isVarNameInGraph("method"), true);
    }

    @Test
    public void exampleHorizontalMergingSameProcess(){
        var callGraph1 = new HashMap<String, ArrayList<StackFrame>>();
        var callGraph2 = new HashMap<String, ArrayList<StackFrame>>();
        callGraph1.put(
                "client",
                new ArrayList<>(List.of(
                        new StackFrame(
                                "X_Client_GET",
                                new ArrayList<>(List.of(
                                      new StackFrame("X_CLient_GET_success"),
                                      new StackFrame("X_Client_GET_failure")
                                ))
                        )
                ))
        );
        callGraph2.put(
                "client",
                new ArrayList<>(List.of(
                        new StackFrame(
                                "X_Client_POST",
                                new ArrayList<>(List.of(
                                        new StackFrame("X_CLient_POST_success"),
                                        new StackFrame("X_Client_POST_failure")
                                ))
                        ))
                )
        );
        var callGraphMerged = new HashMap<String, ArrayList<StackFrame>>();
        callGraphMerged.put("client", new ArrayList<>(List.of(
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
                ))
        ));

        assertEquals(StackFrame.mergeCalledProceduresHorizontal(callGraph1, callGraph2), callGraphMerged);
    }
}
