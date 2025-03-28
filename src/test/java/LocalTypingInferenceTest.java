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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            var callType = new RecurseCallType("X",null);
            System.out.println(extractedType);
            assertEquals(extractedType, callType);
        }
        @Test
        public void testRecursiveDefTypeExtraction() throws Exception {
            var localChor = new Call("p","X");
            localChor.addBehaviour(new Call("p", "X"));
            var extractedType = LocalType.extractLocalType(localChor);
            var recDefType = new RecurseDefType("X", new RecurseCallType("X", null));
            System.out.println(extractedType);
            assertEquals(extractedType, recDefType);
        }
    }

    @Nested
    public class ComplexTests{
        @Test
        public void emptyConditionalShouldReturnEndType() throws IOException{
            var bev = testFile("empty_cdt.sp").compilerCtx.behaviours;
            try {
                assertEquals(new RecurseDefType("Client", new EndType()),
                        LocalType.extractLocalType(bev.get("client")) );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
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
        public void doubleSelectGoodOrder() throws Exception {
            var bev = testFile("double_selection.sp").compilerCtx.behaviours.get("p");
            var lt = LocalType.extractLocalType(bev);
            System.out.println(lt);
        }
        @Test
        public void doubleSelectWrongOrder() throws Exception {
            var bev = testFile("double_selection_wrong_order.sp").compilerCtx.behaviours.get("p");
            assertThrows(
                    Exception.class,
                    () -> LocalType.extractLocalType(bev)
            );
        }
        @Test
        public void selectDifferentDestinations() throws Exception{
            var bev = testFile("cdt_select_different_destinations.sp").compilerCtx.behaviours.get("p");
            assertThrows(
                    Exception.class,
                    () ->LocalType.extractLocalType(bev)
            );
        }
        @Test
        public void intricateBranchingTest() throws Exception{
            var lt_client = readType("local_types_client.lt");
            var lt_server = readType("local_type_server.lt");
            var lt_service = new RecurseDefType("Service", new EndType());
            var bevs = testFile("branching_paths/intricate_branching.sp").compilerCtx.behaviours;
            assertEquals(lt_client, LocalType.extractLocalType(bevs.get("client")));
            assertEquals(lt_server, LocalType.extractLocalType(bevs.get("server")));
            assertEquals(lt_service, LocalType.extractLocalType(bevs.get("service")));
        }

        @Test
        public void inferOAuth2_fragmentTypes() throws Exception{
            var lt_authenticator = readType("OAuth2_fragment_authenticator_type.lt");
            var lt_service = readType("OAuth2_fragment_service_type.lt");
            var lt_client = readType("OAuth2_fragment_client_type.lt");
            var bevs = testFile("OAuth2_fragment.sp").compilerCtx.behaviours;
            assertEquals(lt_authenticator, LocalType.extractLocalType(bevs.get("authenticator")));
            assertEquals(lt_client, LocalType.extractLocalType(bevs.get("client")));
            assertEquals(lt_service, LocalType.extractLocalType(bevs.get("service")));
        }

        @Test
        public void inferThreeBuyerTypes() throws Exception{
            var lt_alice = readType("ThreeBuyer_alice.lt");
            var lt_bob = readType("ThreeBuyer_bob.lt");
            var lt_store = readType("ThreeBuyer_store.lt");
            var bevs = testFile("Three_buyer_protocol.sp").compilerCtx.behaviours;
            assertEquals(lt_alice, LocalType.extractLocalType(bevs.get("alice")));
            assertEquals(lt_bob, LocalType.extractLocalType(bevs.get("bob")));
            assertEquals(lt_store, LocalType.extractLocalType(bevs.get("store")));
        }

        @Test
        public void asymmetricConditionalShouldBeTypable() throws Exception{
            var bevs = testFile("branching_paths/asymmetric_branching.sp").compilerCtx.behaviours;
            var extractedClientType = LocalType.extractLocalType(bevs.get("client"));
            var clientType = readType("asymmetric_cdt_client.lt");
            assertEquals(clientType, extractedClientType);
            assertEquals(LocalType.extractLocalType(bevs.get("server")), readType("asymmetric_cdt_server.lt"));
            assertEquals(LocalType.extractLocalType(bevs.get("service")), readType("asymmetric_cdt_service.lt"));
        }

        @Test
        public void testIntricateBranchingComb7() throws Exception{
            var bevs = testFile("branching_paths/intricate_branching_comb_7.sp").compilerCtx.behaviours;
            assertDoesNotThrow(() -> LocalType.extractLocalType(bevs.get("client")));
            assertDoesNotThrow(() -> LocalType.extractLocalType(bevs.get("server")));
            assertDoesNotThrow(() -> LocalType.extractLocalType(bevs.get("service")));
        }

        @Test
        public void nestedRecursionShouldBeInferable() throws Exception{
            var bevs = testFile("inner_outer_recursion_test.sp").compilerCtx.behaviours;
            assertDoesNotThrow(() -> LocalType.extractLocalType(bevs.get("p")));
        }
    }
}
