package mychor.types;

import mychor.MessageQueues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class EndType extends LocalType{
    public EndType(){
        this.nextTypes = new HashMap<>();
    }

    @Override
    public boolean equivalent(Object obj) {
        return obj instanceof EndType;
    }

    @Override
    public String toString() {
        return "end";
    }

    @Override
    public LocalType reduce(String pr, MessageQueues mqs) {
        return this;
    }

    @Override
    public LocalType duplicate() {
        return new EndType();
    }

    @Override
    protected LocalType extractParticipation(String process) {
        return new EndType();
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return true;
    }

    @Override
    public boolean visitedAllPaths() {
        return true;
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToReceiveRecursive() {
        return null;
    }

    @Override
    public HashMap<String, ArrayList<String>> getMsgsToSendRecursive() {
        return null;
    }

    @Override
    public PossibleMessages getMsgsToReceive(String name) {
        return null;
    }

    @Override
    public PossibleMessages getMsgsToSend(String name) {
        return null;
    }
}
