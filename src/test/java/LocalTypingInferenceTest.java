import mychor.Behaviour;
import mychor.Call;
import mychor.Cdt;
import mychor.Comm;
import mychor.End;
import mychor.None;
import mychor.Utils;
import mychor.types.BranchType;
import mychor.types.EndType;
import mychor.types.LocalType;
import mychor.types.ReceiveType;
import mychor.types.RecurseCallType;
import mychor.types.RecurseDefType;
import mychor.types.SelectType;
import mychor.types.SendType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LocalTypingInferenceTest {

    @Nested
    public class SimpleTests{
        @Test
        public void testEndTypeExtraction(){
            var localChor = new End("p");
            var extractedType = LocalType.extractLocalType(localChor);
            var endType = new EndType();
            assertEquals(extractedType, endType);
        }
        @Test
        public void testNoneTypeExtraction(){
            var localChor = new None("p");
            var extractedType = LocalType.extractLocalType(localChor);
            var endType = new EndType();
            assertEquals(extractedType, endType);
        }

        @Test
        public void testSendTypeExtraction(){
            var localChor = new Comm("p", "q", Utils.Direction.SEND, "");
            var extractedType = LocalType.extractLocalType(localChor);
            var sendType = new SendType(new EndType());
            assertEquals(extractedType, sendType);
        }
        @Test
        public void testReceiveTypeExtraction(){
            var localChor = new Comm("p", "q", Utils.Direction.RECEIVE, "");
            var extractedType = LocalType.extractLocalType(localChor);
            var receiveType = new ReceiveType(new EndType());
            assertEquals(extractedType, receiveType);
        }
        @Test
        public void testSelectTypeExtraction(){
            var localChor = new Comm("pr", "q", Utils.Direction.SELECT, "continue");
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var nb = new HashMap<String, LocalType>();
            nb.put("continue", new EndType());
            var selectType = new SelectType(nb);
            assertEquals(extractedType, selectType);
        }
        @Test
        public void testBranchTypeExtraction(){
            var localChor = new Comm("pr", "q", Utils.Direction.BRANCH, "continue");
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var nb = new HashMap<String, LocalType>();
            nb.put("continue", new EndType());
            var branchType = new BranchType(nb);
            assertEquals(extractedType, branchType);
        }


        @Test
        public void testRecursiveCallTypeExtraction(){
            var localChor = new Call("p", "X");
            var extractedType = LocalType.extractLocalType(localChor);
            var callType = new RecurseCallType("X");
            assertEquals(extractedType, callType);
        }
        @Test
        public void testRecursiveDefTypeExtraction(){
            var localChor = new Call("p","X");
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var recDefType = new RecurseDefType("X", new EndType());
            assertEquals(extractedType, recDefType);
        }
    }
}
