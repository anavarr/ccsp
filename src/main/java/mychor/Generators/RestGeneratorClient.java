package mychor.Generators;

import mychor.Comm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestGeneratorClient implements Generator{

    @Override
    public String generateSend(Comm comm) {
        return "";
    }

    @Override
    public String generateRcv(Comm comm) {
        return "";
    }

    @Override
    public String generateSelect(Comm comm) {
        return "";
    }

    @Override
    public String generateBranch(Comm comm) {
        return "";
    }

    @Override
    public String generateVoid(Comm comm) {
        return "";
    }

    @Override
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {
//        if(framework.contains("_client")){
//            var selectLabels = session.getLabels(Utils.Direction.SELECT);
//            var clientClass = String.format("""
//                    package org.acme.rest.client;
//                    import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
//                    import jakarta.ws.rs.GET;
//                    import jakarta.ws.rs.Path;
//                    import jakarta.ws.rs.QueryParam;
//                    import java.util.Set;
//                    @Path("/")
//                    @RegisterRestClient
//                    public interface ExtensionsService {
//                    """);
//            for (String selectLabel : selectLabels) {
//                clientClass+= String.format("""
//                            @GET
//                            @Path("/%s")
//                            Object get%s();
//                            """, selectLabel, selectLabel);
//            }
//            clientClass+="}";
//        }else if(framework.contains("_server")){
//
//        }
        return new ArrayList<>();
    }

    @Override
    public String closeSession() {
        return "";
    }

    @Override
    public ArrayList<String> generateMainImports() {
        return new ArrayList<>();
    }

}
