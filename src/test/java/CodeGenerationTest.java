import mychor.Generators.GenerationConfig;
import mychor.Generators.GenerationContext;
import mychor.SPCodeGeneratorB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class CodeGenerationTest extends ProgramReaderTest{
    String path = "/tmp";
    String name = "MyQuarkusDistributedApplication";

    @BeforeEach
    public void cleanOutput() throws IOException {
        deleteDir(new File(path+"/"+name));
        assertFalse(Files.exists(Path.of(path, name)));
    }

    void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    @Test
    public void ParsingJSONPatternsShouldGiveBothLocalizedAndFrameworkSettings(){
        assertEquals(GenerationConfig.localizedFrameworkSettingList.size(), 12);
        assertEquals(GenerationConfig.settingsList.size(), 6);
    }

    @Test
    public void generatingMicroServiceFromEmptyFileShouldCreateEmptyFolder() throws IOException {
        var ctx = testFile("empty.sp").compilerCtx;
        new SPCodeGeneratorB(ctx, path, name);
        assertTrue(Files.exists(Path.of(path, name)));
    }

    @Test
    public void simpleClientServerApplicationShouldCreateClientMicroserviceAndServerMicroservice() throws IOException {
        var ctx = testFile("recursive_request_response.sp").compilerCtx;
        var generator = new SPCodeGeneratorB(ctx, path, name);
        generator.generateCode();
        assertTrue(Files.exists(Path.of(path, name)));
        assertTrue(Files.exists(Path.of(path, name, "client")));
        assertTrue(Files.exists(Path.of(path, name, "server")));
    }

    @Test
    public void codeWithoutCallShouldLeaveFunctionsEmpty() throws IOException {
        var ctx = testFile("behavioursCombinations/simple_message_chain.sp").compilerCtx;
        var generator = new SPCodeGeneratorB(ctx, path, name);
        generator.generateCode();
        assertFalse(generator.gcs.isEmpty());
        for (GenerationContext gc : generator.gcs) {
            assertTrue(gc.functions.isEmpty());
            assertFalse(gc.code.isEmpty());
        }
    }

    // ===== GRPC =====
    @Test
    public void basicGRPC_st_stShouldCreateProtoFilesAndServerAndClient() throws IOException {
        var ctx = testFile("recursive_request_response.sp").compilerCtx;
        var generator = new SPCodeGeneratorB(ctx, path, name, List.of("GRPC_st_st_server","GRPC_st_st_client"));
        generator.generateCode();
        assertTrue(generator.necessaryFrameworks.containsAll(List.of(
                "GRPC_st_st_server",
                "GRPC_st_st_client")));
        assertTrue(Files.exists(Path.of(path,name,"client","pom.xml")));
        assertTrue(Files.exists(Path.of(path,name,"server","pom.xml")));
        assertTrue(Files.exists(Path.of(path,name,"client","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"server","src", "main","proto", "GrpcService.proto")));
    }

    @Test
    public void basicGRPC_st_unShouldCreateProtoFilesAndServerAndClient() throws IOException {
        var ctx = testFile("server_request_one_response.sp").compilerCtx;
        var generator = new SPCodeGeneratorB(ctx, path, name, List.of("GRPC_st_un_server","GRPC_st_un_client"));
        generator.generateCode();
        assertTrue(generator.necessaryFrameworks.containsAll(List.of(
                "GRPC_st_un_server",
                "GRPC_st_un_client")));
        assertTrue(Files.exists(Path.of(path,name,"client","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"server","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"client","pom.xml")));
        assertTrue(Files.exists(Path.of(path,name,"server","pom.xml")));
    }

    @Test
    public void basicGRPC_un_stShouldCreateProtoFilesAndServerAndClient() throws IOException {
        var ctx = testFile("request_several_response.sp").compilerCtx;
        var generator = new SPCodeGeneratorB(ctx, path, name, List.of("GRPC_un_st_server","GRPC_un_st_client"));
        generator.generateCode();
        assertTrue(generator.necessaryFrameworks.containsAll(List.of(
                "GRPC_un_st_server",
                "GRPC_un_st_client")));
        assertTrue(Files.exists(Path.of(path,name,"client","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"server","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"client","pom.xml")));
        assertTrue(Files.exists(Path.of(path,name,"server","pom.xml")));
    }

    @Test
    public void basicGRPC_un_unShouldCreateProtoFilesAndServerAndClient() throws IOException {
        var ctx = testFile("recursive_request_response.sp").compilerCtx;
        var generator = new SPCodeGeneratorB(ctx, path, name, List.of("GRPC_un_un_server","GRPC_un_un_client"));
        generator.generateCode();
        assertTrue(generator.necessaryFrameworks.containsAll(List.of(
                "GRPC_un_un_server",
                "GRPC_un_un_client")));
        assertTrue(Files.exists(Path.of(path,name,"client","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"server","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"client","pom.xml")));
        assertTrue(Files.exists(Path.of(path,name,"server","pom.xml")));
    }

    @Test
    public void finiteGRPC_un_unRecursive() throws IOException {
        var ctx = testFile("finite_recursion.sp").compilerCtx;
        var generator = new SPCodeGeneratorB(ctx, path, name, List.of("GRPC_un_un_server","GRPC_un_un_client"));
        generator.generateCode();
        assertTrue(generator.necessaryFrameworks.containsAll(List.of(
                "GRPC_un_un_server",
                "GRPC_un_un_client")));
        assertTrue(Files.exists(Path.of(path,name,"client","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"server","src", "main","proto", "GrpcService.proto")));
        assertTrue(Files.exists(Path.of(path,name,"client","pom.xml")));
        assertTrue(Files.exists(Path.of(path,name,"server","pom.xml")));

    }
}