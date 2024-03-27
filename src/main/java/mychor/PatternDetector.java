package mychor;

import java.util.ArrayList;
import java.util.List;

public class PatternDetector {

    CompilerContext ctx;

    List<Session> patterns = new ArrayList<>();



    public PatternDetector(CompilerContext ctx){
        this.ctx = ctx;
    }

    public List<String> detectCompatibleFrameworks() {
        for (Session pattern : patterns) {
        }
        return null;
    }
}
