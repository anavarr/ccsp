package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GRPCUnUnServerGenerator extends GRPCUnUnGenerator {
    static int port = 5001;

    public GRPCUnUnServerGenerator(Session serviceSession) {
        super(serviceSession);
    }

    @Override
    public String generateSend(Comm comm) {
        return String.format("cfSend.complete(%s);", comm.labels.getFirst());
    }

    @Override
    public String generateRcv(Comm comm) {
        return String.format("var %s = cfReceive.get();", comm.labels.getFirst());
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
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {
        super.generateClass(service, applicationPath);
        setupLock.lock();
        var suffix = generatorCounter;
        setupLock.unlock();
        var serverName = "server_"+suffix;
        var imports = String.format("""
                static CompletableFuture cfSend = new CompletableFuture<String>();
        static CompletableFuture cfReceive = new CompletableFuture<String>();
        static Server %s = ServerBuilder.forPort(%d)
                .addService(new %sImpl(cfSend, cfReceive))
                .build();""", serverName, port, serviceName);

        var header = String.format("""
                package %s;
                
                import io.grpc.Server;
                import io.grpc.ServerBuilder;
                import io.grpc.stub.StreamObserver;
                import java.util.concurrent.CompletableFuture;
                import %s.%sGrpc;
                import java.util.concurrent.CompletableFuture;
                import java.util.concurrent.ExecutionException;
                import %s.%s.Message;
                import java.io.IOException;
                """, packageName, packageName, serviceName, packageName, protoName);
        var classText = String.format("""
                public class %sImpl extends %sGrpc.%sImplBase {
                    public CompletableFuture<String> cfReceive;
                    public CompletableFuture<String> cfSend;
                
                    public %sImpl(CompletableFuture<String> cfSend, CompletableFuture<String> cfReceive){
                        this.cfSend = cfSend;
                        this.cfReceive = cfReceive;
                    }
                    @Override
                    public void communicate(Message request, StreamObserver<Message> responseObserver) {
                        // Handle the request and send a response
                        String requestMessage = request.getMsg();
                        cfReceive.complete(requestMessage);
                        System.out.println("Received message: " + requestMessage);
                        
                        // Create a response message
                        String responseMessage;
                        try {
                            responseMessage = cfSend.get();
                            Message response = Message.newBuilder()
                                .setMsg("Hello, " + responseMessage)
                                .build();
                            // Send the response
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                        } catch (InterruptedException | ExecutionException e) {
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
        return new ArrayList<>(List.of(imports.split("\n")));
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
                String.format("import %s.%sImpl;", packageName, serviceName)));
        return i;
    }


}
