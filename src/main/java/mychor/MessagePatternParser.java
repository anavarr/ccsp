// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/MessagePattern.g4 by ANTLR 4.13.1
package mychor;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class MessagePatternParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TO=1, REPEATER=2, AT=3, EMARK=4, IMARK=5, SEQ=6, QUOTES=7, DOT=8, PLUS=9, 
		AND=10, BRANCH=11, LPAR=12, RPAR=13, SEL=14, SQLPAR=15, SQRPAR=16, CLPAR=17, 
		CRPAR=18, PAR=19, COL=20, IF=21, THEN=22, ELSE=23, CALL=24, NONE=25, SOME=26, 
		END=27, LABEL=28, IDENTIFIER=29, BLABEL=30, WS=31, SP=32, COMMENT=33;
	public static final int
		RULE_pattern = 0, RULE_pattern_single = 1, RULE_expression = 2, RULE_exchange = 3;
	private static String[] makeRuleNames() {
		return new String[] {
			"pattern", "pattern_single", "expression", "exchange"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'->'", null, "'@'", "'!'", "'?'", "';'", "'\"'", "'.'", "'+'", 
			"'&'", "'//'", "'('", "')'", "'(+)'", "'['", "']'", "'{'", "'}'", "'|'", 
			"':'", "'If'", "'Then'", "'Else'", "'Call'", "'None'", "'Some'", "'End'", 
			null, null, null, null, "' '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "TO", "REPEATER", "AT", "EMARK", "IMARK", "SEQ", "QUOTES", "DOT", 
			"PLUS", "AND", "BRANCH", "LPAR", "RPAR", "SEL", "SQLPAR", "SQRPAR", "CLPAR", 
			"CRPAR", "PAR", "COL", "IF", "THEN", "ELSE", "CALL", "NONE", "SOME", 
			"END", "LABEL", "IDENTIFIER", "BLABEL", "WS", "SP", "COMMENT"
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
	public String getGrammarFileName() { return "MessagePattern.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MessagePatternParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PatternContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MessagePatternParser.EOF, 0); }
		public List<Pattern_singleContext> pattern_single() {
			return getRuleContexts(Pattern_singleContext.class);
		}
		public Pattern_singleContext pattern_single(int i) {
			return getRuleContext(Pattern_singleContext.class,i);
		}
		public PatternContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).enterPattern(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).exitPattern(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MessagePatternVisitor ) return ((MessagePatternVisitor<? extends T>)visitor).visitPattern(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PatternContext pattern() throws RecognitionException {
		PatternContext _localctx = new PatternContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_pattern);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(9); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(8);
				pattern_single();
				}
				}
				setState(11); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==LABEL );
			setState(13);
			match(EOF);
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

	@SuppressWarnings("CheckReturnValue")
	public static class Pattern_singleContext extends ParserRuleContext {
		public TerminalNode LABEL() { return getToken(MessagePatternParser.LABEL, 0); }
		public List<TerminalNode> QUOTES() { return getTokens(MessagePatternParser.QUOTES); }
		public TerminalNode QUOTES(int i) {
			return getToken(MessagePatternParser.QUOTES, i);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public Pattern_singleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pattern_single; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).enterPattern_single(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).exitPattern_single(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MessagePatternVisitor ) return ((MessagePatternVisitor<? extends T>)visitor).visitPattern_single(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Pattern_singleContext pattern_single() throws RecognitionException {
		Pattern_singleContext _localctx = new Pattern_singleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_pattern_single);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			match(LABEL);
			setState(16);
			match(QUOTES);
			setState(17);
			expression(0);
			setState(18);
			match(QUOTES);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ChoiceContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode PAR() { return getToken(MessagePatternParser.PAR, 0); }
		public ChoiceContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).enterChoice(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).exitChoice(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MessagePatternVisitor ) return ((MessagePatternVisitor<? extends T>)visitor).visitChoice(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SingleExchangeContext extends ExpressionContext {
		public ExchangeContext exchange() {
			return getRuleContext(ExchangeContext.class,0);
		}
		public SingleExchangeContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).enterSingleExchange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).exitSingleExchange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MessagePatternVisitor ) return ((MessagePatternVisitor<? extends T>)visitor).visitSingleExchange(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class SequentContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode SEQ() { return getToken(MessagePatternParser.SEQ, 0); }
		public SequentContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).enterSequent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).exitSequent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MessagePatternVisitor ) return ((MessagePatternVisitor<? extends T>)visitor).visitSequent(this);
			else return visitor.visitChildren(this);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class RepeatContext extends ExpressionContext {
		public TerminalNode LPAR() { return getToken(MessagePatternParser.LPAR, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAR() { return getToken(MessagePatternParser.RPAR, 0); }
		public TerminalNode REPEATER() { return getToken(MessagePatternParser.REPEATER, 0); }
		public RepeatContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).enterRepeat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).exitRepeat(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MessagePatternVisitor ) return ((MessagePatternVisitor<? extends T>)visitor).visitRepeat(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_expression, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(28);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				{
				_localctx = new SingleExchangeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(21);
				exchange();
				}
				break;
			case LPAR:
				{
				_localctx = new RepeatContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(22);
				match(LPAR);
				setState(23);
				expression(0);
				setState(24);
				match(RPAR);
				setState(26);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(25);
					match(REPEATER);
					}
					break;
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(38);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(36);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new SequentContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(30);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(31);
						match(SEQ);
						setState(32);
						expression(4);
						}
						break;
					case 2:
						{
						_localctx = new ChoiceContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(33);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(34);
						match(PAR);
						setState(35);
						expression(3);
						}
						break;
					}
					} 
				}
				setState(40);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExchangeContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(MessagePatternParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(MessagePatternParser.IDENTIFIER, i);
		}
		public TerminalNode TO() { return getToken(MessagePatternParser.TO, 0); }
		public ExchangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exchange; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).enterExchange(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof MessagePatternListener ) ((MessagePatternListener)listener).exitExchange(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MessagePatternVisitor ) return ((MessagePatternVisitor<? extends T>)visitor).visitExchange(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExchangeContext exchange() throws RecognitionException {
		ExchangeContext _localctx = new ExchangeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_exchange);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(IDENTIFIER);
			setState(42);
			match(TO);
			setState(43);
			match(IDENTIFIER);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001!.\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002"+
		"\u0007\u0002\u0002\u0003\u0007\u0003\u0001\u0000\u0004\u0000\n\b\u0000"+
		"\u000b\u0000\f\u0000\u000b\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002\u001b\b\u0002\u0003\u0002"+
		"\u001d\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0005\u0002%\b\u0002\n\u0002\f\u0002(\t\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0000\u0001\u0004\u0004"+
		"\u0000\u0002\u0004\u0006\u0000\u0000.\u0000\t\u0001\u0000\u0000\u0000"+
		"\u0002\u000f\u0001\u0000\u0000\u0000\u0004\u001c\u0001\u0000\u0000\u0000"+
		"\u0006)\u0001\u0000\u0000\u0000\b\n\u0003\u0002\u0001\u0000\t\b\u0001"+
		"\u0000\u0000\u0000\n\u000b\u0001\u0000\u0000\u0000\u000b\t\u0001\u0000"+
		"\u0000\u0000\u000b\f\u0001\u0000\u0000\u0000\f\r\u0001\u0000\u0000\u0000"+
		"\r\u000e\u0005\u0000\u0000\u0001\u000e\u0001\u0001\u0000\u0000\u0000\u000f"+
		"\u0010\u0005\u001c\u0000\u0000\u0010\u0011\u0005\u0007\u0000\u0000\u0011"+
		"\u0012\u0003\u0004\u0002\u0000\u0012\u0013\u0005\u0007\u0000\u0000\u0013"+
		"\u0003\u0001\u0000\u0000\u0000\u0014\u0015\u0006\u0002\uffff\uffff\u0000"+
		"\u0015\u001d\u0003\u0006\u0003\u0000\u0016\u0017\u0005\f\u0000\u0000\u0017"+
		"\u0018\u0003\u0004\u0002\u0000\u0018\u001a\u0005\r\u0000\u0000\u0019\u001b"+
		"\u0005\u0002\u0000\u0000\u001a\u0019\u0001\u0000\u0000\u0000\u001a\u001b"+
		"\u0001\u0000\u0000\u0000\u001b\u001d\u0001\u0000\u0000\u0000\u001c\u0014"+
		"\u0001\u0000\u0000\u0000\u001c\u0016\u0001\u0000\u0000\u0000\u001d&\u0001"+
		"\u0000\u0000\u0000\u001e\u001f\n\u0003\u0000\u0000\u001f \u0005\u0006"+
		"\u0000\u0000 %\u0003\u0004\u0002\u0004!\"\n\u0002\u0000\u0000\"#\u0005"+
		"\u0013\u0000\u0000#%\u0003\u0004\u0002\u0003$\u001e\u0001\u0000\u0000"+
		"\u0000$!\u0001\u0000\u0000\u0000%(\u0001\u0000\u0000\u0000&$\u0001\u0000"+
		"\u0000\u0000&\'\u0001\u0000\u0000\u0000\'\u0005\u0001\u0000\u0000\u0000"+
		"(&\u0001\u0000\u0000\u0000)*\u0005\u001d\u0000\u0000*+\u0005\u0001\u0000"+
		"\u0000+,\u0005\u001d\u0000\u0000,\u0007\u0001\u0000\u0000\u0000\u0005"+
		"\u000b\u001a\u001c$&";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}