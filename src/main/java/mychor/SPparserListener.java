// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/SPparser.g4 by ANTLR 4.12.0
package mychor;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SPparser}.
 */
public interface SPparserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SPparser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(SPparser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(SPparser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparser#network}.
	 * @param ctx the parse tree
	 */
	void enterNetwork(SPparser.NetworkContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparser#network}.
	 * @param ctx the parse tree
	 */
	void exitNetwork(SPparser.NetworkContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparser#recdef}.
	 * @param ctx the parse tree
	 */
	void enterRecdef(SPparser.RecdefContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparser#recdef}.
	 * @param ctx the parse tree
	 */
	void exitRecdef(SPparser.RecdefContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Snd}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterSnd(SPparser.SndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Snd}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitSnd(SPparser.SndContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Rcv}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterRcv(SPparser.RcvContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Rcv}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitRcv(SPparser.RcvContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Sel}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterSel(SPparser.SelContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Sel}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitSel(SPparser.SelContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BraAnn}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterBraAnn(SPparser.BraAnnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BraAnn}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitBraAnn(SPparser.BraAnnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Bra}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterBra(SPparser.BraContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Bra}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitBra(SPparser.BraContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Cdt}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterCdt(SPparser.CdtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Cdt}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitCdt(SPparser.CdtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CalAnn}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterCalAnn(SPparser.CalAnnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CalAnn}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitCalAnn(SPparser.CalAnnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Cal}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterCal(SPparser.CalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Cal}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitCal(SPparser.CalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code End}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void enterEnd(SPparser.EndContext ctx);
	/**
	 * Exit a parse tree produced by the {@code End}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 */
	void exitEnd(SPparser.EndContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparser#mBehaviour}.
	 * @param ctx the parse tree
	 */
	void enterMBehaviour(SPparser.MBehaviourContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparser#mBehaviour}.
	 * @param ctx the parse tree
	 */
	void exitMBehaviour(SPparser.MBehaviourContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparser#proc}.
	 * @param ctx the parse tree
	 */
	void enterProc(SPparser.ProcContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparser#proc}.
	 * @param ctx the parse tree
	 */
	void exitProc(SPparser.ProcContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(SPparser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(SPparser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link SPparser#ann}.
	 * @param ctx the parse tree
	 */
	void enterAnn(SPparser.AnnContext ctx);
	/**
	 * Exit a parse tree produced by {@link SPparser#ann}.
	 * @param ctx the parse tree
	 */
	void exitAnn(SPparser.AnnContext ctx);
}