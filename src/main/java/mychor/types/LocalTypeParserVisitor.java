// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/LocalTypeParser.g4 by ANTLR 4.13.2
package mychor.types;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LocalTypeParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LocalTypeParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by the {@code EndType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndType(LocalTypeParser.EndTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CallType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallType(LocalTypeParser.CallTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SendType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSendType(LocalTypeParser.SendTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReceiveType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReceiveType(LocalTypeParser.ReceiveTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SelectType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectType(LocalTypeParser.SelectTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BranchType}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBranchType(LocalTypeParser.BranchTypeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RecDef}
	 * labeled alternative in {@link LocalTypeParser#localtype}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecDef(LocalTypeParser.RecDefContext ctx);
}