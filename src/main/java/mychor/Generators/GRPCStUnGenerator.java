package mychor.Generators;

import mychor.Session;

import java.io.IOException;
import java.util.ArrayList;

public abstract class GRPCStUnGenerator extends GRPCGenerator {
    public GRPCStUnGenerator(Session serviceSession) {
        super(serviceSession);
    }

    @Override
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {
        return super.generateClass(service, applicationPath, "rpc Communicate(stream Message) returns (Message);");
    }
}
