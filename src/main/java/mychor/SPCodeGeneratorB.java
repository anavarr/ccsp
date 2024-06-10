package mychor;

import mychor.Generators.GRPCUnUnClientGenerator;
import mychor.Generators.GRPCUnUnServerGenerator;
import mychor.Generators.GenerationContext;
import mychor.Generators.RestGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mychor.Generators.GenerationConfig.localizedFrameworkSettingList;

public class SPCodeGeneratorB {
    CompilerContext ctx;
    String outPath;
    String applicationName;
    ArrayList<String> highPriorityFrameworks = new ArrayList<>();
    public List<String> necessaryFrameworks;
    PatternDetector pd;
    public GenerationContext generationCtx = new GenerationContext();
    public ArrayList<GenerationContext> gcs = new ArrayList<>();

    record ServiceConfiguration(String serviceName, String version, List<String> necessaryFrameworks){
    }

    public SPCodeGeneratorB(CompilerContext ctx, String path, String applicationName){
        this.ctx=ctx;
        this.outPath = path;
        this.applicationName = applicationName;
        pd = new PatternDetector(ctx);
        try{
            Files.createDirectories(Paths.get(outPath+"/"+ applicationName));
        } catch (IOException e) {
            System.err.println("Problem while creating the distributed application folder");
        }
    }
    public SPCodeGeneratorB(CompilerContext ctx, String path, String applicationName, List<String> hpf){
        this(ctx, path, applicationName);
        this.highPriorityFrameworks.addAll(hpf);
    }

    public HashMap<Session, String> pickRandomCompatibleFramework(Map<Session, List<String>> compatibleFrameworks){
        var hm = new HashMap<Session, String>();
        compatibleFrameworks.keySet().forEach(se -> {
            //compatibleFrameworks can't be empty
            var found = false;
            for (String highPriorityFramework : highPriorityFrameworks) {
                if(compatibleFrameworks.get(se).contains(highPriorityFramework)){
                    hm.put(se, highPriorityFramework);
                    found = true;
                }
            }
            if(!found) hm.put(se, compatibleFrameworks.get(se).stream().findAny().get());
        });
        return hm;
    }

    private void populatePom(ServiceConfiguration config){
        Path path = Paths.get("src/main/resources/quarkus_default_files/template_pom.xml");
        try(var lines = Files.lines(path)) {
            var linesMod = new ArrayList<>(lines.toList());
            var idx = linesMod.indexOf("    <dependencies>");
            List<String> pom;
            for (String necessaryFramework : config.necessaryFrameworks) {
                var frameworkPom = localizedFrameworkSettingList.get(necessaryFramework).pom();
                if(frameworkPom == null){
                    throw new RuntimeException("No POM was provided with this framework");
                }
                linesMod.addAll(idx+1, List.of(frameworkPom.split("\n")));
            }
            Files.write(Path.of(
                    outPath,"/", applicationName,"/",config.serviceName,"/","pom.xml"),
                    String.join("\n", linesMod).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void generateCode() throws IOException {
        var cf = pd.detectCompatibleFrameworks();
        HashMap<Session, String> necessaryFrameworks = pickRandomCompatibleFramework(cf);
        this.necessaryFrameworks = new ArrayList<>(necessaryFrameworks.values());
        for (String service : ctx.behaviours.keySet()) {
            generationCtx = new GenerationContext();
            //serviceSessions filters sessions to keep only those where this service is the source
            var serviceSessions = necessaryFrameworks.keySet().stream()
                    .filter(se -> se.peerA().equals(service)).toList();
            //we populate generationCtx.sessionsFrameworks to
            serviceSessions.forEach(serviceSession -> {
                var framework = necessaryFrameworks.get(serviceSession);
                generationCtx.sessionsFrameworks.put(serviceSession.peerB(), framework);
                if(framework.equals("GRPC_un_un_client")){
                    generationCtx.generators.put(serviceSession.peerB(), new GRPCUnUnClientGenerator(serviceSession));
                }else if(framework.equals("GRPC_un_un_server")){
                    generationCtx.generators.put(serviceSession.peerB(), new GRPCUnUnServerGenerator(serviceSession));
                }else if(framework.contains("REST")){
                    generationCtx.generators.put(serviceSession.peerB(), new RestGenerator());
                }else{
                    System.err.println("NOT IMPLEMENTED");
                    throw new RuntimeException("No generator found for this framework");
                }
            });
            var config = new ServiceConfiguration(service, "1.0.0",
                    generationCtx.sessionsFrameworks.values().stream().toList());
            createMicroService(service);
            populatePom(config);
            for (Session serviceSession : serviceSessions) {
                generationCtx.generators.get(serviceSession.peerB())
                        .generateClass(service, outPath+"/"+applicationName);
            }
            var root = ctx.behaviours.get(service);
            unfoldBehaviour(root);
            gcs.add(new GenerationContext(generationCtx));
            writeCode(config);
        }
    }

    private void writeCode(ServiceConfiguration config) throws IOException {
        String code = Files.readString(Paths.get("src/main/resources/quarkus_default_files/main.txt"));
        Files.createDirectories(Paths.get(outPath,"/", applicationName,"/",config.serviceName, "src/main/java"));
        code = code.replace("public int run(String... args) {",
                String.format("public int run(String... args) {\n%s",
                        String.join("\n",generationCtx.code).replace("\n","\n\t\t")));
        Files.write(Paths.get(outPath,"/", applicationName,"/", config.serviceName, "src/main/java", "Main.java"), code.getBytes());
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
        var generator = generationCtx.generators.get(comm.destination);
        switch (comm.direction){
            case SEND -> {
                generationCtx.code.add(generator.generateSend(comm));
                if(!comm.nextBehaviours.isEmpty()) unfoldBehaviour(comm.nextBehaviours.get(";"));
            }
            case RECEIVE -> {
                generationCtx.code.add(generator.generateRcv(comm));
                if(!comm.nextBehaviours.isEmpty()) unfoldBehaviour(comm.nextBehaviours.get(";"));
            }
            case BRANCH -> {
                generationCtx.code.add(generator.generateBranch(comm));
            }
            case SELECT -> {
                generationCtx.code.add(generator.generateSelect(comm));
                if(!comm.nextBehaviours.isEmpty()) unfoldBehaviour(comm.nextBehaviours.get(comm.labels.get(0)));
            }
            case VOID -> {
            }
            default -> {

            }
        }
    }

    public void createMicroService(String pa){
        try{
            Files.createDirectories(Paths.get(outPath+"/"+ applicationName +"/"+pa));
        } catch (IOException e) {
            System.err.println("Problem while creating the folder for µservice "+pa);
        }
    }
}
