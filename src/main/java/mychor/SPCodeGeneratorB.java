package mychor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SPCodeGeneratorB {
    CompilerContext ctx;
    String outPath;
    String name;
    public SPCodeGeneratorB(CompilerContext ctx, String path, String name){
        this.ctx=ctx;
        this.outPath = path;
        this.name = name;
        try{
            Files.createDirectories(Paths.get(outPath+"/"+name));
        } catch (IOException e) {
            System.err.println("Problem while creating the distributed application folder");
        }
    }

    public void generateCode(){
        for (String s : ctx.behaviours.keySet()) {
            createMicroService(s);
        }
    }

    public void createMicroService(String pa){
        try{
            Files.createDirectories(Paths.get(outPath+"/"+name+"/"+pa));
        } catch (IOException e) {
            System.err.println("Problem while creating the folder for µservice "+pa);
        }
    }
}
