package mychor;

import java.util.HashMap;

public class End extends Behaviour{
    public End(String pr) {
        super(pr);
    }

    public End(String pr, HashMap<String, Behaviour> nb) {
        super(pr, nb);
    }

    @Override
    public boolean addBehaviour(Behaviour nb) {
        return false;
    }

    @Override
    Behaviour duplicate() {
        return new End(process);
    }

    @Override
    public String toString() {
        return "End";
    }
}
