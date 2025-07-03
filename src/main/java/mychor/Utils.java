package mychor;

import com.ibm.icu.impl.coll.Collation;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Utils {
    public enum Direction{
        SEND,
        RECEIVE,
        SELECT,
        BRANCH,
        VOID,
        DUMMY
    }

    public static List<String> carthesianProduct(Collection<String> strs){
        var rs = new ArrayList<String>();
        for (String str : strs) {
            for (String s : strs) {
                if(!s.equals(str)) rs.add(str+"-"+s);
            }
        }
        return rs;
    }

    public static String capitalize(String str){
        return str.substring(0,1).toUpperCase()+str.substring(1);
    }
    public static String minimize(String str){
        return str.substring(0,1).toLowerCase()+str.substring(1);
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
                "No recursive variable with the serviceName %s exists",
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

    public static double[] quartiles(ArrayList<Integer> val) {
        double ans[] = new double[3];

        for (int quartileType = 1; quartileType < 4; quartileType++) {
            float length = val.size() + 1;
            double quartile;
            float newArraySize = (length * ((float) (quartileType) * 25 / 100)) - 1;
            val.sort(null);
            if (newArraySize % 1 == 0) {
                quartile = val.get((int) (newArraySize));
            } else {
                int newArraySize1 = (int) (newArraySize);
                quartile = (double) (val.get(newArraySize1) + val.get(newArraySize1 + 1)) / 2;
            }
            ans[quartileType - 1] = quartile;
        }
        return ans;
    }

    public static double stdev(ArrayList<Integer> list){
        double sum = 0.0;
        double mean = 0.0;
        double num=0.0;
        double numi = 0.0;
        double deno = 0.0;

        for (int i : list) {
            sum+=i;
        }
        mean = sum/list.size();

        for (int i : list) {
            numi = Math.pow((double)(i - mean), 2);
            num+=numi;
        }

        return Math.sqrt(num/list.size());
    }
}
