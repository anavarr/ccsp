package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SendType extends LocalType{
    String destination;
    public SendType(String destination, LocalType next){
        this.destination = destination;
        nextTypes.put(";", next);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SendType)) return false;
        return nextTypes.get(";").equals(((SendType) obj).nextTypes.get(";"));
    }

    @Override
    public String toString() {
        return destination+"!;"+nextTypes.get(";").toString();
    }

    @Override
    public LocalType reduce(String pr, MessageQueues mqs) {
        mqs.add(Utils.Direction.SEND, pr, destination, null);
        return nextTypes.get(";");
    }

    @Override
    protected LocalType getLeaf() {
        return this.nextTypes.get(";").getLeaf();
    }

    @Override
    public List<String> getInvolvedProcesses() {
        ArrayList<String> l = new ArrayList<>();
        l.add(destination);
        l.addAll(nextTypes.get(";").getInvolvedProcesses());
        return l;
    }

    @Override
    public boolean visitedAllPaths() {
        return nextTypes.get(";").visitedAllPaths();
    }

    @Override
    public LocalType duplicate() {
        return new SendType(destination, nextTypes.get(";"));
    }

    @Override
    protected LocalType extractParticipation(String process) {
        if(destination.equals(process)) return new SendType(destination, nextTypes.get(";").extractParticipation(process));
        return nextTypes.get(";").extractParticipation(process);
    }

    @Override
    public Boolean knowlegdgeOfChoice(Collection<String> processes) {
        return nextTypes.get(";").knowlegdgeOfChoice(processes);
    }

    @Override
    protected LocalType softReset() {
        nextTypes.get(";").softReset();
        return this;
    }

}
