package mychor.types;

import java.util.HashMap;

public class RecurseCallType extends LocalType{
    String name;
    public RecurseCallType(String name){
        this.name = name;
        this.nextTypes = new HashMap<>();
    }
}
