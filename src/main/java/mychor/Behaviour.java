package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Behaviour {
    HashMap<String, Behaviour> nextBehaviours = new HashMap<>();
    String process;
    public Behaviour(String pr){
        process = pr;
    }
    public Behaviour(String pr, HashMap<String, Behaviour> nb){
        this(pr);
        nextBehaviours = nb;
    }
    public abstract boolean addBehaviour(Behaviour nb);


    abstract Behaviour duplicate();
    @Override
    public String toString() {
        if(nextBehaviours == null) return "";
        return String.join("\n",nextBehaviours.keySet().stream()
                .map(item -> nextBehaviours.get(item).toString().replace("\n","\n\t"))
                .toList()
        );
    }
}
