import dk.brics.automaton.Automaton;
import dk.brics.automaton.AutomatonProvider;
import dk.brics.automaton.RegExp;
import mychor.Communication;
import mychor.Session;
import mychor.Utils;
import mychor.automata.PatternUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static mychor.Utils.Direction.VOID;
import static mychor.automata.PatternUtils.pattern2DFA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Session2AutomatonTest extends ProgramReaderTest{

    @Nested
    class NonRecursiveTests{
        @Test
        public void simpleSendShouldReturn2statesAutomatonWishSTransition(){
            Session s = new Session("a","b", new Communication(Utils.Direction.SEND));
            var auto = pattern2DFA(s);
            RegExp acceptedLanguage = new RegExp("s");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, auto);
        }

        @Test
        public void cdtBranchingLeftShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/cdt_branching_left.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("client")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            assertTrue(automaton.run("") && automaton.run("r"));
            RegExp acceptedLanguage = new RegExp("r?");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void cdtBranchingRightShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/cdt_branching_right.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("client")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            RegExp acceptedLanguage = new RegExp("r?");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void cdtBranchingBothShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/cdt_branching_both.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("client")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            RegExp acceptedLanguage = new RegExp("r");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void branchCdtMsgShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/branching_cdt_msg.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("client")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            automaton.setDeterministic(false);
            RegExp acceptedLanguage = new RegExp("r(s|r)");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void branchingMsgBothShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/branching_message_both.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("server")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            automaton.setDeterministic(false);
            RegExp acceptedLanguage = new RegExp("r(s|r)");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void branchingMsgRightShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/branching_message_right.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("server")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            automaton.setDeterministic(false);
            RegExp acceptedLanguage = new RegExp("rr?");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void cdtSelectBothShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/cdt_select_both.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("client")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            automaton.setDeterministic(false);
            RegExp acceptedLanguage = new RegExp("s");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void cdtSelectLeftShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/cdt_select_left.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("client")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            automaton.setDeterministic(false);
            assertTrue(automaton.run("s") && automaton.run(""));
            RegExp acceptedLanguage = new RegExp("s?");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void cdtSelectRightShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/cdt_select_right.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("client")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            automaton.setDeterministic(false);
            RegExp acceptedLanguage = new RegExp("s?");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void msgBranchingShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/message_branching.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("server")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            automaton.setDeterministic(false);
            RegExp acceptedLanguage = new RegExp("sr");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }

        @Test
        public void msgCdtBranchingMsgShouldReturnSpecificLanguage() throws IOException {
            var ctx = testFile("behavioursCombinations/msg_cdt_branching_msg.sp").compilerCtx;
            var clientSession = ctx.sessions.stream().filter(s -> s.peerA().equals("client")).toList().getFirst();
            var automaton = pattern2DFA(clientSession);
            automaton.setDeterministic(false);
            RegExp acceptedLanguage = new RegExp("sr(s|r)");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, automaton);
        }
    }

    @Nested
    class RecursiveTests{
        @Test
        public void simpleRecursiveSendWithoutFinalStateShouldNotAcceptSstar(){
            RegExp acceptedLanguage = new RegExp("(s)*");
            var a = acceptedLanguage.toAutomaton();
            var com = new Communication(Utils.Direction.SEND);
            com.addLeafCommunicationRoots(new ArrayList<>(List.of(com)));
            Session s = new Session("a","b", com);
            var auto = pattern2DFA(s);
            assertNotEquals(a, auto);
        }
        @Test
        public void simpleRecursiveSendWithFinalStateShouldAcceptSplus(){
            RegExp acceptedLanguage = new RegExp("(s)+");
            var a = acceptedLanguage.toAutomaton();
            var com = new Communication(Utils.Direction.SEND);
            com.addLeafCommunicationRoots(new ArrayList<>(List.of(com, new Communication(VOID))));
            Session s = new Session("a","b", com);
            var auto = pattern2DFA(s);
            assertEquals(a, auto);
        }
        @Test
        public void recursiveSendOrVoidWithFinalStateShouldAcceptSstar(){
            RegExp acceptedLanguage = new RegExp("(s)*");
            var a = acceptedLanguage.toAutomaton();
            var com = new Communication(Utils.Direction.SEND);
            com.addLeafCommunicationRoots(new ArrayList<>(List.of(com, new Communication(VOID))));
            Session s = new Session("a","b", new ArrayList<>(List.of(com, new Communication(VOID))));
            var auto = pattern2DFA(s);
            assertEquals(a, auto);
        }

        @Test
        public void receiveEndOrSendRecursiveShoudNotAcceptSstarR() throws IOException {
            var ctx = testFile("receive_or_recursive_send.sp").compilerCtx;
            var s = ctx.sessions.stream().filter(se -> se.peerA().equals("client")).findFirst().get();
            RegExp acceptedLanguage = new RegExp("(s*)r");
            var a = acceptedLanguage.toAutomaton();
            assertEquals(a, pattern2DFA(s));
        }
    }
}
