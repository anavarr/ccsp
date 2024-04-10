import mychor.PatternDetector;
import mychor.Session;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternDetectionTest extends ProgramReaderTest{
    static Map<String, List<String>> threeBuyersCompatibilityList = new HashMap<>();
    static Map<String, List<String>> OAuth2FragmentCompatibilityList = new HashMap<>();

    static{
        threeBuyersCompatibilityList.put("alice-store", List.of("GRPC_st_st_client", "ReactiveStreams_client"));
        threeBuyersCompatibilityList.put("store-alice", List.of("GRPC_st_st_server", "ReactiveStreams_server"));
        threeBuyersCompatibilityList.put("alice-bob", List.of("GRPC_un_st_client", "GRPC_st_st_client"));
        threeBuyersCompatibilityList.put("bob-alice", List.of("GRPC_un_st_server", "GRPC_st_st_server"));

    }

    @Test
    public void checkThreeBuyersCompatibilityList() throws IOException{
        checkProtocolCompatibilityList("Three_buyer_protocol.sp");
    }

    @Test
    public void checkOAuth2FragmentCompatibilityList() throws IOException{
        checkProtocolCompatibilityList("OAuth2_fragment.sp");
    }

    public void checkProtocolCompatibilityList(String protocol) throws IOException{
        var ctx = testFile(protocol).compilerCtx;
        PatternDetector detector = new PatternDetector(ctx);
        var cf = detector.detectCompatibleFrameworks();
        threeBuyersCompatibilityList.forEach((ends, compliantFrameworks) -> {
            var peerA = ends.split("-")[0];
            var peerB = ends.split("-")[1];
            for (String compliantFramework : compliantFrameworks) {
                assertTrue(frameworkIsCompatibleForSessionPeers(ctx.sessions, peerA, peerB, compliantFramework, cf));
            }
            //we don't want any other framework to be compatible
            assertEquals(getCompatibleFrameworksForEnds(ctx.sessions, peerA, peerB, cf).size(), compliantFrameworks.size());
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
