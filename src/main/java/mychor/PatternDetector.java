package mychor;

import java.util.List;

public class PatternDetector {

    CompilerContext ctx;

    List<Session> patterns;

    static final String REST_PATTERN =
            "client -> server;" +
            "server -> client";

    static final String REACTIVE_MESSAGING =
            "subscriber -> producer ; " +
            "producer -> subscriber; " +
            "(subscriber -> producer | producer -> subscriber)*";

    static final String gRPC_un_un =
            "client -> server;" +
            "server -> client";
    static final String gRPC_un_st =
            "client -> server;" +
            "(server -> client)+";
    static final String gRPC_st_un =
            "(client -> server)+;" +
            "server -> client";
    static final String gRPC_st_st =
            "client -> server ;" +
            "( client -> server | server -> client )*";


    public PatternDetector(CompilerContext ctx){
        this.ctx = ctx;
    }

    public List<String> detectCompatibleFrameworks() {
        for (Session pattern : patterns) {
        }
        return null;
    }
}
