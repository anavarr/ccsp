package mychor.automata;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import mychor.Communication;
import mychor.Session;

import mychor.Utils;

import java.util.HashMap;

public class PatternUtils {
    static boolean allRecursiveCallsAreFinalStates = false;
    static HashMap<Communication, State> communicationStatesRepo;
    public static char getCommunicationChar(Communication c){
        if(c.getDirection() == Utils.Direction.SEND || c.getDirection() == Utils.Direction.SELECT){
            return 's';
        }else if(c.getDirection() == Utils.Direction.RECEIVE || c.getDirection() == Utils.Direction.BRANCH){
            return 'r';
        }else{
            return 0;
        }
    }
    public static Automaton pattern2DFA(Session s){
        communicationStatesRepo = new HashMap<>();
        State startState = new State();
        for (Communication communicationsRoot : s.communicationsRoots()) {
            var state = traverseIt(communicationsRoot);
            if(state.isAccept()) {
                startState.setAccept(true);
            }
            for (Transition transition : state.getTransitions()) {
                startState.addTransition(transition);
            }
        }
        var auto = new Automaton();
        auto.setInitialState(startState);
        auto.setDeterministic(false);
        return auto;
    }
    public static Automaton pattern2DFA(Communication c){
        communicationStatesRepo = new HashMap<>();
        State startState = traverseIt(c);
        var auto = new Automaton();
        auto.setInitialState(startState);
        auto.setDeterministic(false);
        return auto;
    }

    private static State traverseIt(Communication c){
        var s1 = new State();
        communicationStatesRepo.put(c, s1);
        // recursion is not considered yet
        if(getCommunicationChar(c) == 0){
            s1.setAccept(true);
            return s1;
        }
        if(c.isFinal()){
            var s2 = new State();
            s2.setAccept(true);
            s1.addTransition(new Transition(getCommunicationChar(c), s2));
        }else{
            for (Communication nextCommunicationNode : c.getNextCommunicationNodes()) {
                var nextState = traverseIt(nextCommunicationNode);
                s1.addTransition(new Transition(getCommunicationChar(c), nextState));
            }
        }
        for (Communication recursiveCallee : c.getRecursiveCallees()) {
            if(allRecursiveCallsAreFinalStates){
                s1.setAccept(true);
            }
            s1.addTransition(new Transition(getCommunicationChar(c), communicationStatesRepo.get(recursiveCallee)));
        }
        return s1;
    }
}
