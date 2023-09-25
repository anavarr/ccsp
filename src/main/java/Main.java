import mychor.SPChecker;
import mychor.SPCheckerRich;
import mychor.SPCompiler;
import mychor.SPCompiler2;
import mychor.SPlexer;
import mychor.SPparser;
import mychor.SPparserRich;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args){

        var input_path = Path.of("/home/arnavarr/Documents/thesis/prog/antlr4/" +
                "ccsp/src/test/antlr4/SP_programs/rich_program_2.sp");
        var output_path = Path.of("/home/arnavarr/Documents/thesis/prog/antlr4/" +
                "ccsp/src/main/resources");

        try{
            CharStream cs = CharStreams.fromPath(input_path);
            SPlexer spl = new SPlexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(spl);
            var spp = new SPparserRich(tokens);
            verifyRichChorProgramSemantics(spp);
            spp.reset();
            compileToQuarkusServices(spp);
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
    }

    public static void verifyChorProgramSemantics(SPparser spp){
        var spc = new SPChecker();
        var r = spp.program().accept(spc);
        System.out.println(spc.processCommunicationsMap());
    }

    public static void verifyRichChorProgramSemantics(SPparserRich spp){
        var spc = new SPCheckerRich();
        var r = spp.program().accept(spc);
        System.out.println("Your program is well-formed : "+spc.noSelfCom());
        System.out.println("Your program contains those unknown processes : " + spc.unknownProcesses());
        System.out.println("Your program contains those unknown recursive variables : " + spc.unknownVariables());
    }

    public static void compileToQuarkusServices(SPparserRich spp){
        var spc = new SPCompiler2();
        spc.setOutputPath("/tmp/myDistributedApplication");
        spp.program().accept(spc);
    }
}
