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

public class GRPCStUnServerGenerator extends GRPCStUnGenerator {
    static int port = 5001;
    public GRPCStUnServerGenerator(Session serviceSession) {
        super(serviceSession);
    }

    @Override
    public String generateSend(Comm comm) {
        return String.format("""
            if(!cfCompleted.isEmpty()){
                cfSend.offer("%s");
                return;
            }
        """, comm.labels.getFirst());
    }

    @Override
    public String generateRcv(Comm comm) {
        return String.format("""
            if(cfCompleted.isEmpty()){
                var %s = cfReceive.poll();
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
    public String closeSession() {
        return """
            // keep the server running
            %s.awaittermination();
        """;
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
                static ConcurrentLinkedQueue<String> cfSend = new ConcurrentLinkedQueue<String>();
                static ConcurrentLinkedQueue<String> cfReceive = new ConcurrentLinkedQueue<String>();
                static ConcurrentLinkedQueue<String> cfCompleted = new ConcurrentLinkedQueue<String>();
                static %sImpl %s = new %sImpl(cfSend, cfReceive, cfCompleted);
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
                import java.util.concurrent.ConcurrentLinkedQueue;
                import java.util.concurrent.CompletableFuture;
                import %s.%sGrpc;
                import %s.%s.Message;
                """, packageName, packageName, serviceName, packageName, protoName);
        var classText = String.format("""
                public class %sImpl extends %sGrpc.%sImplBase {
                    public ConcurrentLinkedQueue<String> cfReceive;
                    public ConcurrentLinkedQueue<String> cfSend;
                    public ConcurrentLinkedQueue<String> cfCompleted;
                
                    public %sImpl(ConcurrentLinkedQueue<String> cfSend, ConcurrentLinkedQueue<String> cfReceive, ConcurrentLinkedQueue<String> cfCompleted){
                        this.cfSend = cfSend;
                        this.cfCompleted = cfCompleted;
                        this.cfReceive = cfReceive;
                    }
                    @Override
                     public StreamObserver<Message> communicate(StreamObserver<Message> responseObserver) {
                         return new StreamObserver<Message>() {
                             // We'll concatenate all the messages here
                             StringBuilder messages = new StringBuilder();
             
                             @Override
                             public void onNext(Message message) {
                                 System.out.println("Received message: " + message.getMsg());
                                 try{
                                    cfReceive.offer(message.getMsg());
                                } catch (Exception e){
                                    //
                                }
                                 messages.append(message.getMsg()).append(" ");
                             }
             
                             @Override
                             public void onError(Throwable t) {
                                 System.err.println("Error receiving messages: " + t.getMessage());
                             }
             
                             @Override
                             public void onCompleted() {
                                cfCompleted.offer("right u done mate");
                                try{
                                     var responseMessage = cfSend.poll();
                                    // When the client finishes sending messages, we reply with a confirmation
                                     Message msg = Message.newBuilder()
                                        .setMsg("Hello, " + responseMessage)
                                         .build();
                                     responseObserver.onNext(msg);
                                     responseObserver.onCompleted();
                                } catch (Exception e){
                                    //
                                }
                             }
                         };
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
