package mychor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

public class Cdt extends Behaviour {
    String expr;
    private List<String> visitedSelections = new ArrayList<String>();

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
        if(nb.size() > 2){
            throw new IllegalArgumentException("A conditional behaviour must have at most two continuations");
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
            s.append("\n").append("Then").append("\n\t")
                    .append(nextBehaviours.get("then").toString().replace("\n","\n\t"));
        }
        if(nextBehaviours.containsKey("else")){
            s.append("\n").append("Else").append("\n\t")
                    .append(nextBehaviours.get("else").toString().replace("\n","\n\t"));
        }
        return s.toString();
    }

    @Override
    public Behaviour reduce(HashMap<String, Behaviour> behaviours, MessageQueues queues) {
        if(nextBehaviours.containsKey("then") && !visitedSelections.contains("then")){
            visitedSelections.add("then");
            return nextBehaviours.get("then");
        }
        if(nextBehaviours.containsKey("else") && !visitedSelections.contains("else")){
            System.err.println("visit else");
            visitedSelections.add("else");
            return nextBehaviours.get("else");
        }
        return new End(process);
    }

    @Override
    Behaviour duplicate() {
        if(nextBehaviours.isEmpty()){
            return new Cdt(process, expr);
        }else{
            var nbThen = nextBehaviours.get("then");
            nbThen = nbThen != null ? nbThen.duplicate() : null;
            var nbElse = nextBehaviours.get("else");
            nbElse = nbElse != null ? nbElse.duplicate() : null;
            var hm = new HashMap<String, Behaviour>();
            if(nbThen != null) hm.put("then", nbThen);
            if(nbElse != null) hm.put("else", nbElse);
            return new Cdt(process, hm, expr);
        }
    }

    @Override
    public boolean equals(Object b) {
        if (!(b instanceof Cdt && ((Cdt) b).expr.equals(expr))) return false;
        return super.equals(b);
    }

    @Override
    public List<Behaviour> getBranches() {
        List<Behaviour> branches= new ArrayList<>();
        if(nextBehaviours.containsKey("then")){
            branches.addAll(nextBehaviours.get("then").getBranches());
        }
        if(nextBehaviours.containsKey("else")){
            branches.addAll(nextBehaviours.get("else").getBranches());
        }
        return branches;
    }
}
