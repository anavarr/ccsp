package mychor.Generators;

import mychor.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GRPCUnStGenerator extends GRPCGenerator{
    public GRPCUnStGenerator(Session serviceSession) {
        super(serviceSession);
    }

    @Override
    public ArrayList<String> generateMainImports() {
        return new ArrayList<>(List.of(
                "import java.util.concurrent.SynchronousQueue;"
        ));
    }

    @Override
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {
        return super.generateClass(service, applicationPath, "rpc Communicate(Message) returns (stream Message);");
    }
}
