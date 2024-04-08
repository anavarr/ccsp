package mychor;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatternDetector {

    CompilerContext ctx;

    HashMap<String, Session> patterns = new HashMap<>();


    public PatternDetector(){
        var path = Path.of("src", "main", "messaging-patterns", "all_patterns.txt");
        MessagePatternLexer spl = null;
        try {
            spl = new MessagePatternLexer(CharStreams.fromPath(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var spp = new MessagePatternParser(new CommonTokenStream(spl));
        var mpm = new MessagePatternMaker();
        spp.pattern().accept(mpm);
        patterns = mpm.getSessionsMap();
    }

    public PatternDetector(CompilerContext ctx){
        this();
        this.ctx = ctx;
    }

    public boolean testCompatibility(Session target, Session template){
        return template.supports(target);
    }

    public HashMap<Session, List<String>> detectCompatibleFrameworks() {
        var compatibleFrameworks = new HashMap<Session, List<String>>();
        for (Session session : ctx.sessions) {
            compatibleFrameworks.put(session, new ArrayList<>());
            for (String name : patterns.keySet()) {
                if(session.areEnds("alice", "store") && name.equals("GRPC_st_st_client")){
                    System.out.println(session);
                    System.out.println("======");
                    System.out.println(patterns.get(name));
                }
                if(testCompatibility(session, patterns.get(name))) compatibleFrameworks.get(session).add(name);
            }
        }
        return compatibleFrameworks;
    }
}
