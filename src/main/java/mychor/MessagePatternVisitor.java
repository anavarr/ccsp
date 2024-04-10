// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/MessagePattern.g4 by ANTLR 4.13.1
package mychor;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MessagePatternParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MessagePatternVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MessagePatternParser#pattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern(MessagePatternParser.PatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link MessagePatternParser#pattern_single}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern_single(MessagePatternParser.Pattern_singleContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Choice}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitChoice(MessagePatternParser.ChoiceContext ctx);
	/**
	 * Visit a parse tree produced by the {@code SingleExchange}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleExchange(MessagePatternParser.SingleExchangeContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Sequent}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSequent(MessagePatternParser.SequentContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Repeat}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeat(MessagePatternParser.RepeatContext ctx);
	/**
	 * Visit a parse tree produced by {@link MessagePatternParser#exchange}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExchange(MessagePatternParser.ExchangeContext ctx);
}