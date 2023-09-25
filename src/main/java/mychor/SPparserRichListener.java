// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/SPparserRich.g4 by ANTLR 4.12.0
package mychor;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SPparserRich}.
 */
public interface SPparserRichListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SPparserRich#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(SPparserRich.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparserRich#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(SPparserRich.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparserRich#network}.
	 * @param ctx the parse tree
	 */
	void enterNetwork(SPparserRich.NetworkContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparserRich#network}.
	 * @param ctx the parse tree
	 */
	void exitNetwork(SPparserRich.NetworkContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparserRich#recdef}.
	 * @param ctx the parse tree
	 */
	void enterRecdef(SPparserRich.RecdefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparserRich#recdef}.
	 * @param ctx the parse tree
	 */
	void exitRecdef(SPparserRich.RecdefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Snd}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterSnd(SPparserRich.SndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Snd}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitSnd(SPparserRich.SndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Rcv}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterRcv(SPparserRich.RcvContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Rcv}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitRcv(SPparserRich.RcvContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Sel}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterSel(SPparserRich.SelContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Sel}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitSel(SPparserRich.SelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BraAnn}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterBraAnn(SPparserRich.BraAnnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BraAnn}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitBraAnn(SPparserRich.BraAnnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Bra}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterBra(SPparserRich.BraContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Bra}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitBra(SPparserRich.BraContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Cdt}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterCdt(SPparserRich.CdtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Cdt}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitCdt(SPparserRich.CdtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CalAnn}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterCalAnn(SPparserRich.CalAnnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CalAnn}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitCalAnn(SPparserRich.CalAnnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Cal}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterCal(SPparserRich.CalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Cal}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitCal(SPparserRich.CalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code End}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterEnd(SPparserRich.EndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code End}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitEnd(SPparserRich.EndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Non}
	 * labeled alternative in {@link SPparserRich#mBehaviour}.
	 * @param ctx the parse tree
	 */
	void enterNon(SPparserRich.NonContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Non}
	 * labeled alternative in {@link SPparserRich#mBehaviour}.
	 * @param ctx the parse tree
	 */
	void exitNon(SPparserRich.NonContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Som}
	 * labeled alternative in {@link SPparserRich#mBehaviour}.
	 * @param ctx the parse tree
	 */
	void enterSom(SPparserRich.SomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Som}
	 * labeled alternative in {@link SPparserRich#mBehaviour}.
	 * @param ctx the parse tree
	 */
	void exitSom(SPparserRich.SomContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparserRich#proc}.
	 * @param ctx the parse tree
	 */
	void enterProc(SPparserRich.ProcContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparserRich#proc}.
	 * @param ctx the parse tree
	 */
	void exitProc(SPparserRich.ProcContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparserRich#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(SPparserRich.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparserRich#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(SPparserRich.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparserRich#ann}.
	 * @param ctx the parse tree
	 */
	void enterAnn(SPparserRich.AnnContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparserRich#ann}.
	 * @param ctx the parse tree
	 */
	void exitAnn(SPparserRich.AnnContext ctx);
}