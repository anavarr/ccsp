package mychor.types;

import java.util.HashMap;

public class SendType extends LocalType{
    public SendType(LocalType next){
        nextTypes.put(";", next);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SendType)) return false;
        return nextTypes.get(";").equals(((SendType) obj).nextTypes.get(";"));
    }

    @Override
    public String toString() {
        return "!;"+nextTypes.get(";").toString();
    }
}
