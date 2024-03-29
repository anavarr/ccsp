import mychor.Behaviour;
import mychor.Call;
import mychor.Cdt;
import mychor.Comm;
import mychor.End;
import mychor.MessageQueues;
import mychor.None;
import mychor.SPcheckerRich;
import mychor.Utils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BehaviourTest {
    Behaviour myBehaviour = new End("myProcess");

    @Nested
    class ConstructorsTest{
        @Test
        public void creatingCallWithMultipleContinuationsShouldFail(){
            var nbs = new HashMap<String, Behaviour>();
            nbs.put("unfold", new End("myProcess"));
            nbs.put("unfold2", new End("myProcess"));
            assertThrows(IllegalArgumentException.class, () -> new Call("myProcess",nbs,  "MyFunction"));
        }
        @Test
        public void creatingCdtWithMoreThanTwoContinuationsShouldFail(){
            var nbs = new HashMap<String, Behaviour>();
            nbs.put("then", new End("myProcess"));
            nbs.put("else", new End("myProcess"));
            nbs.put("else-if", new End("myProcess"));
            assertThrows(IllegalArgumentException.class, () -> new Cdt("myProcess", nbs));
        }


    }
    @Nested
    class EqualityTest{
        @Test
        public void emptyCallsSameProcessSameVariableAreEqual(){
            var call1 = new Call("a", "MyV");
            var call2 = new Call("a", "MyV");
            assertEquals(call1, call2);
        }
        @Test
        public void emptyCallsSameProcessDifferentVariableAreNotEqual(){
            var call1 = new Call("a", "MyV");
            var call2 = new Call("a", "MyOtherV");
            assertNotEquals(call1, call2);
        }
        @Test
        public void emptyCallsDifferentProcessSameVariableAreNotEqual(){
            var call1 = new Call("a", "MyV");
            var call2 = new Call("b", "MyV");
            assertNotEquals(call1, call2);
        }
        @Test
        public void emptyConditionalsSameProcessSameExpressionAreEqual(){
            var cdt1 = new Cdt("myProcess", "check(x)");
            var cdt2 = new Cdt("myProcess", "check(x)");
            assertEquals(cdt1, cdt2);
        }
        @Test
        public void emptyConditionalsDifferentProcessSameExpressionAreNotEqual(){
            var cdt1 = new Cdt("myOtherProcess", "check(x)");
            var cdt2 = new Cdt("myProcess", "check(x)");
            assertNotEquals(cdt1, cdt2);
        }
        @Test
        public void emptyConditionalsSameProcessDifferentExpressionAreNotEqual(){
            var cdt1 = new Cdt("myProcess", "check(x)");
            var cdt2 = new Cdt("myProcess", "checkBis(x)");
            assertNotEquals(cdt1, cdt2);
        }
        @Test
        public void nonEmptyConditionalsSameProcessSameExpressionSameThenAreEqual(){
            var cdt1 = new Cdt("myProcess", "check(x)");
            cdt1.addBehaviour(new End("myProcess"));
            var cdt2 = new Cdt("myProcess", "check(x)");
            cdt2.addBehaviour(new End("myProcess"));
            assertEquals(cdt1, cdt2);
        }

        @Test
        public void NoneWithSameProcessesAreEqual(){
            var n1 = new None("myProcess");
            var n2 = new None("myProcess");
            assertEquals(n1, n2);
        }

        @Test
        public void NoneWithDifferentProcessesAreNotEqual(){
            var n1 = new None("myProcess");
            var n2 = new None("myOtherProcess");
            assertNotEquals(n1, n2);
        }
        @Test
        public void sendsSameEndsAreEqual(){
            var s1 = new Comm("a", "b", Utils.Direction.SEND, "");
            var s2 = new Comm("a", "b", Utils.Direction.SEND, "");
            assertEquals(s1, s2);
        }
        @Test
        public void sendsDifferentEndsAreNotEqual(){
            var s1 = new Comm("a", "b", Utils.Direction.SEND, "");
            var s2 = new Comm("a", "c", Utils.Direction.SEND, "");
            assertNotEquals(s1, s2);
        }
        @Test
        public void sendsReceivesAreNotEqual(){
            var s1 = new Comm("a", "b", Utils.Direction.SEND, "");
            var s2 = new Comm("a", "b", Utils.Direction.RECEIVE, "");
            assertNotEquals(s1, s2);
        }
        @Test
        public void sendsSelectsAreNotEqual(){
            var s1 = new Comm("a", "b", Utils.Direction.SEND, "");
            var s2 = new Comm("a", "b", Utils.Direction.SELECT, "");
            assertNotEquals(s1, s2);
        }
        @Test
        public void receivesSelectsAreNotEqual(){
            var s1 = new Comm("a", "b", Utils.Direction.RECEIVE, "");
            var s2 = new Comm("a", "b", Utils.Direction.SELECT, "");
            assertNotEquals(s1, s2);
        }
        @Test
        public void branchingDifferentBranchesAreNotEqual(){
            var nb1 = new HashMap<String, Behaviour>();
            nb1.put("GET", new End("myProcess"));
            nb1.put("PUT", new None("myProcesses"));
            var nb2 = new HashMap<String, Behaviour>();
            nb2.put("GET", new End("myProcess"));
            nb2.put("PUT", new End("myProcesses"));
            var s1 = new Comm("a", "b", nb1);
            var s2 = new Comm("a", "b", nb2);
            assertNotEquals(s1, s2);
        }
        @Test
        public void branchingSameBranchesAreEqual(){
            var nb1 = new HashMap<String, Behaviour>();
            nb1.put("GET", new End("myProcess"));
            nb1.put("PUT", new None("myProcesses"));
            var nb2 = new HashMap<String, Behaviour>();
            nb2.put("GET", new End("myProcess"));
            nb2.put("PUT", new None("myProcesses"));
            var s1 = new Comm("a", "b", nb1);
            var s2 = new Comm("a", "b", nb2);
            assertEquals(s1, s2);
        }

    }
    @Nested
    class ContinuationsAdditionTest{
        @Test
        public void addBranchToEmptyConditionalShouldReturnConditionalWithThen(){
            var cdt = new Cdt("myProcess", "check(x)");
            var nbs = new HashMap<String, Behaviour>();
            nbs.put("then", new End("myProcess"));
            var cdt2 = new Cdt("myProcess", nbs, "check(x)");
            cdt.addBehaviour(new End("myProcess"));
            assertEquals(cdt, cdt2);
        }
        @Test
        public void addBranchToThenableConditionalShouldAppendIt(){
            var cdt = new Cdt("myProcess", "check(x)");
            cdt.addBehaviour(new Call("myProcess", "MyFunction"));
            cdt.addBehaviour(new End("myProcess"));

            var nbs = new HashMap<String, Behaviour>();
            var nbs2 = new HashMap<String, Behaviour>();
            nbs2.put("unfold", new End("myProcess"));
            nbs.put("then", new Call("myProcess", nbs2, "MyFunction"));
            var cdt2 = new Cdt("myProcess", nbs, "check(x)");
            assertEquals(cdt, cdt2);
        }
        @Test
        public void addContinuationToEndShouldFail(){
            var e = new End("myProcess");
            assertFalse(e.addBehaviour(myBehaviour));
        }
        @Test
        public void addContinuationToNoneShouldFail(){
            var n = new None("myProcess");
            assertFalse(n.addBehaviour(myBehaviour));
        }
        @Test
        public void addContinuationToEmptyCallShouldReturnUnfoldableCall(){
            var call = new Call("myProcess", "MyVar");
            call.addBehaviour(new End("myProcess"));
            HashMap<String, Behaviour> nb = new HashMap<>(Map.of("unfold", new End("myProcess")));
            var fullCall = new Call("myProcess", nb,"MyVar");
            assertEquals(call, fullCall);
        }
        @Test
        public void addContinuationToUnfoldableCallShouldAppendIt(){
            var call = new Call("myProcess","MyVar");
            call.addBehaviour(new Call("myProcess", "MyVar"));
            call.addBehaviour(new End("myProcess"));

            HashMap<String, Behaviour> nb2 = new HashMap<>(
                    Map.of("unfold", new Call("myProcess",new HashMap<>(
                            Map.of("unfold", new End("myProcess")))
                            , "MyVar")));
            var fullCall = new Call("myProcess", nb2,"MyVar");
            assertEquals(call, fullCall);
        }
        @Test
        public void addContinuationToEmptySendReturnContinuatedSend(){
            var send = new Comm("a", "b", Utils.Direction.SEND, "");
            send.addBehaviour(new End("a"));
            var fullSend = new Comm("a", "b", Utils.Direction.SEND, "");
            fullSend.nextBehaviours.put(";", new End("a"));
            assertEquals(send, fullSend);
        }
        @Test
        public void addContinuationToEmptySelectReturnContinuatedSelection(){
            var select = new Comm("a", "b", Utils.Direction.SELECT, "GET");
            select.addBehaviour(new End("a"));
            var fullSelect = new Comm("a", "b", Utils.Direction.SELECT, "GET");
            fullSelect.nextBehaviours.put("GET;", new End("a"));
            assertEquals(select, fullSelect);
        }
        @Test
        public void addContinuationToEmptyBranchingReturnContinuatedBranching(){
            var br = new Comm("a", "b", Utils.Direction.BRANCH, "GET");
            br.addBehaviour(new End("a"));

            var fullNb = new HashMap<String, Behaviour>();
            fullNb.put("GET", new End("a"));
            var fullBr = new Comm("a", "b", fullNb);
            assertEquals(br, fullBr);
        }

        @Test
        public void addContinuationToContinuedBranchingShouldAppendIt(){
            var nb = new HashMap<String, Behaviour>();
            nb.put("GET", new Call("a","MyVar"));
            var br = new Comm("a", "b", nb);
            br.addBehaviour(new End("a"));

            var fullNb = new HashMap<String, Behaviour>();
            fullNb.put("GET", new Call("a", "MyVar"));
            fullNb.get("GET").nextBehaviours.put("unfold", new End("a"));
            var fullBr = new Comm("a","b", fullNb);
            assertEquals(br, fullBr);
        }
        
        @Test
        public void addContinuationToContinuedSendShouldAppendIt(){
            var nb = new HashMap<String, Behaviour>();
            nb.put(";", new Call("a","MyVar"));
            var send = new Comm("a","b", Utils.Direction.SEND, "");
            send.nextBehaviours = nb;
            send.addBehaviour(new End("a"));

            var fullSend = new Comm("a","b", Utils.Direction.SEND, "");
            fullSend.nextBehaviours = new HashMap<>(Map.of(";",
                    new Call("a", new HashMap<>(Map.of("unfold", new End("a"))), "MyVar")));
            assertEquals(send, fullSend);
        }

        @Test
        public void addContinuationToContinuedSelectShouldAppendIt(){
            var nb = new HashMap<String, Behaviour>();
            nb.put("GET;", new Call("a","MyVar"));
            var select = new Comm("a","b", Utils.Direction.SELECT, "GET");
            select.nextBehaviours = nb;
            select.addBehaviour(new End("a"));

            var fullSelect = new Comm("a","b", Utils.Direction.SELECT, "GET");
            fullSelect.nextBehaviours = new HashMap<>(Map.of("GET;",
                    new Call("a", new HashMap<>(Map.of("unfold", new End("a"))), "MyVar")));
            assertEquals(select, fullSelect);
        }
    }
    @Nested
    class FinalityTest{
        @Test
        public void emptyCdtIsNotFinal(){
            var cdt = new Cdt("myProcess", "check(x)");
            assertFalse(cdt.isFinal());
        }
        @Test
        public void nonEmptyCdtIsNotFinal(){
            var cdt = new Cdt("myProcess", "check(x)");
            cdt.addBehaviour(new End("myProcess"));
            assertFalse(cdt.isFinal());
        }

        @Test
        public void callIsNotFinal(){
            var call = new Call("myProcess", "MyFunction");
            var call2 = call.duplicate();
            call2.addBehaviour(new End("myProcess"));
            assertFalse(call.isFinal());
            assertFalse(call2.isFinal());
        }

        @Test
        public void endIsFinal(){
            assertTrue(myBehaviour.isFinal());
        }

        @Test
        public void noneIsNotFinal(){
            assertFalse(new None("myProcess").isFinal());
        }
        @Test
        public void sendIsNotFinal(){
            var com = new Comm("myProcess", "myOtherProcess", Utils.Direction.SEND, "");
            assertFalse(com.isFinal());
        }
        @Test
        public void receiveIsNotFinal(){
            var com = new Comm("myProcess", "myOtherProcess", Utils.Direction.RECEIVE, "");
            assertFalse(com.isFinal());
        }
        @Test
        public void selectIsNotFinal(){
            var com = new Comm("myProcess", "myOtherProcess", Utils.Direction.SELECT, "");
            assertFalse(com.isFinal());
        }
        @Test
        public void branchWithNonFinalBranchIsNotFinal(){
            var branches = new HashMap<String, Behaviour>();
            branches.put("GET", new End("myProcess"));
            branches.put("POST", new None("myProcesses"));
            var com = new Comm("myProcess", "myOtherProcess", branches);
            assertFalse(com.isFinal());
        }
        @Test
        public void branchWithFinalBranchesIsFinal(){
            var branches = new HashMap<String, Behaviour>();
            branches.put("GET", new End("myProcess"));
            branches.put("POST", new End("myProcesses"));
            var com = new Comm("myProcess", "myOtherProcess", branches);
            assertTrue(com.isFinal());
        }
    }
    @Nested
    class DuplicationTest{
        @Test
        public void emptyConditionalDuplicationEqualButNotSame(){
            var cdt = new Cdt("myProcess", "check(x");
            var cdtPrime = cdt.duplicate();
            assertNotSame(cdt, cdtPrime);
            assertEquals(cdt, cdtPrime);
        }
        @Test
        public void fullConditionalDuplicationEqualButNotSame(){
            var nb = new HashMap<String, Behaviour>();
            nb.put("then", new End("myProcess"));
            nb.put("else", new End("myProcess"));
            var cdt = new Cdt("myProcess", nb, "check(x");
            var cdtPrime = cdt.duplicate();
            assertNotSame(cdt, cdtPrime);
            assertEquals(cdt, cdtPrime);
        }
        @Test
        public void noneDuplicationEqualButNotSame(){
            var none = new None("myProcess");
            var nonePrime = none.duplicate();
            assertNotSame(none, nonePrime);
            assertEquals(none, nonePrime);
        }
        @Test
        public void endDuplicationEqualButNotSame(){
            var end = new End("myProcess");
            var endPrime = end.duplicate();
            assertNotSame(end, endPrime);
            assertEquals(end, endPrime);
        }
        @Test
        public void emptyCallDuplicationEqualButNotSame(){
            var call = new Call("myProcess","myVar");
            var callPrime = call.duplicate();
            assertNotSame(call, callPrime);
            assertEquals(call, callPrime);
        }
        @Test
        public void unfoldableCallDuplicationEqualButNotSame(){
            var call = new Call("myProcess", "myVar");
            call.addBehaviour(new End("myProcess"));
            var callPrime = call.duplicate();
            assertNotSame(call, callPrime);
            assertEquals(call, callPrime);
        }
        @Test
        public void sendDuplicationEqualButNotSame(){
            var send = new Comm("a", "b", Utils.Direction.SEND, "");
            send.addBehaviour(new End("a"));
            var sendPrime = send.duplicate();
            assertNotSame(send, sendPrime);
            assertEquals(send, sendPrime);
        }
        @Test
        public void selectDuplicationEqualButNotSame(){
            var select = new Comm("a", "b", Utils.Direction.SELECT, "GET");
            select.addBehaviour(new End("a"));
            var selectPrime = select.duplicate();
            assertNotSame(select, selectPrime);
            assertEquals(select, selectPrime);
        }

        @Test
        public void branchingDuplicationEqualButNotSame(){
            var nb = new HashMap<String, Behaviour>();
            nb.put("GET", new End("a"));
            nb.put("POST", new End("a"));
            var branching = new Comm("a", "b", nb);
            var branchingPrime = branching.duplicate();
            assertNotSame(branching, branchingPrime);
            assertEquals(branching, branchingPrime);
        }
    }
    @Nested
    class ReductionTest {
        MessageQueues mqs = new MessageQueues();
        HashMap<String, Behaviour> behaviours = new HashMap<>(Map.of(
                "a",new End("a"),
                "b", new End("b"),
                "c", new End("c")
        ));

        @Test
        public void reduceEndShouldReturnItself(){
            var e = new End("process");
            var oldMqs = mqs.duplicate();
            var reduced = e.reduce(behaviours, mqs);
            assertSame(e, reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceNoneShouldReturnEnd(){
            var n = new None("process");
            var oldMqs = mqs.duplicate();
            var reduced = n.reduce(behaviours, mqs);
            var e = new End("process");
            assertEquals(e, reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceEmptyCallShouldReturnEnd(){
            var ca = new Call("process","Var");
            var oldMqs = mqs.duplicate();
            var reduced = ca.reduce(behaviours, mqs);
            var e = new End("process");
            assertEquals(e, reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceNonEmptyCallShouldReturnUnfoldedBehaviour(){
            var continuation = new Comm("a", "b", Utils.Direction.SEND, "");
            var oldMqs = mqs.duplicate();
            var ca = new Call("process",new HashMap<>(Map.of("unfold", continuation )),"Var");
            var reduced = ca.reduce(behaviours, mqs);
            assertSame(continuation, reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceEmptyConditionalShouldReturnEnd(){
            var cdt = new Cdt("process","check(x)");
            var oldMqs = mqs.duplicate();
            var reduced = cdt.reduce(behaviours, mqs);
            assertEquals(new End("process"), reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceThenableConditionalShouldReturnThenBranch(){
            var thenBranch = new Call("process", "ThenVariable");
            var oldMqs = mqs.duplicate();
            var elseBranch = new Call("process", "ElseVariable");
            var nbs = new HashMap<String, Behaviour>(Map.of(
                    "then", thenBranch,
                    "else", elseBranch
            ));
            var cdt = new Cdt("process",nbs, "check(x)");
            var reduced = cdt.reduce(behaviours, mqs);
            assertSame(thenBranch, reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceElsableConditionalShouldReturnElseBranch(){
            var elseBranch = new Call("process", "ElseVariable");
            var oldMqs = mqs.duplicate();
            var nbs = new HashMap<String, Behaviour>(Map.of(
                    "else", elseBranch
            ));
            var cdt = new Cdt("process",nbs, "check(x)");
            var reduced = cdt.reduce(behaviours, mqs);
            assertSame(elseBranch, reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceSendShouldGiveContinuationAndAddMessageInQueue(){
            var send = new Comm("a", "b", Utils.Direction.SEND, "");
            var oldMqs = mqs.duplicate();
            send.reduce(behaviours, mqs);
            assertTrue(mqs.containsKey("a-b") && mqs.get("a-b").stream()
                    .filter(msg -> msg.direction() == Utils.Direction.SEND && msg.label() == null).toList().size() == 1);
        }
        @Test
        public void reduceSendShouldFailIfDestinationNotInEnv(){
            var send = new Comm("a", "b", Utils.Direction.SEND, "");
            var b = new HashMap<String, Behaviour>();
            var oldMqs = mqs.duplicate();
            var reduced = send.reduce(b, mqs);
            assertSame(send, reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceReceiveShouldFailIfMsgQueueIsEmpty(){
            var rcv = new Comm("a", "b", Utils.Direction.RECEIVE, "");
            var oldMqs = mqs.duplicate();
            var reduced = rcv.reduce(behaviours, mqs);
            assertSame(rcv, reduced);
            assertEquals(oldMqs, mqs);
        }
        @Test
        public void reduceReceiveShouldFailIfMsgQueueDoesntContainSend(){
            var rcv = new Comm("a", "b", Utils.Direction.RECEIVE, "");
            var oldMqs = mqs.duplicate();
            mqs.add(Utils.Direction.SELECT, "b", "a", "GET");
            var reduced = rcv.reduce(behaviours, mqs);
            assertSame(rcv, reduced);
        }
        @Test
        public void reduceReceiveShouldReturnContinuationAndConsumeSendFromQueue(){
            var rcv = new Comm("a", "b", Utils.Direction.RECEIVE, "");
            var cont = new End("a");
            rcv.addBehaviour(cont);
            var oldMqs = mqs.duplicate();
            mqs.add(Utils.Direction.SEND, "b", "a", null);
            var reduced = rcv.reduce(behaviours, mqs);
            assertSame(cont, reduced);
            assertTrue(mqs.areEmpty());
        }

        @Test
        public void reduceSelectShouldPutLabelInQueue(){
            var select = new Comm("a", "b", Utils.Direction.SELECT, "GET");
            select.reduce(behaviours, mqs);
            assertTrue(mqs.containsKey("a-b") && mqs.get("a-b").stream()
                    .filter(msg -> msg.direction() == Utils.Direction.SELECT && msg.label().equals("GET"))
                    .toList().size() == 1);
        }
        @Test
        public void reduceBranchShouldReturnContinuationAndConsumeSeelectFromQueue(){
            mqs.add(Utils.Direction.SELECT, "b", "a", "GET");
            var branching = new Comm("a", "b",new HashMap<>(Map.of(
                    "GET", new Call("a", "GET"),
                    "POST", new Call("a","POST"),
                    "DELETE", new Call("a", "Delete")
            )));
            var reduced = branching.reduce(behaviours, mqs);
            assertEquals(reduced, new Call("a", "GET"));
            assertTrue(mqs.areEmpty());
        }
        @Test
        public void reduceBranchShouldThrowErrorWhenLabelIsNotSupported(){
            mqs.add(Utils.Direction.SELECT, "b", "a", "PUT");
            var branching = new Comm("a", "b",new HashMap<>(Map.of(
                    "GET", new Call("a", "Get"),
                    "POST", new Call("a","Post"),
                    "DELETE", new Call("a", "Delete")
            )));
            var oldBranching = branching.duplicate();
            final Behaviour[] reduced = new Behaviour[1];
            assertThrows(RuntimeException.class, () -> {
                branching.reduce(behaviours, mqs);
            });
            assertEquals(oldBranching, branching);
            assertTrue(mqs.areEmpty());

        }
    }

    @Nested
    class BranchesExtractionTest {
        //base cases
        @Test
        public void endShouldReturnOnlyOneExecutionPath(){
           var b = new End("a");
           var branches = b.getBranches();
           assertEquals(branches.size(), 1);
           assertTrue(branches.contains(b));
        }
        @Test
        public void noneShouldReturnOnlyOneExecutionPath(){
            var b = new None("a");
            var branches = b.getBranches();
            assertEquals(branches.size(), 1);
            assertTrue(branches.contains(b));
        }
        @Test
        public void emptyCallShouldReturnOnlyOneExecutionPath(){
            var b = new Call("a","MyVar");
            var branches = b.getBranches();
            assertEquals(branches.size(), 1);
            assertTrue(branches.contains(b));
        }

        //conditions give left and right
        @Test
        public void conditionShouldReturnTwoExecutionPaths(){
            var hm = new HashMap<String, Behaviour>();
            hm.put("then", new End("a"));
            hm.put("else", new End("a"));
            var b = new Cdt("a", hm, "expr(e)");
            var branches = b.getBranches();
            assertEquals(branches.size(), 2);
            assertTrue(branches.contains(new End("a")));
        }
        @Test
        public void cdtAfterCallShouldReturnTwoThenElsePrefixedWithCall(){
            var hmCdt = new HashMap<String, Behaviour>();
            var selectGet = new Comm("a","b", Utils.Direction.SELECT, "GET");
            selectGet.addBehaviour(new End("a"));
            var selectPut = new Comm("a","b", Utils.Direction.SELECT, "PUT");
            selectPut.addBehaviour(new End("a"));
            hmCdt.put("then", selectGet);
            hmCdt.put("else", selectPut);
            var cdt = new Cdt("a", hmCdt, "expr(myvar)");

            var hmCall = new HashMap<String, Behaviour>();
            hmCall.put("unfold", cdt);
            var call = new Call("a", hmCall, "MyVar");
            var branches = call.getBranches();

            var hmThen = new HashMap<String, Behaviour>();
            hmThen.put("unfold", selectGet);
            var hmElse = new HashMap<String, Behaviour>();
            hmElse.put("unfold", selectPut);
            var callThen = new Call("a", hmThen,"MyVar");
            var callElse = new Call("a", hmElse, "MyVar");
            assertEquals(branches.size(), 2);
            assertNotEquals(callThen, callElse);
            assertTrue(branches.contains(callThen));
            assertTrue(branches.contains(callElse));
        }


        //simple case with continuations
        @Test
        public void sendShouldReturnContinuationExecutionPaths(){
            var b = new Comm("a", "b", Utils.Direction.SEND, "");
            b.addBehaviour(new End("a"));
            var branches = b.getBranches();
            assertEquals(branches.size(), 1);
            assertTrue(branches.contains(b));
        }
        @Test
        public void receiveShouldReturnContinuationExecutionPaths(){
            var b = new Comm("a", "b", Utils.Direction.RECEIVE, "");
            b.addBehaviour(new End("a"));
            var branches = b.getBranches();
            assertEquals(branches.size(), 1);
            assertTrue(branches.contains(b));
        }
        @Test
        public void selectShouldReturnContinuationExecutionPaths(){
            var b = new Comm("a", "b", Utils.Direction.SELECT, "GET");
            b.addBehaviour(new End("a"));
            var branches = b.getBranches();
            assertEquals(branches.size(), 1);
            assertTrue(branches.contains(b));
        }
        @Test
        public void nonEmptyCallShouldReturnContinuationExecutionPaths(){
            var b = new Call("a", "MyVar");
            b.addBehaviour(new End("a"));
            var branches = b.getBranches();
            assertEquals(branches.size(), 1);
            assertTrue(branches.contains(b));
        }

        //Getting executionPaths in Branching
        @Test
        public void branching2andConditionalShouldReturnFourPaths(){
            var hmCdt1 = new HashMap<String, Behaviour>();
            var selectBranch1Then = new Comm("a","b", Utils.Direction.SELECT, "1-THEN");
            selectBranch1Then.addBehaviour(new End("a"));
            var selectBranch1Else = new Comm("a","b", Utils.Direction.SELECT, "1-ELSE");
            selectBranch1Else.addBehaviour(new End("a"));
            hmCdt1.put("then", selectBranch1Then);
            hmCdt1.put("else", selectBranch1Else);

            var hmCdt2 = new HashMap<String, Behaviour>();
            var selectBranch2Then = new Comm("a","b", Utils.Direction.SELECT, "2-THEN");
            selectBranch2Then.addBehaviour(new End("a"));
            var selectBranch2Else = new Comm("a","b", Utils.Direction.SELECT, "2-ELSE");
            selectBranch2Else.addBehaviour(new End("a"));
            hmCdt2.put("then", selectBranch2Then);
            hmCdt2.put("else", selectBranch2Else);

            var cdt1 = new Cdt("a", hmCdt1, "expr(e1)");
            var cdt2 = new Cdt("a", hmCdt2, "expr(e2)");

            var hm = new HashMap<String, Behaviour>();
            hm.put("DOTHIS", cdt1);
            hm.put("DOTHAT", cdt2);
            var br = new Comm("a","b", hm);

            var branches = br.getBranches();

            var branching1 = new HashMap<String, Behaviour>();
            branching1.put("DOTHIS", selectBranch1Then.duplicate());
            branching1.put("DOTHAT", selectBranch2Then.duplicate());
            var branch1 = new Comm("a","b", branching1);

            var branching2 = new HashMap<String, Behaviour>();
            branching2.put("DOTHIS", selectBranch1Then.duplicate());
            branching2.put("DOTHAT", selectBranch2Else.duplicate());
            var branch2 = new Comm("a","b", branching2);

            var branching3 = new HashMap<String, Behaviour>();
            branching3.put("DOTHIS", selectBranch1Else.duplicate());
            branching3.put("DOTHAT", selectBranch2Then.duplicate());
            var branch3 = new Comm("a","b", branching3);

            var branching4 = new HashMap<String, Behaviour>();
            branching4.put("DOTHIS", selectBranch1Else.duplicate());
            branching4.put("DOTHAT", selectBranch2Else.duplicate());
            var branch4 = new Comm("a","b", branching4);

            assertEquals(branches.size(), 4);
            assertTrue(branches.containsAll(List.of(branch1, branch2, branch3, branch4)));
        }
    }

    @Nested
    class IntricatePathExtractionTest extends ProgramReaderTest{
        @Test
        public void IntricateBranchingEqualsSetOfSimpleProcesses() throws IOException{
            var spc = testFile("branching_paths/intricate_branching.sp");
            var subbranches  = new ArrayList<SPcheckerRich>();
            var i = 13;
            for (int i1 = 1; i1 < i; i1++) {
                subbranches.add(testFile("branching_paths/intricate_branching_comb_"+i1+".sp"));
                assertTrue(spc.getExecutionPaths().containsAll(subbranches.get(i1-1).getExecutionPaths()));
            }
        }
    }


}
