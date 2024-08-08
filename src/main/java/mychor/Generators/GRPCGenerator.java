package mychor.Generators;

import mychor.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class GRPCGenerator implements Generator{
    String serviceName;
    String protoName;
    Session session;
    boolean alreadySetup = false;
    String packageName = "grpc";
    static long generatorCounter = 0;
    static ReentrantLock setupLock = new ReentrantLock();
    static ReentrantLock messageLock = new ReentrantLock();
    ArrayBlockingQueue<Long> counterQueue = new ArrayBlockingQueue<>(100);

    public GRPCGenerator(Session serviceSession) {
        session = serviceSession;
        protoName = "GrpcService";
        serviceName = "GRPCService";
    }

    protected ArrayList<String> generateClass(String service, String applicationPath, String rpc) throws IOException {
        var proto = String.format("""
                syntax = "proto3";
                package grpc;
                message Message {
                  string msg = 1;
                }
                service %s {
                  %s
                }""", serviceName, rpc);
        Files.createDirectories(Paths.get(applicationPath,service, "src", "main", "proto"));
        Files.write(Path.of(
                        applicationPath,service, "src", "main", "proto",protoName+".proto"),
                proto.getBytes());
        return new ArrayList<>();
    }
}
