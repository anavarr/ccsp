package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

import java.util.Collection;

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

    @Override
    protected LocalType getLeaf(RecurseCallType source) {
        return this;
    }
}
