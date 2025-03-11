package mychor.automata;
import dk.brics.automaton.Automaton;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;
import mychor.Comm;
import mychor.Communication;
import mychor.Session;

import mychor.TypeGraph;
import mychor.Utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatternUtils {
    static boolean allRecursiveCallsAreFinalStates = false;
    static HashMap<Communication, State> communicationStatesRepo;
    static HashMap<Character, String> labelMapping = new HashMap<>();
    static char biggestKey = 3;

    static{
        labelMapping.put((char) 0,"");
        labelMapping.put((char) 1,"send");
        labelMapping.put((char) 2,"receive");
    }

    static char stringToChar(String arg){
        for (Map.Entry<Character, String> characterStringEntry : labelMapping.entrySet()) {
            if(characterStringEntry.getValue().equals(arg)){
                return characterStringEntry.getKey();
            }
        }
        labelMapping.put(biggestKey, arg);
        biggestKey++;
        return (char) (biggestKey-1);
    }

    public static char getCommunicationChar(Communication c){
        if(c.getDirection() == Utils.Direction.SEND || c.getDirection() == Utils.Direction.SELECT){
            return 's';
        }else if(c.getDirection() == Utils.Direction.RECEIVE || c.getDirection() == Utils.Direction.BRANCH){
            return 'r';
        }else{
            return 0;
        }
    }

    public static String getCommunicationCharAlt(Communication c){
        if(c.getDirection() == Utils.Direction.SEND){
            return "send";
        }else if(c.getDirection() == Utils.Direction.SELECT){
            return "select_"+c.getLabel();
        }else if(c.getDirection() == Utils.Direction.RECEIVE){
            return "receive";
        }else if(c.getDirection() == Utils.Direction.BRANCH){
            return "branching_"+c.getLabel().replace("\"","");
        }else{
            return "";
        }
    }

    public static TypeGraph pattern2DFAalt(Session s){
        communicationStatesRepo = new HashMap<>();
        State startState = new State();
        ArrayList<Communication> toDo = new ArrayList<>(s.communicationsRoots());
        while (!toDo.isEmpty()) {
            for (Communication communicationsRoot : s.communicationsRoots()) {
                var state = traverseItAlt(communicationsRoot);
                if(state == null){
                    continue;
                }
                toDo.remove(communicationsRoot);
                if(state.isAccept()) {
                    startState.setAccept(true);
                }
                for (Transition transition : state.getTransitions()) {
                    startState.addTransition(transition);
                }
            }
        }
        var auto = new TypeGraph(labelMapping);
        auto.setInitialState(startState);
        auto.setDeterministic(false);
        return auto;
    }

    public static Automaton pattern2DFA(Session s){
        communicationStatesRepo = new HashMap<>();
        State startState = new State();
        ArrayList<Communication> toDo = new ArrayList<>(s.communicationsRoots());
        while (!toDo.isEmpty()) {
            for (Communication communicationsRoot : s.communicationsRoots()) {
                var state = traverseIt(communicationsRoot);
                if(state == null){
                    continue;
                }
                toDo.remove(communicationsRoot);
                if(state.isAccept()) {
                    startState.setAccept(true);
                }
                for (Transition transition : state.getTransitions()) {
                    startState.addTransition(transition);
                }
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
            if(communicationStatesRepo.containsKey(recursiveCallee)){
                s1.addTransition(new Transition(getCommunicationChar(c), communicationStatesRepo.get(recursiveCallee)));
            }else{
                return null;
            }
        }
        return s1;
    }

    private static State traverseItAlt(Communication c){
        var s1 = new State();
        communicationStatesRepo.put(c, s1);
        // recursion is not considered yet
        if(getCommunicationCharAlt(c).equals("")){
            s1.setAccept(true);
            return s1;
        }
        if(c.isFinal()){
            var s2 = new State();
            s2.setAccept(true);
            s1.addTransition(new Transition(stringToChar(getCommunicationCharAlt(c)), s2));
        }else{
            for (Communication nextCommunicationNode : c.getNextCommunicationNodes()) {
                var nextState = traverseItAlt(nextCommunicationNode);
                s1.addTransition(new Transition(stringToChar(getCommunicationCharAlt(c)), nextState));
            }
        }
        for (Communication recursiveCallee : c.getRecursiveCallees()) {
            if(allRecursiveCallsAreFinalStates){
                s1.setAccept(true);
            }
            if(communicationStatesRepo.containsKey(recursiveCallee)){
                s1.addTransition(new Transition(stringToChar(getCommunicationCharAlt(c)), communicationStatesRepo.get(recursiveCallee)));
            }else{
                return null;
            }
        }
        return s1;
    }
}
