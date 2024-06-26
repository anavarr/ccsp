package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static mychor.Utils.minimize;

public class GRPCUnUnServerGenerator extends GRPCUnUnGenerator {
    static int port = 5001;

    public GRPCUnUnServerGenerator(Session serviceSession) {
        super(serviceSession);
    }

    @Override
    public String generateSend(Comm comm) {
        return String.format("""
            try {
                cfSend.put("%s");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        """, comm.labels.getFirst());
    }

    @Override
    public String generateRcv(Comm comm) {
        return String.format("""
            try{
                var %s = cfReceive.take();
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
        """, comm.labels.getFirst());
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
        var superImports = super.generateClass(service, applicationPath);
        setupLock.lock();
        var suffix = generatorCounter;
        setupLock.unlock();
        var serverName = "server_"+suffix;
        superImports.addAll(new ArrayList<>(
                Arrays.stream(String.format("""
                static SynchronousQueue<String> cfSend = new SynchronousQueue<String>();
        static SynchronousQueue<String> cfReceive = new SynchronousQueue<String>();
        static %sImpl %s = new %sImpl(cfSend, cfReceive);
        static Server %s = ServerBuilder.forPort(%d)
                .addService(%s)
                .build();
        static {
            try {
                %s.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        """, serviceName, minimize(serviceName), serviceName, serverName, port, minimize(serviceName), serverName).split("\n")).toList()
        ));

        var header = String.format("""
                package %s;
                import io.grpc.stub.StreamObserver;
                import java.util.concurrent.SynchronousQueue;
                import %s.%sGrpc;
                import %s.%s.Message;
                """, packageName, packageName, serviceName, packageName, protoName);
        var classText = String.format("""
                public class %sImpl extends %sGrpc.%sImplBase {
                    public SynchronousQueue<String> cfReceive;
                    public SynchronousQueue<String> cfSend;
                
                    public %sImpl(SynchronousQueue<String> cfSend, SynchronousQueue<String> cfReceive){
                        this.cfSend = cfSend;
                        this.cfReceive = cfReceive;
                    }
                    @Override
                    public void communicate(Message request, StreamObserver<Message> responseObserver) {
                        // Handle the request and send a response
                        String requestMessage = request.getMsg();
                        try {
                            cfReceive.put(requestMessage);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println("Received message: " + requestMessage);
                        
                        // Create a response message
                        String responseMessage;
                        try {
                            responseMessage = cfSend.take();
                            Message response = Message.newBuilder()
                                .setMsg("Hello, " + responseMessage)
                                .build();
                            // Send the response
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                """, serviceName, serviceName, serviceName, serviceName);
        Files.createDirectories(Paths.get(applicationPath,service,"src", "main", "java", packageName));
        Files.write(Path.of(applicationPath,service,"src", "main", "java",
                        packageName, String.format("%sImpl.java", serviceName)),
                (header+classText).getBytes());
        return superImports;
    }

    @Override
    public String closeSession() {
        return """
            // Keep the server running
            %s.awaitTermination();
        """;
    }

    @Override
    public ArrayList<String> generateMainImports() {
        var i = super.generateMainImports();
        i.addAll(List.of(
                "import io.grpc.Server;",
                "import io.grpc.ServerBuilder;",
                "import java.io.IOException;",
                String.format("import %s.%sImpl;", packageName, serviceName)));
        return i;
    }


}
