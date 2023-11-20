import mychor.SPcheckerRich;
import mychor.SPcodeGenerator;
import mychor.SPlexer;
import mychor.SPparserRich;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args){

        String inputPrefix = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/test/antlr4";
        var inputPath = Path.of(inputPrefix, "SP_programs/IP_protocol.sp");

        try{
            CharStream cs = CharStreams.fromPath(inputPath);
            SPlexer spl = new SPlexer(cs);
            CommonTokenStream tokens = new CommonTokenStream(spl);
            var spp = new SPparserRich(tokens);
            verifyRichChorProgramSemantics(spp);
            spp.reset();
//            compileToQuarkusServices(spp);
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
    }

    public static void verifyRichChorProgramSemantics(SPparserRich spp){
        var spc = new SPcheckerRich();
        spp.program().accept(spc);
        spc.displayContext();
        spc.displayComplementarySessions();
        System.out.println("Your program has no self communications : "+spc.noSelfCom());
        System.out.println("Your program contains those unknown processes : " + spc.unknownProcesses());
        System.out.println("Your program contains those unknown recursive variables : " + spc.unknownVariables());
        var nonComplementarySessions = spc.getNonComplementarySessions();
    }

    public static void compileToQuarkusServices(SPparserRich spp){
        var spc = new SPcodeGenerator();
        spc.setOutputPath("/tmp/myDistributedApplication");
        spp.program().accept(spc);
    }
}
