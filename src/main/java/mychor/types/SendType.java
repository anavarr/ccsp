package mychor.types;

import java.util.HashMap;

public class SendType extends LocalType{
    public SendType(LocalType next){
        nextTypes.put(";", next);
    }
}
