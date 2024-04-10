package mychor;

import org.antlr.v4.runtime.ParserRuleContext;

public class Utils {
    public enum Direction{
        SEND,
        RECEIVE,
        SELECT,
        BRANCH,
        VOID
    }

    static String ERROR_RECVAR_ADD(String key, String boundProcess, String newProcess, ParserRuleContext ctx){
        return ERROR_DEFAULT(String.format(
                "Procedure %s is already bound to process %s, can't bind it to %s",
                key,
                boundProcess,
                newProcess), ctx);
    }

    static String ERROR_RECVAR_UNKNOWN(String vname, ParserRuleContext ctx){
        return ERROR_DEFAULT(String.format(
                "No recursive variable with the name %s exists",
                vname
        ), ctx);
    }

    static String ERROR_DEFAULT(String error, ParserRuleContext ctx){
        return String.format("%s:\n\t %s %s:%s", error,
                ctx.getText(), ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    }

    //You can get the first token in the rule with ctx.start or ctx.getStart(). Then use getLine() on the token to get the line number (and getCharPositionInLine() to get the column).

    static String ERROR_NULL_PROCESS(SPparserRich.BehaviourContext ctx){
        return ERROR_DEFAULT("Current Process can't be null in a communication",ctx);
    }
}
