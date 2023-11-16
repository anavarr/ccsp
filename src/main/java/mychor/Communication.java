package mychor;

import static mychor.Utils.Arity.INFINITE;
import static mychor.Utils.Arity.MULTIPLE;
import static mychor.Utils.Arity.SINGLE;

public record Communication(Utils.Direction direction, Utils.Arity arity) {
    public boolean isSend(){
        return direction == Utils.Direction.SEND;
    }

    public boolean isReceive(){
        return direction == Utils.Direction.RECEIVE;
    }

    public boolean isSelect(){
        return direction == Utils.Direction.SELECT;
    }

    public boolean isBranch(){
        return direction == Utils.Direction.BRANCH;
    }

    public boolean isSingle(){
        return arity == SINGLE;
    }

    public boolean isMultiple(){
        return arity == MULTIPLE;
    }

    public boolean isInfinite(){
        return arity == INFINITE;
    }

}
