package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class GRPCUnUnClientGenerator implements Generator{
    private boolean alreadySetup = false;
    String channelName;
    String asyncStubName;
    private final Session session;
    static int generatorCounter = 0;
    private long counter = 0;
    public GRPCUnUnClientGenerator(Session session){
        this.session = session;
    }
    ReentrantLock setupLock = new ReentrantLock();
    ReentrantLock messageLock = new ReentrantLock();
    ArrayBlockingQueue<Long> counterQueue = new ArrayBlockingQueue<>(100);

    private String setup(){
        if(alreadySetup) return "";
        alreadySetup = true;
        setupLock.lock();
        channelName = "channel_"+generatorCounter;
        asyncStubName = "asyncStubName_"+generatorCounter;
        generatorCounter++;
        setupLock.unlock();
        return
            String.format("""
                    ManagedChannel %s = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
            // Create an asynchronous stub
            ServiceGrpc.ServiceStub %s = ServiceGrpc.newStub(%s);
            """, channelName, asyncStubName, channelName);
    }

    @Override
    public String generateSend(Comm comm) {
        var setup = setup();
        messageLock.lock();
        var suffix = counter;
        counterQueue.add(suffix);
        counter++;
        messageLock.unlock();
        var requestName = "request_"+suffix;
        var cfName = "cf_"+suffix;

        var createRequest = String.format("Message %s = Message.newBuilder().setMsg(\"%s\").build();",
                requestName,
                comm.labels.getFirst());
        var createCF = String.format("CompletableFuture<String> %s = new CompletableFuture<>();",
                cfName);
        var createCb = String.format(
                """
                %s.communicate(%s, new StreamObserver<Message>() {
                    @Override
                    public void onNext(Message response) {
                        // Handle the response
                        %s.complete(response.getMsg());
                    }
        
                    @Override
                    public void onError(Throwable t) {
                        // Handle the error
                        System.err.println("Error occurred: " + t.getMessage());
                        %s.cancel(true);
                    }
        
                    @Override
                    public void onCompleted() {
                        // Signal that the call has completed
                        // do nothing ?
                    }
                });
                """, requestName, asyncStubName, cfName, cfName);

        return setup+"\n"+createRequest+"\n"+createCF+"\n"+createCb;

    }

    @Override
    public String generateRcv(Comm comm) {
        var suffix = counterQueue.poll();
        var variableName = comm.labels.getFirst();
        if(suffix == null){
            throw new RuntimeException("error when receiving a message, no message was send beforehand");
        }
        var cfName = "cf_"+suffix;
        return String.format("""
        var %s = %s.get();
        """, variableName, cfName);
    }

    @Override
    public String generateSelect(Comm comm) {
        setup();
        return "";
    }

    @Override
    public String generateBranch(Comm comm) {
        setup();
        return "";
    }

    @Override
    public void generateClass(String service, String applicationPath) throws IOException {
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
    }
}