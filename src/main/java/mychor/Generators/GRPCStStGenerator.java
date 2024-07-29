package mychor.Generators;

import mychor.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GRPCStStGenerator extends GRPCGenerator {

    public GRPCStStGenerator(Session serviceSession){
        super(serviceSession);
    }

    @Override
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {
        return super.generateClass(service, applicationPath, "rpc Communicate(stream Message) returns (stream Message);");
    }
}
