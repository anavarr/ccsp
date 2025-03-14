import mychor.Behaviour;
import mychor.Call;
import mychor.Cdt;
import mychor.Comm;
import mychor.End;
import mychor.Utils;
import mychor.types.EndType;
import mychor.types.LocalType;
import mychor.types.SendType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalTypingInferenceTest {

    @Test
    public void test(){
        var send = new Comm("p", "q", Utils.Direction.SEND, "");
        var sendTypeExtracted = LocalType.extractLocalType(send);
        var sendType = new SendType(new EndType());
        System.out.println(sendTypeExtracted);
        System.out.println(sendType);
        assertEquals(sendTypeExtracted, sendType);
    }
}
