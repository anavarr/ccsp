package mychor;

import java.util.HashMap;
import java.util.List;

public class None extends Behaviour{
    public None(String pr) {
        super(pr);
    }

    @Override
    public boolean addBehaviour(Behaviour nb) {
        return false;
    }

    @Override
    public Behaviour duplicate() {
        return new None(process);
    }

    @Override
    public String toString() {
        return "None";
    }

    @Override
    public Behaviour reduce(HashMap<String, Behaviour> behaviours, MessageQueues queues) {
        return new End(process);
    }

    @Override
    public boolean equals(Object b1) {
        return b1 instanceof None;
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public List<Behaviour> getBranches() {
        return List.of(this);
    }
}
