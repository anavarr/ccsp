// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/MessagePattern.g4 by ANTLR 4.13.1
package mychor;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MessagePatternParser}.
 */
public interface MessagePatternListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MessagePatternParser#pattern}.
	 * @param ctx the parse tree
	 */
	void enterPattern(MessagePatternParser.PatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link MessagePatternParser#pattern}.
	 * @param ctx the parse tree
	 */
	void exitPattern(MessagePatternParser.PatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link MessagePatternParser#pattern_single}.
	 * @param ctx the parse tree
	 */
	void enterPattern_single(MessagePatternParser.Pattern_singleContext ctx);
	/**
	 * Exit a parse tree produced by {@link MessagePatternParser#pattern_single}.
	 * @param ctx the parse tree
	 */
	void exitPattern_single(MessagePatternParser.Pattern_singleContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Choice}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterChoice(MessagePatternParser.ChoiceContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Choice}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitChoice(MessagePatternParser.ChoiceContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SingleExchange}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSingleExchange(MessagePatternParser.SingleExchangeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SingleExchange}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSingleExchange(MessagePatternParser.SingleExchangeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Sequent}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSequent(MessagePatternParser.SequentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Sequent}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSequent(MessagePatternParser.SequentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Repeat}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterRepeat(MessagePatternParser.RepeatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Repeat}
	 * labeled alternative in {@link MessagePatternParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitRepeat(MessagePatternParser.RepeatContext ctx);
	/**
	 * Enter a parse tree produced by {@link MessagePatternParser#exchange}.
	 * @param ctx the parse tree
	 */
	void enterExchange(MessagePatternParser.ExchangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MessagePatternParser#exchange}.
	 * @param ctx the parse tree
	 */
	void exitExchange(MessagePatternParser.ExchangeContext ctx);
}