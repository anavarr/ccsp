package mychor;

import mychor.Generators.GRPCStStClientGenerator;
import mychor.Generators.GRPCStStServerGenerator;
import mychor.Generators.GRPCStUnClientGenerator;
import mychor.Generators.GRPCStUnServerGenerator;
import mychor.Generators.GRPCUnStClientGenerator;
import mychor.Generators.GRPCUnStServerGenerator;
import mychor.Generators.GRPCUnUnClientGenerator;
import mychor.Generators.GRPCUnUnServerGenerator;
import mychor.Generators.GenerationContext;
import mychor.Generators.ReactiveStreamsClientGenerator;
import mychor.Generators.ReactiveStreamsServerGenerator;
import mychor.Generators.RestGeneratorClient;
import mychor.Generators.RestGeneratorServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static mychor.Generators.GenerationConfig.localizedFrameworkSettingList;
import static mychor.Utils.minimize;

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
            for (String necessaryFramework : config.necessaryFrameworks) {
                var idxDep = linesMod.indexOf("    <dependencies>");
                var pomDependencies = localizedFrameworkSettingList.get(necessaryFramework).pomDependencies();
                if(pomDependencies == null){
                    throw new RuntimeException("No POM was provided with this framework");
                }
                for (String s : pomDependencies) {
                    var offset = s.split("\n").length;
                    linesMod.add(idxDep+1, "\t\t<dependency>");
                    linesMod.addAll(idxDep+2, Arrays.stream(s.split("\n")).map(l -> "\t\t\t"+l).toList());
                    linesMod.add(idxDep+2+offset, "\t\t</dependency>");
                }


                var idxBuildExtension = linesMod.indexOf("        <extensions>");
                var pomBuildExtensions = localizedFrameworkSettingList.get(necessaryFramework).pomBuildExtensions();
                if(pomBuildExtensions == null){
                    throw new RuntimeException("No POM was provided with this framework");
                }
                for (String s : pomBuildExtensions) {
                    var offset = s.split("\n").length;
                    linesMod.add(idxBuildExtension+1, "\t\t\t<extension>");
                    linesMod.addAll(idxBuildExtension+2, Arrays.stream(s.split("\n")).map(l -> "\t\t\t"+l).toList());
                    linesMod.add(idxBuildExtension+2+offset, "\t\t\t</extension>");
                }

                var idxBuildPlugins = linesMod.indexOf("        <plugins>");
                var pomBuildPlugins = localizedFrameworkSettingList.get(necessaryFramework).pomBuildPlugins();
                if(pomBuildPlugins == null){
                    throw new RuntimeException("No POM was provided with this framework");
                }
                for (String s : pomBuildPlugins) {
                    var offset = s.split("\n").length;
                    linesMod.add(idxBuildPlugins+1, "\t\t\t<plugin>");
                    linesMod.addAll(idxBuildPlugins+2, Arrays.stream(s.split("\n")).map(l -> "\t\t\t"+l).toList());
                    linesMod.add(idxBuildPlugins+2+offset, "\t\t\t</plugin>");
                }
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
                switch (framework) {
                    case "GRPC_un_un_client" ->
                            generationCtx.generators.put(serviceSession.peerB(), new GRPCUnUnClientGenerator(serviceSession));
                    case "GRPC_un_un_server" ->
                            generationCtx.generators.put(serviceSession.peerB(), new GRPCUnUnServerGenerator(serviceSession));
                    case "GRPC_st_st_client" ->
                            generationCtx.generators.put(serviceSession.peerB(), new GRPCStStClientGenerator(serviceSession));
                    case "GRPC_st_st_server" ->
                            generationCtx.generators.put(serviceSession.peerB(), new GRPCStStServerGenerator(serviceSession));
                    case "GRPC_un_st_client" ->
                            generationCtx.generators.put(serviceSession.peerB(), new GRPCUnStClientGenerator(serviceSession));
                    case "GRPC_un_st_server" ->
                            generationCtx.generators.put(serviceSession.peerB(), new GRPCUnStServerGenerator(serviceSession));
                    case "GRPC_st_un_client" ->
                            generationCtx.generators.put(serviceSession.peerB(), new GRPCStUnClientGenerator(serviceSession));
                    case "GRPC_st_un_server" ->
                            generationCtx.generators.put(serviceSession.peerB(), new GRPCStUnServerGenerator(serviceSession));
                    case "REST_client" ->
                            generationCtx.generators.put(serviceSession.peerB(), new RestGeneratorClient());
                    case "REST_server" ->
                            generationCtx.generators.put(serviceSession.peerB(), new RestGeneratorServer());
                    case "ReactiveStreams_client" ->
                            generationCtx.generators.put(serviceSession.peerB(), new ReactiveStreamsClientGenerator());
                    case "ReactiveStreams_server" ->
                            generationCtx.generators.put(serviceSession.peerB(), new ReactiveStreamsServerGenerator());
                    default -> {
                        System.err.println("NOT IMPLEMENTED");
                        throw new RuntimeException("No generator found for framework " + framework);
                    }
                }
            });
            var config = new ServiceConfiguration(service, "1.0.0",
                    generationCtx.sessionsFrameworks.values().stream().toList());
            createMicroService(service);
            populatePom(config);
            for (Session serviceSession : serviceSessions) {
                generationCtx.staticInit.addAll(generationCtx.generators
                        .get(serviceSession.peerB())
                        .generateClass(service, outPath+"/"+applicationName));
                generationCtx.imports.addAll(generationCtx.generators.get(serviceSession.peerB()).generateMainImports());
            }
            var root = ctx.behaviours.get(service);
            unfoldBehaviour(root);
            gcs.add(new GenerationContext(generationCtx));
            writeCode(config);
        }
    }

    private void writeCode(ServiceConfiguration config) throws IOException {
        String code = String.join("\n",generationCtx.imports) + "\n" +
                Files.readString(Paths.get("src/main/resources/quarkus_default_files/main.txt"));
        Files.createDirectories(Paths.get(outPath,"/", applicationName,"/",config.serviceName, "src/main/java"));
        if(generationCtx.functions.isEmpty()){
            code = code.replace("public int run(String... args) {",
                    String.format("public int run(String... args) {\n%s",
                            String.join("\n",generationCtx.code).replace("\n","\n\t\t")));
        }else{
            var mainList = new ArrayList<String>();
            for (String s : generationCtx.functions.keySet()) {
                if(!s.equals("main")){
                    mainList.add(String.format("public static void %s(){", s));
                    mainList.addAll(generationCtx.functions.get(s));
                    mainList.add("}");
                }
            }
            code = code.replace("public static void main(String... args) {",
                    String.format("%s\n\tpublic static void main(String... args) {",
                            String.join("\n", generationCtx.staticInit)));
            code = code.replace("@Override\n" +
                            "    public int run(String... args) {",
                    String.format("""
                                    %s
                                    @Override
                                        public int run(String... args) {
                                            %s
                                    """,
                            String.join("\n", mainList).replace("\n", "\n\t\t"),
                            String.join("\n",generationCtx.functions.get("main")).replace("\n","\n\t\t")));
        }
        Files.write(Paths.get(outPath,"/", applicationName,"/", config.serviceName, "src/main/java", "Main.java"), code.getBytes());
    }

    private void unfoldBehaviour(Behaviour root) {
        if(generationCtx.code == null) generationCtx.code = new ArrayList<>();
        switch (root) {
            case Cdt cdt -> {
                generationCtx.code.add(String.format("if(%s)",cdt.expr));
                if(cdt.nextBehaviours.containsKey("then")){
                    generationCtx.code.add("{");
                    unfoldBehaviour(cdt.nextBehaviours.get("then"));
                    generationCtx.code.add("}");
                }
                if(cdt.nextBehaviours.containsKey("else")){
                    generationCtx.code.add(String.format("else",cdt.expr));
                    generationCtx.code.add("{");
                    unfoldBehaviour(cdt.nextBehaviours.get("else"));
                    generationCtx.code.add("}");
                }
            }
            case Call call -> {
                var vname = minimize(call.variableName);
                generationCtx.code.add(vname+"();");
                if(generationCtx.functions.isEmpty()){
                    generationCtx.functions.put("main", generationCtx.code);
                    generationCtx.code = new ArrayList<>();
                }
                if(!generationCtx.functions.containsKey(call.variableName) && !call.nextBehaviours.isEmpty()){
                    generationCtx.code = new ArrayList<>();
                    unfoldBehaviour(call.nextBehaviours.get("unfold"));
                    generationCtx.functions.put(vname, generationCtx.code);
                }
            }
            case Comm comm -> {
                generateCom(comm);
            }
            case End end -> {
//                for (String s : generationCtx.sessionsFrameworks.keySet()) {
//                    generationCtx.sessionsFrameworks.get(s).gen
//                }
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
                generationCtx.code.add(generator.generateVoid(comm));
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
