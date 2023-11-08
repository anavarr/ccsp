package mychor;

import java.util.List;

public class Utils {
    public enum Direction{
        SEND,
        RECEIVE
    }

    public static <V>List<V> addL2ToL1(List<V> l1, List<V> l2){
        l1.addAll(l2);
        return l1;
    }

    public enum Arity{
        SINGLE,
        MULTIPLE,
        INFINITE
    }
}
