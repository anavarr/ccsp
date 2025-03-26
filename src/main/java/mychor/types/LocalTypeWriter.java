package mychor.types;

import mychor.End;
import org.antlr.v4.runtime.TokenStream;

import java.util.HashMap;

public class LocalTypeWriter extends LocalTypeParserBaseVisitor<LocalType>{


    private static HashMap<String, LocalType> recursionDef = new HashMap<String, LocalType>();

    @Override
    public LocalType visitEndType(LocalTypeParser.EndTypeContext ctx) {
        return new EndType();
    }

    @Override
    public LocalType visitCallType(LocalTypeParser.CallTypeContext ctx) {
        return new RecurseCallType(ctx.getText(), recursionDef.get(ctx.getText()));
    }

    @Override
    public LocalType visitRecDef(LocalTypeParser.RecDefContext ctx) {
        // 'µ' 0
        // IDENTIFIER 1
        // '.' 2
        // localtype 3
        var lt = new RecurseDefType(ctx.getChild(1).getText(), ctx.getChild(3).accept(this));
        recursionDef.put(ctx.getChild(1).getText(), lt);
        return lt;
    }

    @Override
    public LocalType visitSendType(LocalTypeParser.SendTypeContext ctx) {
        // IDENTIFIER
        // '!'
        // ';'
        // localtype
        return new SendType(ctx.getChild(0).getText(), ctx.getChild(3).accept(this));
    }

    @Override
    public LocalType visitReceiveType(LocalTypeParser.ReceiveTypeContext ctx) {
        // IDENTIFIER
        // '?'
        // ';'
        // localtype
        return new ReceiveType(ctx.getChild(0).getText(), ctx.getChild(3).accept(this));
    }

    @Override
    public LocalType visitBranchType(LocalTypeParser.BranchTypeContext ctx) {
        // IDENTIFIER       0
        // '&'              1
        // '{'              2
        // BLABEL           3
        // ':'              4
        // localtype        5
        // (','             6+4i
        // BLABEL           7+4i
        // ':'              8+4i
        // localtype        9+4i)*
        // '}'
        HashMap<String, LocalType> nextTypes = new HashMap<>();
        nextTypes.put(ctx.getChild(3).getText(), ctx.getChild(5).accept(this));
        var i = 0;
        while(7+4*i < ctx.getChildCount()){
            nextTypes.put(ctx.getChild(7+4*i).getText(), ctx.getChild(9+4*i).accept(this));
            i++;
        }
        return new BranchType(ctx.getChild(0).getText(), nextTypes);
    }

    @Override
    public LocalType visitSelectType(LocalTypeParser.SelectTypeContext ctx) {
        // IDENTIFIER       0
        // '+'              1
        // '{'              2
        // BLABEL           3
        // ':'              4
        // localtype        5
        // (','             6+4i
        // BLABEL           7+4i
        // ':'              8+4i
        // localtype        9+4i)*
        // '}'
        HashMap<String, LocalType> nextTypes = new HashMap<>();
        nextTypes.put(ctx.getChild(3).getText(), ctx.getChild(5).accept(this));
        var i = 0;
        while(7+4*i < ctx.getChildCount()){
            nextTypes.put(ctx.getChild(7+4*i).getText(), ctx.getChild(9+4*i).accept(this));
            i++;
        }
        return new SelectType(ctx.getChild(0).getText(), nextTypes);
    }
}
