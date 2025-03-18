package mychor.types;

import mychor.MessageQueues;
import mychor.Utils;

public class ReceiveType extends LocalType{
    String destination;
    public ReceiveType(String destination, LocalType next){
        this.destination = destination;
        nextTypes.put(";", next);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ReceiveType rt)) return false;
        if(!this.destination.equals(rt.destination)) return false;
        return nextTypes.get(";").equals(((ReceiveType) obj).nextTypes.get(";"));
    }

    @Override
    public LocalType reduce(String process, MessageQueues mqs) {
        var msg = mqs.peek(destination, process);
        // maybe the message didn't arrive yet
        if(msg == null) return this;
        // the sender sent a label it was not supposed to send beforehand
        // it is not valid
        if(!msg.direction().equals(Utils.Direction.SEND)) throw new RuntimeException(
                        "A receive action is expected, the queue contains a labeled selection, type is not valid"
       );
        // we got the right message, this communication can reduce
        msg = mqs.poll(destination, process);
        return nextTypes.get(";");
    }

    @Override
    public String toString() {
        return "?;"+nextTypes.get(";").toString();
    }
}
