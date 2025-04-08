package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

import java.util.Collection;

public class ReceiveType extends LocalType{
    String destination;
    public ReceiveType(String destination, LocalType next){
        this.destination = destination;
        nextTypes.put(";", next);
    }

    @Override
    public boolean visitedAllPaths() {
        return nextTypes.get(";").visitedAllPaths();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ReceiveType rt)) return false;
        if(!this.destination.equals(rt.destination)) return false;
        return nextTypes.get(";").equals(((ReceiveType) obj).nextTypes.get(";"));
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        var msg = mqs.poll(destination, process);
        // maybe the message didn't arrive yet
        if(msg == null) return this;
        // the sender sent a label it was not supposed to send beforehand
        // it is not valid
        if(!msg.direction().equals(Utils.Direction.SEND)) throw new RuntimeException(
                        "A receive action is expected, the queue "+process+"-"+destination+
                                " contains a labeled selection, type is not valid"
       );
        // we got the right message, this communication can reduce
        return nextTypes.get(";");
    }

    @Override
    public LocalType reduceOnce(String process, MessageQueues mqs) {
        return reduce(process, mqs);
    }

    @Override
    public LocalType reduceNoRec(String process, MessageQueues mqs) {
        return reduce(process, mqs);
    }

    @Override
    public void hardReset() {
        nextTypes.get(";").hardReset();
    }

    @Override
    public LocalType duplicate() {
        return new ReceiveType(destination, nextTypes.get(";"));
    }

    @Override
    protected LocalType extractParticipation(String process) {
        if(destination.equals(process)) return new ReceiveType(destination, nextTypes.get(";").extractParticipation(process));
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
    public String toString() {
        return destination+"?;"+nextTypes.get(";").toString();
    }

    public String getDestination() {
        return destination;
    }
}
