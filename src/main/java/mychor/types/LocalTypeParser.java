// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/LocalTypeParser.g4 by ANTLR 4.13.2
package mychor.types;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class LocalTypeParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ENDLT=1, MU=2, COL=3, DOUBLECOL=4, AT=5, EMARK=6, IMARK=7, SEQ=8, QUOTES=9, 
		DOT=10, PLUS=11, AND=12, BRANCH=13, LPAR=14, RPAR=15, SEL=16, SQLPAR=17, 
		SQRPAR=18, CLPAR=19, CRPAR=20, PAR=21, IF=22, THEN=23, ELSE=24, CALL=25, 
		NONE=26, SOME=27, END=28, LABEL=29, IDENTIFIER=30, BLABEL=31, WS=32, SP=33, 
		COMMENT=34;
	public static final int
		RULE_localtype = 0;
	private static String[] makeRuleNames() {
		return new String[] {
			"localtype"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'end'", "'\\u00B5'", "','", "':'", "'@'", "'!'", "'?'", "';'", 
			"'\"'", "'.'", "'+'", "'&'", "'//'", "'('", "')'", "'(+)'", "'['", "']'", 
			"'{'", "'}'", "'|'", "'If'", "'Then'", "'Else'", "'Call'", "'None'", 
			"'Some'", "'End'", null, null, null, null, "' '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ENDLT", "MU", "COL", "DOUBLECOL", "AT", "EMARK", "IMARK", "SEQ", 
			"QUOTES", "DOT", "PLUS", "AND", "BRANCH", "LPAR", "RPAR", "SEL", "SQLPAR", 
			"SQRPAR", "CLPAR", "CRPAR", "PAR", "IF", "THEN", "ELSE", "CALL", "NONE", 
			"SOME", "END", "LABEL", "IDENTIFIER", "BLABEL", "WS", "SP", "COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "LocalTypeParser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LocalTypeParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LocaltypeContext extends ParserRuleContext {
		public LocaltypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_localtype; }
	 
		public LocaltypeContext() { }
		public void copyFrom(LocaltypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReceiveTypeContext extends LocaltypeContext {
		public TerminalNode IDENTIFIER() { return getToken(LocalTypeParser.IDENTIFIER, 0); }
		public TerminalNode IMARK() { return getToken(LocalTypeParser.IMARK, 0); }
		public TerminalNode SEQ() { return getToken(LocalTypeParser.SEQ, 0); }
		public LocaltypeContext localtype() {
			return getRuleContext(LocaltypeContext.class,0);
		}
		public ReceiveTypeContext(LocaltypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).enterReceiveType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).exitReceiveType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LocalTypeParserVisitor ) return ((LocalTypeParserVisitor<? extends T>)visitor).visitReceiveType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BranchTypeContext extends LocaltypeContext {
		public TerminalNode IDENTIFIER() { return getToken(LocalTypeParser.IDENTIFIER, 0); }
		public TerminalNode AND() { return getToken(LocalTypeParser.AND, 0); }
		public TerminalNode CLPAR() { return getToken(LocalTypeParser.CLPAR, 0); }
		public List<TerminalNode> BLABEL() { return getTokens(LocalTypeParser.BLABEL); }
		public TerminalNode BLABEL(int i) {
			return getToken(LocalTypeParser.BLABEL, i);
		}
		public List<TerminalNode> DOUBLECOL() { return getTokens(LocalTypeParser.DOUBLECOL); }
		public TerminalNode DOUBLECOL(int i) {
			return getToken(LocalTypeParser.DOUBLECOL, i);
		}
		public List<LocaltypeContext> localtype() {
			return getRuleContexts(LocaltypeContext.class);
		}
		public LocaltypeContext localtype(int i) {
			return getRuleContext(LocaltypeContext.class,i);
		}
		public TerminalNode CRPAR() { return getToken(LocalTypeParser.CRPAR, 0); }
		public List<TerminalNode> COL() { return getTokens(LocalTypeParser.COL); }
		public TerminalNode COL(int i) {
			return getToken(LocalTypeParser.COL, i);
		}
		public BranchTypeContext(LocaltypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).enterBranchType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).exitBranchType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LocalTypeParserVisitor ) return ((LocalTypeParserVisitor<? extends T>)visitor).visitBranchType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SendTypeContext extends LocaltypeContext {
		public TerminalNode IDENTIFIER() { return getToken(LocalTypeParser.IDENTIFIER, 0); }
		public TerminalNode EMARK() { return getToken(LocalTypeParser.EMARK, 0); }
		public TerminalNode SEQ() { return getToken(LocalTypeParser.SEQ, 0); }
		public LocaltypeContext localtype() {
			return getRuleContext(LocaltypeContext.class,0);
		}
		public SendTypeContext(LocaltypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).enterSendType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).exitSendType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LocalTypeParserVisitor ) return ((LocalTypeParserVisitor<? extends T>)visitor).visitSendType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CallTypeContext extends LocaltypeContext {
		public TerminalNode LABEL() { return getToken(LocalTypeParser.LABEL, 0); }
		public CallTypeContext(LocaltypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).enterCallType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).exitCallType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LocalTypeParserVisitor ) return ((LocalTypeParserVisitor<? extends T>)visitor).visitCallType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SelectTypeContext extends LocaltypeContext {
		public TerminalNode IDENTIFIER() { return getToken(LocalTypeParser.IDENTIFIER, 0); }
		public TerminalNode PLUS() { return getToken(LocalTypeParser.PLUS, 0); }
		public TerminalNode CLPAR() { return getToken(LocalTypeParser.CLPAR, 0); }
		public List<TerminalNode> BLABEL() { return getTokens(LocalTypeParser.BLABEL); }
		public TerminalNode BLABEL(int i) {
			return getToken(LocalTypeParser.BLABEL, i);
		}
		public List<TerminalNode> DOUBLECOL() { return getTokens(LocalTypeParser.DOUBLECOL); }
		public TerminalNode DOUBLECOL(int i) {
			return getToken(LocalTypeParser.DOUBLECOL, i);
		}
		public List<LocaltypeContext> localtype() {
			return getRuleContexts(LocaltypeContext.class);
		}
		public LocaltypeContext localtype(int i) {
			return getRuleContext(LocaltypeContext.class,i);
		}
		public TerminalNode CRPAR() { return getToken(LocalTypeParser.CRPAR, 0); }
		public List<TerminalNode> COL() { return getTokens(LocalTypeParser.COL); }
		public TerminalNode COL(int i) {
			return getToken(LocalTypeParser.COL, i);
		}
		public SelectTypeContext(LocaltypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).enterSelectType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).exitSelectType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LocalTypeParserVisitor ) return ((LocalTypeParserVisitor<? extends T>)visitor).visitSelectType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class EndTypeContext extends LocaltypeContext {
		public TerminalNode ENDLT() { return getToken(LocalTypeParser.ENDLT, 0); }
		public EndTypeContext(LocaltypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).enterEndType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).exitEndType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LocalTypeParserVisitor ) return ((LocalTypeParserVisitor<? extends T>)visitor).visitEndType(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RecDefContext extends LocaltypeContext {
		public TerminalNode MU() { return getToken(LocalTypeParser.MU, 0); }
		public TerminalNode LABEL() { return getToken(LocalTypeParser.LABEL, 0); }
		public TerminalNode DOT() { return getToken(LocalTypeParser.DOT, 0); }
		public LocaltypeContext localtype() {
			return getRuleContext(LocaltypeContext.class,0);
		}
		public RecDefContext(LocaltypeContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).enterRecDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LocalTypeParserListener ) ((LocalTypeParserListener)listener).exitRecDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LocalTypeParserVisitor ) return ((LocalTypeParserVisitor<? extends T>)visitor).visitRecDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LocaltypeContext localtype() throws RecognitionException {
		LocaltypeContext _localctx = new LocaltypeContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_localtype);
		int _la;
		try {
			setState(50);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				_localctx = new EndTypeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(2);
				match(ENDLT);
				}
				break;
			case 2:
				_localctx = new CallTypeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(3);
				match(LABEL);
				}
				break;
			case 3:
				_localctx = new SendTypeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(4);
				match(IDENTIFIER);
				setState(5);
				match(EMARK);
				setState(6);
				match(SEQ);
				setState(7);
				localtype();
				}
				break;
			case 4:
				_localctx = new ReceiveTypeContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(8);
				match(IDENTIFIER);
				setState(9);
				match(IMARK);
				setState(10);
				match(SEQ);
				setState(11);
				localtype();
				}
				break;
			case 5:
				_localctx = new SelectTypeContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(12);
				match(IDENTIFIER);
				setState(13);
				match(PLUS);
				setState(14);
				match(CLPAR);
				setState(15);
				match(BLABEL);
				setState(16);
				match(DOUBLECOL);
				setState(17);
				localtype();
				setState(24);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COL) {
					{
					{
					setState(18);
					match(COL);
					setState(19);
					match(BLABEL);
					setState(20);
					match(DOUBLECOL);
					setState(21);
					localtype();
					}
					}
					setState(26);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(27);
				match(CRPAR);
				}
				break;
			case 6:
				_localctx = new BranchTypeContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(29);
				match(IDENTIFIER);
				setState(30);
				match(AND);
				setState(31);
				match(CLPAR);
				setState(32);
				match(BLABEL);
				setState(33);
				match(DOUBLECOL);
				setState(34);
				localtype();
				setState(41);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COL) {
					{
					{
					setState(35);
					match(COL);
					setState(36);
					match(BLABEL);
					setState(37);
					match(DOUBLECOL);
					setState(38);
					localtype();
					}
					}
					setState(43);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(44);
				match(CRPAR);
				}
				break;
			case 7:
				_localctx = new RecDefContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(46);
				match(MU);
				setState(47);
				match(LABEL);
				setState(48);
				match(DOT);
				setState(49);
				localtype();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\"5\u0002\u0000\u0007\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0005"+
		"\u0000\u0017\b\u0000\n\u0000\f\u0000\u001a\t\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000(\b\u0000"+
		"\n\u0000\f\u0000+\t\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0003\u00003\b\u0000\u0001\u0000\u0000\u0000"+
		"\u0001\u0000\u0000\u0000;\u00002\u0001\u0000\u0000\u0000\u00023\u0005"+
		"\u0001\u0000\u0000\u00033\u0005\u001d\u0000\u0000\u0004\u0005\u0005\u001e"+
		"\u0000\u0000\u0005\u0006\u0005\u0006\u0000\u0000\u0006\u0007\u0005\b\u0000"+
		"\u0000\u00073\u0003\u0000\u0000\u0000\b\t\u0005\u001e\u0000\u0000\t\n"+
		"\u0005\u0007\u0000\u0000\n\u000b\u0005\b\u0000\u0000\u000b3\u0003\u0000"+
		"\u0000\u0000\f\r\u0005\u001e\u0000\u0000\r\u000e\u0005\u000b\u0000\u0000"+
		"\u000e\u000f\u0005\u0013\u0000\u0000\u000f\u0010\u0005\u001f\u0000\u0000"+
		"\u0010\u0011\u0005\u0004\u0000\u0000\u0011\u0018\u0003\u0000\u0000\u0000"+
		"\u0012\u0013\u0005\u0003\u0000\u0000\u0013\u0014\u0005\u001f\u0000\u0000"+
		"\u0014\u0015\u0005\u0004\u0000\u0000\u0015\u0017\u0003\u0000\u0000\u0000"+
		"\u0016\u0012\u0001\u0000\u0000\u0000\u0017\u001a\u0001\u0000\u0000\u0000"+
		"\u0018\u0016\u0001\u0000\u0000\u0000\u0018\u0019\u0001\u0000\u0000\u0000"+
		"\u0019\u001b\u0001\u0000\u0000\u0000\u001a\u0018\u0001\u0000\u0000\u0000"+
		"\u001b\u001c\u0005\u0014\u0000\u0000\u001c3\u0001\u0000\u0000\u0000\u001d"+
		"\u001e\u0005\u001e\u0000\u0000\u001e\u001f\u0005\f\u0000\u0000\u001f "+
		"\u0005\u0013\u0000\u0000 !\u0005\u001f\u0000\u0000!\"\u0005\u0004\u0000"+
		"\u0000\")\u0003\u0000\u0000\u0000#$\u0005\u0003\u0000\u0000$%\u0005\u001f"+
		"\u0000\u0000%&\u0005\u0004\u0000\u0000&(\u0003\u0000\u0000\u0000\'#\u0001"+
		"\u0000\u0000\u0000(+\u0001\u0000\u0000\u0000)\'\u0001\u0000\u0000\u0000"+
		")*\u0001\u0000\u0000\u0000*,\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000"+
		"\u0000,-\u0005\u0014\u0000\u0000-3\u0001\u0000\u0000\u0000./\u0005\u0002"+
		"\u0000\u0000/0\u0005\u001d\u0000\u000001\u0005\n\u0000\u000013\u0003\u0000"+
		"\u0000\u00002\u0002\u0001\u0000\u0000\u00002\u0003\u0001\u0000\u0000\u0000"+
		"2\u0004\u0001\u0000\u0000\u00002\b\u0001\u0000\u0000\u00002\f\u0001\u0000"+
		"\u0000\u00002\u001d\u0001\u0000\u0000\u00002.\u0001\u0000\u0000\u0000"+
		"3\u0001\u0001\u0000\u0000\u0000\u0003\u0018)2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}