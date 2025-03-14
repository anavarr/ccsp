package mychor.types;

public class ReceiveType extends LocalType{
    public ReceiveType(LocalType next){
        nextTypes.put(";", next);
    }
}
