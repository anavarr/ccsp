package mychor.types;

public class ReceiveType extends LocalType{
    public ReceiveType(LocalType next){
        nextTypes.put(";", next);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ReceiveType)) return false;
        return nextTypes.get(";").equals(((ReceiveType) obj).nextTypes.get(";"));
    }
}
