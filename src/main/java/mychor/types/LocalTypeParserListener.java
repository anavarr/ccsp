// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/LocalTypeParser.g4 by ANTLR 4.13.2
package mychor.types;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LocalTypeParser}.
 */
public interface LocalTypeParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code EndType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void enterEndType(LocalTypeParser.EndTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EndType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void exitEndType(LocalTypeParser.EndTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CallType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void enterCallType(LocalTypeParser.CallTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CallType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void exitCallType(LocalTypeParser.CallTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SendType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void enterSendType(LocalTypeParser.SendTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SendType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void exitSendType(LocalTypeParser.SendTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReceiveType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void enterReceiveType(LocalTypeParser.ReceiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReceiveType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void exitReceiveType(LocalTypeParser.ReceiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SelectType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void enterSelectType(LocalTypeParser.SelectTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SelectType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void exitSelectType(LocalTypeParser.SelectTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BranchType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void enterBranchType(LocalTypeParser.BranchTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BranchType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void exitBranchType(LocalTypeParser.BranchTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RecDef}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void enterRecDef(LocalTypeParser.RecDefContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RecDef}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 */
	void exitRecDef(LocalTypeParser.RecDefContext ctx);
}