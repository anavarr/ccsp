package mychor.Generators;

import mychor.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GRPCUnUnGenerator extends GRPCGenerator {
    public GRPCUnUnGenerator(Session serviceSession) {
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
        return super.generateClass(service, applicationPath, "rpc Communicate(Message) returns (Message);");
    }
}