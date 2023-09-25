// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/SPparser.g4 by ANTLR 4.12.0
package mychor;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SPparser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SPparserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SPparser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(SPparser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparser#network}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNetwork(SPparser.NetworkContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparser#recdef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecdef(SPparser.RecdefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Snd}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSnd(SPparser.SndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Rcv}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRcv(SPparser.RcvContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Sel}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSel(SPparser.SelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BraAnn}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBraAnn(SPparser.BraAnnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Bra}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBra(SPparser.BraContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Cdt}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCdt(SPparser.CdtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CalAnn}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCalAnn(SPparser.CalAnnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Cal}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCal(SPparser.CalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code End}
	 * labeled alternative in {@link SPparser#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(SPparser.EndContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparser#mBehaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMBehaviour(SPparser.MBehaviourContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparser#proc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProc(SPparser.ProcContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(SPparser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparser#ann}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnn(SPparser.AnnContext ctx);
}