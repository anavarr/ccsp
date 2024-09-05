package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GRPCStUnClientGenerator extends GRPCStUnGenerator {
    String channelName;
    String asyncStubName;
    String cfName;
    String requestName;

    public GRPCStUnClientGenerator(Session serviceSession) {
        super(serviceSession);
    }

    @Override
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {
        alreadySetup = true;
        setupLock.lock();
        channelName = "channel_"+generatorCounter;
        asyncStubName = "asyncStubName_"+generatorCounter;
        cfName = "cf_"+generatorCounter;
        requestName = "request_"+generatorCounter;
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
            static CompletableFuture<String> %s = new CompletableFuture<>();
            """, channelName, serviceName, serviceName, asyncStubName, serviceName, channelName, cfName)
        )));
        staticInit.addAll(new ArrayList<>(List.of(
                String.format("""
                        static StreamObserver<Message> %s = %s.communicate(new StreamObserver<>(){
                            @Override
                            public void onNext(Message m) {
                                %s.complete(m.getMsg());
                            }
        
                            @Override
                            public void onError(Throwable t) {
                                // Should never reach here.
                                throw new Error(t);
                            }
        
                            @Override
                            public void onCompleted() {
                                System.out.println("completed");
                            }
                        });
                    """, requestName, asyncStubName, cfName)
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
        var localRequestName = "request_"+suffix;
        var createRequest = String.format("Message %s = Message.newBuilder().setMsg(\"%s\").build();",
                localRequestName,
                comm.labels.getFirst());
        var sendRequest = String.format("%s.onNext(%s);", requestName, localRequestName);
        return createRequest+"\n"+sendRequest;
    }

    @Override
    public String generateRcv(Comm comm) {
        return String.format("""
            try{
                var response = %s.get();
            }catch (Exception e){
                //
            }
            """,cfName);
    }

    @Override
    public String generateSelect(Comm comm) {
        if(comm.labels.getFirst().equals("\"end\"")) {
            System.out.println("ending");
            return String.format("%s.onCompleted();", requestName);
        };
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
