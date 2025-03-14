package mychor.types;

import mychor.End;

import java.util.HashMap;

public class EndType extends LocalType{
    public EndType(){
        this.nextTypes = new HashMap<>();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EndType;
    }
}
