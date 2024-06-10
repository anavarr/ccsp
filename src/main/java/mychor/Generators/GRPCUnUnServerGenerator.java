package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GRPCUnUnServerGenerator implements Generator {
    String className;
    static int port;
    String serverName;
    private final Session session;

    public GRPCUnUnServerGenerator(Session serviceSession) {
        session = serviceSession;
    }

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

    private String setup(){
        return String.format("""
            Server %s = ServerBuilder.forPort(%d)
                    .addService(new %s())
                    .build();
    
            // Start the server
            %s.start();
            System.out.println("Server started on port %d");
    
            // Keep the server running
            %s.awaitTermination();
        """, serverName, port, className, serverName, port, serverName);
    }

    @Override
    public void generateClass(String service, String applicationPath) throws IOException {
        System.out.println(session);
        var proto = String.format("""
                syntax = "proto3";
                package %s;
                message Message {
                  string msg = 1;
                }
                service %s {
                  rpc Communicate(Message) returns (Message);
                }""",service,session.peerA()+"-"+session.peerB());
        Files.createDirectories(Paths.get(applicationPath,"protobuf"));
        Files.write(Path.of(
                        applicationPath,"protobuf",session.peerA()+"-"+session.peerB()+".proto"),
                proto.getBytes());
        var serverImpl= String.format("""
                package server;
                
                import io.grpc.Server;
                import io.grpc.ServerBuilder;
                import io.grpc.stub.StreamObserver;
                import %s.ServiceGrpc;
                import %s.Message;
                import java.io.IOException;
                
                public class ServiceImpl extends ServiceGrpc.ServiceImplBase {
                    @Override
                    public void communicate(Message request, StreamObserver<Message> responseObserver) {
                        // Handle the request and send a response
                        String requestMessage = request.getMsg();
                        System.out.println("Received message: " + requestMessage);
                        
                        HERE CREATE A LATCH OR A CF OR SOMETHING THAT WILL BE WAITED FOR 
                        IN MAIN WHEN IT IS TIME TO RECEIVE A MESSAGE ON SERVER
                        
                        // Create a response message
                        Message response = Message.newBuilder()
                                .setMsg("Hello, " + requestMessage)
                                .build();
                        // Send the response
                        responseObserver.onNext(response);
                        
                        WAIT FOR A LATCH OR SOMETHING THAT IS CREATED BEFOREHAND AND THAT IS COMPLETED IN MAIN WHEN MESSAGE IS SENT
                        
                        // Complete the RPC call
                        responseObserver.onCompleted();
                    }
                }
                """, service, service);
        Files.createDirectories(Paths.get(applicationPath,service,"src", "main", "java"));
        Files.write(Path.of(
                        applicationPath,service,"src", "main", "java", "ServerImpl.java"),
                serverImpl.getBytes());
    }


}
