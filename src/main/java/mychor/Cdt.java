package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cdt extends Behaviour {
    String expr;

    public Cdt(String pr) {
        super(pr);
    }

    @Override
    public boolean addBehaviour(Behaviour nb) {
        if(nextBehaviours.isEmpty()){
            nextBehaviours.put("then", nb);
            return true;
        }
        return nextBehaviours.get("then").addBehaviour(nb);
    }

    public Cdt(String pr, String expr) {
        super(pr);
        this.expr = expr;
    }

    public Cdt(String pr, HashMap<String, Behaviour> nb) {
        super(pr, nb);
        if(nb.size() != 2){
            throw new IllegalArgumentException("A conditional behaviour must have two continuations");
        }
    }

    public Cdt(String pr, HashMap<String, Behaviour> nb, String expr) {
        this(pr, nb);
        this.expr = expr;
    }

    @Override
    public String toString() {
        var s = new StringBuilder("Cdt ").append(expr).append(":");
        if(nextBehaviours.containsKey("then")){
            s.append("\n").append("Then").append("\n\t").append(nextBehaviours.get("then").toString().replace("\n","\n\t"));
        }
        if(nextBehaviours.containsKey("else")){
            s.append("\n").append("Else").append("\n\t").append(nextBehaviours.get("else").toString().replace("\n","\n\t"));
        }
        return s.toString();
    }

    @Override
    Behaviour duplicate() {
        if(nextBehaviours.isEmpty()){
            return new Cdt(process, expr);
        }else{
            var nbThen = nextBehaviours.get("then").duplicate();
            var nbElse = nextBehaviours.get("else").duplicate();
            var hm = new HashMap<String, Behaviour>();
            hm.put("then", nbThen);
            hm.put("else", nbElse);
            return new Cdt(process, hm, expr);
        }
    }
}
