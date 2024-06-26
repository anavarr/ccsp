package mychor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import mychor.Generators.GenerationConfig;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatternDetector {

    CompilerContext ctx;

    HashMap<String, Session> patterns = new HashMap<>();

    List<String> patternTemplates;

    public PatternDetector() {
        var path = Path.of("src", "main","resources", "messaging-patterns", "patterns.json");
        try {
            JsonNode parent= new ObjectMapper().readTree(Files.readString(path));
            ArrayList<String> patternsString = new ArrayList<>();
            MessagePatternLexer spl = null;
            parent.elements().forEachRemaining(node -> {
                patternsString.add(node.get("name").asText()+'"'+node.get("pattern").asText()+'"');
            });
            spl = new MessagePatternLexer(CharStreams.fromString(String.join("\n", patternsString)));
            var spp = new MessagePatternParser(new CommonTokenStream(spl));
            var mpm = new MessagePatternMaker();
            spp.pattern().accept(mpm);
            patterns = mpm.getSessionsMap();
            for (Session session : patterns.values()) {
                session.cleanVoid();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public PatternDetector(CompilerContext ctx){
        this();
        this.ctx = ctx;
    }

    public Session getPattern(String name){
        return patterns.get(name);
    }

    public boolean testCompatibility(Session target, Session template){
        return template.supports(target);
    }

    public HashMap<Session, List<String>> detectCompatibleFrameworks() {
        var compatibleFrameworks = new HashMap<Session, List<String>>();
        for (Session session : ctx.sessions) {
            compatibleFrameworks.put(session, new ArrayList<>());
            for (String name : patterns.keySet()) {
                if(testCompatibility(session, patterns.get(name))) compatibleFrameworks.get(session).add(name);
            }
        }
        return compatibleFrameworks;
    }
}
