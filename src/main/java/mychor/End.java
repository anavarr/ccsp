package mychor;

import java.util.HashMap;
import java.util.List;

public class End extends Behaviour{
    public End(String pr) {
        super(pr);
    }

    @Override
    public boolean addBehaviour(Behaviour nb) {
        return false;
    }

    @Override
    public Behaviour duplicate() {
        return new End(process);
    }

    @Override
    public String toString() {
        return "End";
    }

    @Override
    public Behaviour reduce(HashMap<String, Behaviour> behaviours, MessageQueues queues) {
        return this;
    }

    @Override
    public boolean equals(Object b1) {
        return b1 instanceof End;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public List<Behaviour> getBranches() {
        return List.of(this);
    }
}
