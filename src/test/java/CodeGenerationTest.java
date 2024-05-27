import mychor.SPCodeGeneratorB;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodeGenerationTest extends ProgramReaderTest{
    String path = "/tmp";
    String name = "MyQuarkusDistributedApplication";
    @Test
    public void generatingMicroServiceFromEmptyFileShouldCreateEmptyFolder() throws IOException {
        var ctx = testFile("empty.sp").compilerCtx;
        new SPCodeGeneratorB(ctx, path, name);
        assertTrue(Files.exists(Path.of(path, name)));
    }

    @Test
    public void simpleClientServerApplicationShouldCreateClientMicroserviceAndServerMicroservice() throws IOException {
        var ctx = testFile("program_1.sp").compilerCtx;
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
        for (SPCodeGeneratorB.GenerationContext gc : generator.gcs) {
            assertTrue(gc.functions.isEmpty());
            assertFalse(gc.code.isEmpty());
        }
    }
}
