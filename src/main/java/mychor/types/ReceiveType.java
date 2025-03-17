package mychor.types;

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
    public String toString() {
        return "?;"+nextTypes.get(";").toString();
    }
}
