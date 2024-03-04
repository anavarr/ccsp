package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class Comm extends Behaviour {
    Utils.Arity arity;
    Utils.Direction direction;
    List<String> labels = new ArrayList<>();
    String destination;
    String exploredLabel;
    boolean hasBeenExplored = false;


    public Comm(String pr, String dest, Utils.Direction direction, Utils.Arity arity, List<String> labels){
        super(pr);
        this.destination= dest;
        this.direction = direction;
        this.arity = arity;
        this.labels.addAll(labels);
    }

    public Comm(String pr, String dest, Utils.Direction direction, Utils.Arity arity, String label){
        super(pr);
        this.destination= dest;
        this.direction = direction;
        this.arity = arity;
        if(label != null) labels.add(label);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(direction.toString()).append(" ").append(destination);
        for (String key : nextBehaviours.keySet()) {
            if(key.contains(";")) {
                s.append(key).append("\n").append(nextBehaviours.get(key)
                        .toString());
            }else{
                s.append("\n\t").append(key).append(":").append("\n\t\t").append(nextBehaviours.get(key)
                        .toString().replace("\n","\n\t\t"));
            }
        }
        return s.toString();
    }

    @Override
    public Behaviour reduce(HashMap<String, Behaviour> behaviours, MessageQueues qs) throws RuntimeException{
        if(!behaviours.containsKey(destination)){
            return this;
        }
        if(direction == Utils.Direction.SEND){
            qs.add(Utils.Direction.SEND, process, destination, null);
            return nextBehaviours.get(";");
        }else if(direction == Utils.Direction.RECEIVE){
            var m = qs.poll(destination, process);
            if(m == null) return this;
            if(m.direction() != Utils.Direction.SEND) return this;
            return nextBehaviours.get(";");
        }else if(direction == Utils.Direction.SELECT){
            var label = labels.get(0);
            qs.add(Utils.Direction.SELECT, process, destination, label);
            return nextBehaviours.get(label+";");
        }else if(direction == Utils.Direction.BRANCH){
            var m = qs.poll(destination, process);
            if(m == null) return this;
            if(m.direction() != Utils.Direction.SELECT) return this;
            if(!nextBehaviours.containsKey(m.label())) throw new RuntimeException(
                    String.format(
                            "Selection process %s requires a label %s not possessed by the branching operator in %s",
                            destination,
                            m.label(),
                            process));
            return nextBehaviours.get(m.label());
        }else{
            return this;
        }
    }
    @Override
    public boolean addBehaviour(Behaviour nb) {
        if (nextBehaviours.isEmpty()) {
            if(direction != Utils.Direction.BRANCH && direction != Utils.Direction.SELECT){
                nextBehaviours.put(";", nb);
            }else if (direction == Utils.Direction.SELECT) {
                nextBehaviours.put(labels.get(0)+";", nb);
            }else{
                nextBehaviours.put(labels.get(0), nb);
            }
            return true;
        }
        if(direction != Utils.Direction.BRANCH && direction != Utils.Direction.SELECT){
            return nextBehaviours.get(";").addBehaviour(nb);
        }else if (direction == Utils.Direction.SELECT){
            return nextBehaviours.get(labels.get(0)+";").addBehaviour(nb);
        }else{
            if(labels.size() == 1){
                nextBehaviours.get(labels.get(0)).addBehaviour(nb);
            }
        }
        return false;
    }

    @Override
    Behaviour duplicate() {
        var c = new Comm(process, destination, direction, arity, labels);
        if (!nextBehaviours.isEmpty()){
            if(direction != Utils.Direction.BRANCH && direction != Utils.Direction.SELECT) {
                c.nextBehaviours.put(";", nextBehaviours.get(";").duplicate());
            }else if(direction == Utils.Direction.SELECT){
                c.nextBehaviours.put(c.labels.get(0)+";", nextBehaviours.get(labels.get(0)+";").duplicate());
            }else{
                for (String s : nextBehaviours.keySet()) {
                    c.nextBehaviours.put(s, nextBehaviours.get(s).duplicate());
                }
            }
        }
        return c;
    }

    @Override
    public boolean equals(Object b) {
        if(!(b instanceof Comm comm)) return false;
        if(!(comm.direction.equals(direction) &&
                comm.labels.equals(labels) &&
                comm.destination.equals(destination) &&
                comm.arity == arity)) return false;
        return super.equals(b);
    }

    @Override
    public boolean isFinal() {
        if(direction != Utils.Direction.BRANCH){
            return false;
        }else{
            for (String s : nextBehaviours.keySet()) {
                if(!nextBehaviours.get(s).isFinal()) return false;
            }
            return true;
        }
    }
}
