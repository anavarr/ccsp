package mychor;

public record Message(Utils.Direction direction, String label) {

    @Override
    public boolean equals(Object o) {
        if(o instanceof Message m){
            if(!m.direction.equals(direction)) return false;
            if(m.label == null && label != null) return false;
            if(m.label != null && label == null) return false;
            if(m.label == null && label == null) return true;
            return m.label.equals(label);
        }
        return false;
    }
}

