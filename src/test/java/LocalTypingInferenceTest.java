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
        public void testSelectTypeExtraction1branch(){
            var localChor = new Comm("pr", "q", Utils.Direction.SELECT, "continue");
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var nb = new HashMap<String, LocalType>();
            nb.put("continue", new EndType());
            var selectType = new SelectType(nb);
            assertEquals(extractedType, selectType);
        }
        @Test
        public void testBranchTypeExtraction1branch(){
            var localChor = new Comm("pr", "q", Utils.Direction.BRANCH, "continue");
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var nb = new HashMap<String, LocalType>();
            nb.put("continue", new EndType());
            var branchType = new BranchType(nb);
            assertEquals(extractedType, branchType);
        }
        @Test
        public void testBranchTypeExtraction2branches(){
            var nb = new HashMap<String, Behaviour>();
            nb.put("continue", new End("p"));
            nb.put("quit", new End("p"));

            var localChor = new Comm("pr", "q", nb);
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var nb1 = new HashMap<String, LocalType>();
            nb1.put("continue", new EndType());
            nb1.put("quit", new EndType());
            var branchType = new BranchType(nb1);
            assertEquals(extractedType, branchType);
        }
        @Test
        public void testCdtAllSelectTypeExtraction(){
            var localChorBranch1 = new Comm("pr", "q", Utils.Direction.SELECT, "continue");
            localChorBranch1.addBehaviour(new End("p"));
            var localChorBranch2 = new Comm("pr", "q", Utils.Direction.SELECT, "quit");
            localChorBranch2.addBehaviour(new End("p"));
            var nb = new HashMap<String, Behaviour>();
            nb.put("then", localChorBranch1);
            nb.put("else", localChorBranch2);
            var cdt = new Cdt("p",nb,"check(x)");

            var extractedType = LocalType.extractLocalType(cdt);
            var map = new HashMap<String, LocalType>();
            map.put("continue", new EndType());
            map.put("quit", new EndType());
            var selectType = new SelectType(map);
            assertEquals(extractedType, selectType);
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
