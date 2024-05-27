package mychor;

import com.ibm.icu.impl.ICUResourceBundle;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPCodeGeneratorB {
    CompilerContext ctx;
    String outPath;
    String name;
    PatternDetector pd;
    static HashMap<String, String> patternsPom = new HashMap<>();
    GenerationContext generationCtx = new GenerationContext();
    public ArrayList<GenerationContext> gcs = new ArrayList<>();
    public static class GenerationContext{
        public ArrayList<String> code = new ArrayList<>();
        public HashMap<String, ArrayList<String>> functions= new HashMap<>();
        HashMap<String, String> sessionsFrameworks = new HashMap<>();
        public GenerationContext(){}

        public GenerationContext(GenerationContext gc){
            code.addAll(gc.code);
            functions.putAll(gc.functions);
            sessionsFrameworks.putAll(gc.sessionsFrameworks);
        }
    }

    static{
        String grpcPom = """
                
                <dependency>
                      <groupId>io.grpc</groupId>
                      <artifactId>grpc-protobuf</artifactId>
                    </dependency>
                    <dependency>
                      <groupId>io.grpc</groupId>
                      <artifactId>grpc-stub</artifactId>
                    </dependency>
                    <dependency>
                      <groupId>io.grpc</groupId>
                      <artifactId>grpc-netty-shaded</artifactId>
                    </dependency>""";
        String restPom = """
                
                <dependency>
                    <groupId>io.rest.reactive</groupId>
                    <artifactId>myRestStuff</artifactId>
                    <version>2.6.0</version>
                </dependency>
                """;
        String mutinyPom = """
                
                <dependency>
                    <groupId>io.smallrye.reactive</groupId>
                    <artifactId>mutiny</artifactId>
                    <version>2.6.0</version>
                </dependency>
                """;
        patternsPom.put("REST", restPom);
        patternsPom.put("REST_Recursive", restPom);
        patternsPom.put("GRPC_un_un", grpcPom);
        patternsPom.put("GRPC_un_st", grpcPom);
        patternsPom.put("GRPC_st_un", grpcPom);
        patternsPom.put("GRPC_st_st", grpcPom);
        patternsPom.put("ReactiveStreams",mutinyPom);
    }

    record ServiceConfiguration(String name, String version, List<String> necessaryFrameworks){
    }

    public SPCodeGeneratorB(CompilerContext ctx, String path, String name){
        this.ctx=ctx;
        this.outPath = path;
        this.name = name;
        pd = new PatternDetector(ctx);
        try{
            Files.createDirectories(Paths.get(outPath+"/"+name));
        } catch (IOException e) {
            System.err.println("Problem while creating the distributed application folder");
        }
    }

    public HashMap<Session, String> pickRandomCompatibleFramework(Map<Session, List<String>> compatibleFrameworks){
        var hm = new HashMap<Session, String>();
        compatibleFrameworks.keySet().forEach(se -> {
            //compatibleFrameworks can't be empty
            hm.put(se, compatibleFrameworks.get(se).stream().findAny().get());
        });
        return hm;
    }

    private void populatePom(ServiceConfiguration config){
        Path path = Paths.get("src/main/resources/quarkus_default_files/template_pom.xml");
        try(var lines = Files.lines(path)) {
            var linesMod = new ArrayList<>(lines.toList());
            var idx = linesMod.indexOf("    <dependencies>");
            for (String necessaryFramework : config.necessaryFrameworks) {
                necessaryFramework = necessaryFramework.replace("_server", "");
                necessaryFramework = necessaryFramework.replace("_client", "");
                linesMod.addAll(idx+1, List.of(
                        patternsPom.get(necessaryFramework).replace("\n", "\n\t\t")
                                .split("\n")));
            }
            Files.write(Path.of(
                    outPath,"/",name,"/",config.name,"/","pom.xml"),
                    String.join("\n", linesMod).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateCode() throws IOException {
        HashMap<Session, String> necessaryFrameworks = pickRandomCompatibleFramework(pd.detectCompatibleFrameworks());
        for (String service : ctx.behaviours.keySet()) {
            generationCtx = new GenerationContext();
            //serviceSessions filters sessions to keep only those where this service is the source
            var serviceSessions = necessaryFrameworks.keySet().stream()
                    .filter(se -> se.peerA().equals(service)).toList();
            //we populate generationCtx.sessionsFrameworks to
            serviceSessions.forEach(serviceSession ->
                    generationCtx.sessionsFrameworks.put(serviceSession.peerB(), necessaryFrameworks.get(serviceSession)));
            var config = new ServiceConfiguration(service, "1.0.0",
                    generationCtx.sessionsFrameworks.values().stream().toList());
            createMicroService(service);
            populatePom(config);
            var root = ctx.behaviours.get(service);
            unfoldBehaviour(root);
            gcs.add(new GenerationContext(generationCtx));
            writeCode(config);
        }
    }

    private void writeCode(ServiceConfiguration config) throws IOException {
        String code = Files.readString(Paths.get("src/main/resources/quarkus_default_files/main.txt"));
        Files.createDirectories(Paths.get(outPath,"/",name,"/",config.name, "src/main/java"));
        code = code.replace("public int run(String... args) {",
                String.format("public int run(String... args) {\n%s",
                        String.join("\n",generationCtx.code).replace("\n","\n\t\t")));
        Files.write(Paths.get(outPath,"/", name,"/", config.name, "src/main/java", "Main.java"), code.getBytes());
    }

    private void unfoldBehaviour(Behaviour root) {
        switch (root) {
            case Cdt cdt -> {
                generationCtx.code.add(String.format("if(%s)",cdt.expr));
                if(cdt.nextBehaviours.containsKey("then")){
                    generationCtx.code.add("{");
                    unfoldBehaviour(cdt.nextBehaviours.get("then"));
                    generationCtx.code.add("}");
                }
                if(cdt.nextBehaviours.containsKey("else")){
                    generationCtx.code.add("{");
                    unfoldBehaviour(cdt.nextBehaviours.get("else"));
                    generationCtx.code.add("}");
                }
            }
            case Call call -> {
                if(!generationCtx.functions.containsKey(call.variableName) && !call.nextBehaviours.isEmpty()){
                    generationCtx.code.add(call.variableName+"();");
                    generationCtx.code = new ArrayList<>();
                    unfoldBehaviour(call.nextBehaviours.get("unfold"));
                    generationCtx.functions.put(call.variableName, generationCtx.code);
                }
            }
            case Comm comm -> {
                generateCom(comm);
            }
            case End end -> {
            }
            case None none -> {
            }
            default -> throw new IllegalStateException("Unexpected value: " + root);
        }
    }

    private void generateCom(Comm comm) {
        System.out.println(generationCtx.sessionsFrameworks.get(comm.destination));
        switch (comm.direction){
            case SEND -> {
                generateSend(comm);
            }
            case RECEIVE -> {
                generateRcv(comm);
            }
            case BRANCH -> {
                generateBranch(comm);
            }
            case SELECT -> {
                generateSelect(comm);
            }
            case VOID -> {
            }
            default -> {

            }
        }
    }

    private void generateSelect(Comm comm) {
        generationCtx.code.add(String.format("select(%s,%s);", comm.destination, comm.labels.getFirst()));
        unfoldBehaviour(comm.nextBehaviours.get(comm.labels.getFirst()));
    }

    private void generateBranch(Comm comm) {
        for (String s : comm.nextBehaviours.keySet()) {
            generationCtx.code.add(String.format("branch(%s,%s);", comm.destination, s));
            unfoldBehaviour(comm.nextBehaviours.get(s));
        }
    }

    private void generateRcv(Comm comm) {
        generationCtx.code.add(String.format("var %s = recv(%s);", comm.labels.getFirst(),comm.destination));
        unfoldBehaviour(comm.nextBehaviours.get(";"));
    }

    private void generateSend(Comm comm) {
        generationCtx.code.add(String.format("send(%s,%s);", comm.destination, comm.labels.getFirst()));
        unfoldBehaviour(comm.nextBehaviours.get(";"));
    }

    public void createMicroService(String pa){
        try{
            Files.createDirectories(Paths.get(outPath+"/"+name+"/"+pa));
        } catch (IOException e) {
            System.err.println("Problem while creating the folder for µservice "+pa);
        }
    }
}
