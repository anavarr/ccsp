// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/SPparserRich.g4 by ANTLR 4.12.0
package mychor;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SPparserRich}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SPparserRichVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SPparserRich#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(SPparserRich.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparserRich#network}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNetwork(SPparserRich.NetworkContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparserRich#recdef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecdef(SPparserRich.RecdefContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Snd}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSnd(SPparserRich.SndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Rcv}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRcv(SPparserRich.RcvContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Sel}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSel(SPparserRich.SelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BraAnn}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBraAnn(SPparserRich.BraAnnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Bra}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBra(SPparserRich.BraContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Cdt}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCdt(SPparserRich.CdtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CalAnn}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCalAnn(SPparserRich.CalAnnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Cal}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCal(SPparserRich.CalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code End}
	 * labeled alternative in {@link SPparserRich#behaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnd(SPparserRich.EndContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Non}
	 * labeled alternative in {@link SPparserRich#mBehaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNon(SPparserRich.NonContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Som}
	 * labeled alternative in {@link SPparserRich#mBehaviour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSom(SPparserRich.SomContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparserRich#proc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProc(SPparserRich.ProcContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparserRich#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(SPparserRich.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SPparserRich#ann}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnn(SPparserRich.AnnContext ctx);
}