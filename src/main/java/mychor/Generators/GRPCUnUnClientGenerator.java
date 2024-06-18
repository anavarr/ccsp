package mychor.Generators;
import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GRPCUnUnClientGenerator extends GRPCUnUnGenerator{
    String channelName;
    String asyncStubName;

    public GRPCUnUnClientGenerator(Session session){
        super(session);
    }


    @Override
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {
        alreadySetup = true;
        setupLock.lock();
        channelName = "channel_"+generatorCounter;
        asyncStubName = "asyncStubName_"+generatorCounter;
        generatorCounter++;
        setupLock.unlock();
        var staticInit = super.generateClass(service, applicationPath);
        staticInit.addAll(new ArrayList<>(List.of(
                String.format("""
                    static ManagedChannel %s = ManagedChannelBuilder.forAddress("localhost", 5001)
                .usePlaintext()
                .build();
            // Create an asynchronous stub
            static %sGrpc.%sStub %s = %sGrpc.newStub(%s);
            """, channelName, serviceName, serviceName, asyncStubName, serviceName, channelName)
        )));
        return staticInit;
    }

    @Override
    public String generateSend(Comm comm) {
        messageLock.lock();
        var suffix = generatorCounter;
        counterQueue.add(suffix);
        generatorCounter++;
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
                """, asyncStubName,requestName, cfName, cfName);

        return createRequest+"\n"+createCF+"\n"+createCb;

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
        try {
            var %s = %s.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        """, variableName, cfName);
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
    public String closeSession() {
        return "";
    }

    @Override
    public ArrayList<String> generateMainImports() {
        var i = super.generateMainImports();
        i.addAll(List.of(
                "import "+packageName+"."+serviceName+"Grpc;",
                "import "+packageName+"."+protoName+".Message;",
                "import io.grpc.ManagedChannel;",
                "import java.util.concurrent.ExecutionException;",
                "import java.util.concurrent.CompletableFuture;",
                "import io.grpc.stub.StreamObserver;",
                "import io.grpc.ManagedChannelBuilder;"));
        return i;
    }
}