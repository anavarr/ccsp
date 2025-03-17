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

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocalTypingInferenceTest extends ProgramReaderTest {

    @Nested
    public class SimpleTests{
        @Test
        public void testEndTypeExtraction() throws Exception {
            var localChor = new End("p");
            var extractedType = LocalType.extractLocalType(localChor);
            var endType = new EndType();
            System.out.println(extractedType);
            assertEquals(extractedType, endType);
        }
        @Test
        public void testNoneTypeExtraction() throws Exception {
            var localChor = new None("p");
            var extractedType = LocalType.extractLocalType(localChor);
            var endType = new EndType();
            System.out.println(extractedType);
            assertEquals(extractedType, endType);
        }

        @Test
        public void testSendTypeExtraction() throws Exception {
            var localChor = new Comm("p", "q", Utils.Direction.SEND, "");
            var extractedType = LocalType.extractLocalType(localChor);
            var sendType = new SendType("q", new EndType());
            System.out.println(extractedType);
            assertEquals(extractedType, sendType);
        }
        @Test
        public void testReceiveTypeExtraction() throws Exception {
            var localChor = new Comm("p", "q", Utils.Direction.RECEIVE, "");
            var extractedType = LocalType.extractLocalType(localChor);
            var receiveType = new ReceiveType("q", new EndType());
            System.out.println(extractedType);
            assertEquals(extractedType, receiveType);
        }
        @Test
        public void testSelectTypeExtraction1branch() throws Exception {
            var localChor = new Comm("p", "q", Utils.Direction.SELECT, "continue");
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var nb = new HashMap<String, LocalType>();
            nb.put("continue", new EndType());
            var selectType = new SelectType("q", nb);
            System.out.println(extractedType);
            assertEquals(extractedType, selectType);
        }
        @Test
        public void testBranchTypeExtraction1branch() throws Exception {
            var localChor = new Comm("p", "q", Utils.Direction.BRANCH, "continue");
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var nb = new HashMap<String, LocalType>();
            nb.put("continue", new EndType());
            var branchType = new BranchType("q", nb);
            System.out.println(extractedType);
            assertEquals(extractedType, branchType);
        }
        @Test
        public void testBranchTypeExtraction2branches() throws Exception {
            var nb = new HashMap<String, Behaviour>();
            nb.put("continue", new End("p"));
            nb.put("quit", new End("p"));

            var localChor = new Comm("p", "q", nb);
            localChor.addBehaviour(new End("p"));
            var extractedType = LocalType.extractLocalType(localChor);
            var nb1 = new HashMap<String, LocalType>();
            nb1.put("continue", new EndType());
            nb1.put("quit", new EndType());
            var branchType = new BranchType("q", nb1);
            System.out.println(extractedType);
            assertEquals(extractedType, branchType);
        }
        @Test
        public void testCdtAllSelectTypeExtraction() throws Exception {
            var localChorBranch1 = new Comm("p", "q", Utils.Direction.SELECT, "continue");
            localChorBranch1.addBehaviour(new End("p"));
            var localChorBranch2 = new Comm("p", "q", Utils.Direction.SELECT, "quit");
            localChorBranch2.addBehaviour(new End("p"));
            var nb = new HashMap<String, Behaviour>();
            nb.put("then", localChorBranch1);
            nb.put("else", localChorBranch2);
            var cdt = new Cdt("p",nb,"check(x)");

            var extractedType = LocalType.extractLocalType(cdt);
            var map = new HashMap<String, LocalType>();
            map.put("continue", new EndType());
            map.put("quit", new EndType());
            var selectType = new SelectType("q", map);
            System.out.println(extractedType);
            assertEquals(extractedType, selectType);
        }


        @Test
        public void testSimpleCallTypeExtraction() throws Exception {
            var localChor = new Call("p", "X");
            var extractedType = LocalType.extractLocalType(localChor);
            var callType = new RecurseCallType("X");
            System.out.println(extractedType);
            assertEquals(extractedType, callType);
        }
        @Test
        public void testRecursiveDefTypeExtraction() throws Exception {
            var localChor = new Call("p","X");
            localChor.addBehaviour(new Call("p", "X"));
            var extractedType = LocalType.extractLocalType(localChor);
            var recDefType = new RecurseDefType("X", new RecurseCallType("X"));
            System.out.println(extractedType);
            assertEquals(extractedType, recDefType);
        }
    }

    @Nested
    public class ComplexTests{
        @Test
        public void differentBranchesConditional() throws IOException {
            var bev = testFile("behavioursCombinations/branching_cdt_msg.sp").compilerCtx.behaviours.get("client");
            assertThrows(Exception.class, () -> LocalType.extractLocalType(bev));
        }
        @Test
        public void sameBranchesConditional() throws Exception {
            var bev = testFile("behavioursCombinations/branching_cdt_msg_symmetric.sp").compilerCtx.behaviours.get("client");
            var lt = LocalType.extractLocalType(bev);
            System.out.println(lt);
        }

        @Test
        public void doubleSelect() throws Exception {
            var bev = testFile("double_selection.sp").compilerCtx.behaviours.get("p");
            var lt = LocalType.extractLocalType(bev);
            System.out.println(lt);
        }
    }
}
