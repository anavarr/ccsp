package mychor.Generators;

import mychor.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GRPCStUnGenerator extends GRPCGenerator {
    public GRPCStUnGenerator(Session serviceSession) {
        super(serviceSession);
    }

    @Override
    public ArrayList<String> generateMainImports() {
        return new ArrayList<>(List.of(
                "import java.util.concurrent.ConcurrentLinkedQueue;"
        ));
    }

    @Override
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {
        return super.generateClass(service, applicationPath, "rpc Communicate(stream Message) returns (Message);");
    }
}
