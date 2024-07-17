import mychor.Comm;
import mychor.Communication;
import mychor.PatternDetector;
import mychor.Session;
import mychor.Utils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mychor.Utils.Direction.RECEIVE;
import static mychor.Utils.Direction.SEND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternDetectionTest extends ProgramReaderTest{
    static Map<String, List<String>> threeBuyersCompatibilityList = new HashMap<>();
    static Map<String, List<String>> OAuth2FragmentCompatibilityList = new HashMap<>();

//    ====== SUPPORTS

    @Nested
    public class SendSupportTestSimple{

        @Test
        public void sendShouldSupportsSend(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.SEND));
            Session target = new Session("a", "b", new Communication(Utils.Direction.SEND));

            assertTrue(template.supports(target));
        }
        @Test
        public void sendShouldSupportsSelect(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.SEND));
            Session target = new Session("a", "b", new Communication(Utils.Direction.SELECT, "GET"));

            assertTrue(template.supports(target));
        }
        @Test
        public void selectShouldSupportsSend(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.SELECT, "GET"));
            Session target = new Session("a", "b", new Communication(Utils.Direction.SEND));

            assertTrue(template.supports(target));
        }
        @Test
        public void sendShouldNotSupportsReceive(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.SEND));
            Session target = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            assertFalse(template.supports(target));
        }
        @Test
        public void sendShouldNotSupportsBranch(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.SEND));
            Session target = new Session("a", "b", new Communication(Utils.Direction.BRANCH, "left"));
            assertFalse(template.supports(target));
        }
        @Test
        public void selectShouldNotSupportsReceive(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.SELECT, "GET"));
            Session target = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            assertFalse(template.supports(target));
        }
        @Test
        public void selectShouldNotSupportsBranch(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.SELECT, "left"));
            Session target = new Session("a", "b", new Communication(Utils.Direction.BRANCH, "left"));
            assertFalse(template.supports(target));
        }
    }

    @Nested
    public class ReceiveSupportTestSimple{

        @Test
        public void receiveShouldSupportsReceive(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            Session target = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            assertTrue(template.supports(target));
        }
        @Test
        public void receiveShouldSupportsBranch(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            Session target = new Session("a", "b", new Communication(Utils.Direction.BRANCH, "GET"));
            assertTrue(template.supports(target));
        }
        @Test
        public void branchShouldSupportsReceive(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.BRANCH, "GET"));
            Session target = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            assertTrue(template.supports(target));
        }
        @Test
        public void receiveShouldNotSupportsSend(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            Session target = new Session("a", "b", new Communication(Utils.Direction.SEND));
            assertFalse(template.supports(target));
        }
        @Test
        public void receiveShouldNotSupportsSelect(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.RECEIVE));
            Session target = new Session("a", "b", new Communication(Utils.Direction.SELECT, "left"));
            assertFalse(template.supports(target));
        }
        @Test
        public void branchShouldNotSupportsSend(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.BRANCH, "GET"));
            Session target = new Session("a", "b", new Communication(Utils.Direction.SEND));
            assertFalse(template.supports(target));
        }
        @Test
        public void branchShouldNotSupportsSelect(){
            Session template = new Session("a", "b", new Communication(Utils.Direction.BRANCH, "left"));
            Session target = new Session("a", "b", new Communication(Utils.Direction.SELECT, "left"));
            assertFalse(template.supports(target));
        }
    }

    @Nested
    public class TwoStepsCommunicationTest{
        @Test
        public void sendReceiveShouldSupportSendReceive(){
            var template = new Session("a","b", new Communication(Utils.Direction.SEND, new Communication(Utils.Direction.RECEIVE)));
            var target = new Session("a","b", new Communication(Utils.Direction.SEND, new Communication(Utils.Direction.RECEIVE)));
            assertTrue(template.supports(target));
        }
        @Test
        public void sendSendShouldSupportSendSend(){
            var template = new Session("a","b", new Communication(Utils.Direction.SEND, new Communication(Utils.Direction.SEND)));
            var target = new Session("a","b", new Communication(Utils.Direction.SEND, new Communication(Utils.Direction.SEND)));
            assertTrue(template.supports(target));
        }
        @Test
        public void receiveSendShouldSupportReceiveSend(){
            var template = new Session("a","b", new Communication(Utils.Direction.RECEIVE, new Communication(Utils.Direction.SEND)));
            var target = new Session("a","b", new Communication(Utils.Direction.RECEIVE, new Communication(Utils.Direction.SEND)));
            assertTrue(template.supports(target));
        }
        @Test
        public void receiveReceiveShouldSupportReceiveReceive(){
            var template = new Session("a","b", new Communication(Utils.Direction.RECEIVE, new Communication(Utils.Direction.RECEIVE)));
            var target = new Session("a","b", new Communication(Utils.Direction.RECEIVE, new Communication(Utils.Direction.RECEIVE)));
            assertTrue(template.supports(target));
        }
    }

    @Nested
    public class RecursiveCommunicationsTest{
        @Test
        public void recursiveSendShouldSupportRecursiveSend(){
            var send = new Communication(Utils.Direction.SEND);
            send.addLeafCommunicationRoots((new ArrayList<>(List.of(send))));
            var send2 = new Communication(Utils.Direction.SEND);
            send2.addLeafCommunicationRoots((new ArrayList<>(List.of(send2))));
            var template = new Session("a","b", send);
            var target = new Session("a","b", send2);
            assertTrue(template.supports(target));
        }

        @Test
        public void starSendReceiveStarShouldNotSupportsSendStarReceiveStar(){
            var send1 = new Communication(Utils.Direction.SEND);
            var recv1 = new Communication(Utils.Direction.RECEIVE);
            send1.addLeafCommunicationRoots(new ArrayList<>(List.of(recv1)));
            recv1.addLeafCommunicationRoots((new ArrayList<>(List.of(send1))));

            var send2 = new Communication(Utils.Direction.SEND);
            var recv2 = new Communication(Utils.Direction.RECEIVE);
            send2.addLeafCommunicationRoots((new ArrayList<>(List.of(recv2))));
            recv2.addLeafCommunicationRoots((new ArrayList<>(List.of(recv2))));
            var template = new Session("a","b", send1);
            var target = new Session("a","b", send2);
            assertFalse(template.supports(target));
        }

        @Test
        public void starSendStarShouldNotSupportStarSendStarVoid(){
            var send1 = new Communication(Utils.Direction.SEND);
            send1.addLeafCommunicationRoots(new ArrayList<>(List.of(send1, new Communication(Utils.Direction.VOID))));

            var send2 = new Communication(Utils.Direction.SEND);
            send2.addLeafCommunicationRoots(new ArrayList<>(List.of(send2)));
            assertTrue(send1.supports(send2));
            assertFalse(send2.supports(send1));
        }

        @Test
        public void grpcStStShouldSupportRecursivePingPong() throws IOException {
            var pd = new PatternDetector();
            var grpcStStServerPattern = pd.getPattern("GRPC_st_st_server");
            var grpcStStClientPattern = pd.getPattern("GRPC_st_st_client");
            var recursivePingPong = testFile("recursive_request_response.sp").compilerCtx;
            var serverSession = recursivePingPong.sessions.stream()
                    .filter(s -> s.peerA().equals("server")).toList().getFirst();
            var clientSession = recursivePingPong.sessions.stream()
                    .filter(s -> s.peerA().equals("client")).toList().getFirst();
            assertTrue(grpcStStServerPattern.supports(serverSession));
            assertTrue(grpcStStClientPattern.supports(clientSession));
        }

        @Test
        public void grpcStStShouldNotSupportServerFirstPingPong() throws IOException {
            var pd = new PatternDetector();
            var grpcStStServerPattern = pd.getPattern("GRPC_st_st_server");
            var grpcStStClientPattern = pd.getPattern("GRPC_st_st_client");
            var recursivePingPong = testFile("recursive_request_response.sp").compilerCtx;
            var serverSession = recursivePingPong.sessions.stream()
                    .filter(s -> s.peerA().equals("server")).toList().getFirst();
            var clientSession = recursivePingPong.sessions.stream()
                    .filter(s -> s.peerA().equals("client")).toList().getFirst();
            assertFalse(grpcStStServerPattern.supports(clientSession));
            assertFalse(grpcStStClientPattern.supports(serverSession));
        }

        @Test
        public void recursiveShouldSuppor4Instance() {
            var send1 = new Communication(Utils.Direction.SEND);
            var rcv1 = new Communication(Utils.Direction.RECEIVE);
            send1.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv1)));
            rcv1.addLeafCommunicationRoots(new ArrayList<>(List.of(send1, new Communication(Utils.Direction.VOID))));

            var send2 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE,
                                            new Communication(Utils.Direction.SEND,
                                                    new Communication(Utils.Direction.RECEIVE,
                                                            new Communication(Utils.Direction.SEND,
                                                                    new Communication(Utils.Direction.RECEIVE))))))));
            send2.addLeafCommunicationRoots(new ArrayList<>(List.of(send2)));
            assertTrue(send1.supports(send2));
            assertFalse(send2.supports(send1));
        }

        @Test
        public void recursiveShouldNotSupporFaulty5thInstance() {
            var send1 = new Communication(Utils.Direction.SEND);
            var rcv1 = new Communication(Utils.Direction.RECEIVE);
            send1.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv1)));
            rcv1.addLeafCommunicationRoots(new ArrayList<>(List.of(send1, new Communication(Utils.Direction.VOID))));

            var send2 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE,
                                            new Communication(Utils.Direction.SEND,
                                                    new Communication(Utils.Direction.RECEIVE,
                                                            new Communication(Utils.Direction.SEND,
                                                                    new Communication(Utils.Direction.SEND))))))));
            assertFalse(send1.supports(send2));
            assertFalse(send2.supports(send1));
        }

        @Test
        public void test2(){
            var send11 = new Communication(Utils.Direction.SEND);
            var rcv11 = new Communication(Utils.Direction.RECEIVE);
            var send12 = new Communication(Utils.Direction.SEND);
            send11.addLeafCommunicationRoots(new ArrayList<>(List.of(send11)));
            send11.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv11)));
            rcv11.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv11)));
            send11.addLeafCommunicationRoots(new ArrayList<>(List.of(send12)));
            send12.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv11)));

            var send21 = new Communication(SEND);
            var rcv21 = new Communication(RECEIVE);
            var rcv22 = new Communication(RECEIVE);
            var rcv23 = new Communication(RECEIVE);
            var send22 = new Communication(SEND);
            send21.addLeafCommunicationRoots(new ArrayList<>(List.of(send21)));
            send21.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv21)));
            send21.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv22)));
            send21.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv23)));
            rcv23.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv23)));
            rcv23.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv22)));
            rcv23.addLeafCommunicationRoots(new ArrayList<>(List.of(send21)));
            send21.addLeafCommunicationRoots(new ArrayList<>(List.of(send22)));
            send22.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv22)));

            assertTrue(send11.supports(send21));
        }

        @Test
        public void test() {
            var send1 = new Communication(Utils.Direction.SEND);
            var rcv1 = new Communication(Utils.Direction.RECEIVE);
            send1.addLeafCommunicationRoots(new ArrayList<>(List.of(rcv1)));
            rcv1.addLeafCommunicationRoots(new ArrayList<>(List.of(send1, new Communication(Utils.Direction.VOID))));

            var send2 = new Communication(Utils.Direction.SEND,
                    new Communication(Utils.Direction.RECEIVE,
                            new Communication(Utils.Direction.SEND,
                                    new Communication(Utils.Direction.RECEIVE,
                                            new Communication(Utils.Direction.SEND,
                                                    new Communication(Utils.Direction.RECEIVE))))));
            send2.addLeafCommunicationRoots(new ArrayList<>(List.of(send2)));
            assertTrue(send1.supports(send2));
            assertFalse(send2.supports(send1));
        }
    }


    static{
        threeBuyersCompatibilityList.put("alice-store", List.of("GRPC_st_st_client", "ReactiveStreams_client"));
        threeBuyersCompatibilityList.put("store-alice", List.of("GRPC_st_st_server", "ReactiveStreams_server"));
        threeBuyersCompatibilityList.put("alice-bob", List.of("GRPC_st_st_client"));
        threeBuyersCompatibilityList.put("bob-alice", List.of("GRPC_st_st_server"));

        OAuth2FragmentCompatibilityList.put("service-client", List.of("GRPC_un_st_client", "GRPC_st_st_client"));
        OAuth2FragmentCompatibilityList.put("client-service", List.of("GRPC_un_st_server", "GRPC_st_st_server"));
        OAuth2FragmentCompatibilityList.put("service-authenticator", List.of("GRPC_st_st_server"));
        OAuth2FragmentCompatibilityList.put("authenticator-service", List.of("GRPC_st_st_client"));
        OAuth2FragmentCompatibilityList.put("client-authenticator", List.of("GRPC_st_st_client"));
        OAuth2FragmentCompatibilityList.put("authenticator-client", List.of("GRPC_st_st_server"));
    }

    @Test
    public void checkThreeBuyersCompatibilityList() throws IOException{
        checkProtocolCompatibilityList("Three_buyer_protocol.sp", threeBuyersCompatibilityList);
    }

    @Test
    public void checkOAuth2FragmentCompatibilityList() throws IOException{
        checkProtocolCompatibilityList("OAuth2_fragment.sp", OAuth2FragmentCompatibilityList);
    }

    public void checkProtocolCompatibilityList(String protocol, Map<String, List<String>> compatibilityList) throws IOException{
        var ctx = testFile(protocol).compilerCtx;
        PatternDetector detector = new PatternDetector(ctx);
        var cf = detector.detectCompatibleFrameworks();
        compatibilityList.forEach((ends, compliantFrameworks) -> {
            var peerA = ends.split("-")[0];
            var peerB = ends.split("-")[1];
            for (String compliantFramework : compliantFrameworks) {
                assertTrue(frameworkIsCompatibleForSessionPeers(ctx.sessions, peerA, peerB, compliantFramework, cf),
                        compliantFramework+" is not compliant with the session between "+peerA+" and "+peerB);
            }
            //we don't want any other framework to be compatible
            var cffe = getCompatibleFrameworksForEnds(ctx.sessions, peerA, peerB, cf);
            assertEquals(cffe.size(), compliantFrameworks.size(),
                    "the session between "+peerA+" and "+peerB+" must be compliant with "
                            +compliantFrameworks+" only but it is compliant with "+cffe);
        });

    }

    boolean frameworkIsCompatibleForSessionPeers(List<Session> sessions, String peerA, String peerB,
                                                 String framework, Map<Session, List<String>> frameworks){
        var sessionOptional = sessions.stream().filter(s -> s.peerA().equals(peerA) && s.peerB().equals(peerB))
                .findFirst();
        if(sessionOptional.isEmpty()) return false;
        var session = sessionOptional.get();
        return (frameworks.get(session).contains(framework));
    }

    List<String> getCompatibleFrameworksForEnds(List<Session> sessions, String peerA, String peerB, Map<Session, List<String>> frameworks){
        var sessionOptional = sessions.stream().filter(s -> s.peerA().equals(peerA) && s.peerB().equals(peerB))
                .findFirst();
        if(sessionOptional.isEmpty()) return new ArrayList<>();
        var session = sessionOptional.get();
        return frameworks.get(session);
    }
}
