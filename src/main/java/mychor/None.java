package mychor;

import java.util.HashMap;

public class None extends Behaviour{
    public None(String pr) {
        super(pr);
    }

    public None(String pr, HashMap<String, Behaviour> nb) {
        super(pr, nb);
    }

    @Override
    public boolean addBehaviour(Behaviour nb) {
        return false;
    }

    @Override
    Behaviour duplicate() {
        return new None(process);
    }

    @Override
    public String toString() {
        return "None";
    }
}
