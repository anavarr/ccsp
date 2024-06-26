// Generated from /home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/antlr4/CommonLexer.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class CommonLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		AT=1, EMARK=2, IMARK=3, SEQ=4, QUOTES=5, DOT=6, PLUS=7, AND=8, BRANCH=9, 
		LPAR=10, RPAR=11, SEL=12, SQLPAR=13, SQRPAR=14, CLPAR=15, CRPAR=16, PAR=17, 
		COL=18, IF=19, THEN=20, ELSE=21, CALL=22, NONE=23, SOME=24, END=25, LABEL=26, 
		IDENTIFIER=27, BLABEL=28, WS=29, SP=30, COMMENT=31;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"AT", "EMARK", "IMARK", "SEQ", "QUOTES", "DOT", "PLUS", "AND", "BRANCH", 
			"LPAR", "RPAR", "SEL", "SQLPAR", "SQRPAR", "CLPAR", "CRPAR", "PAR", "COL", 
			"IF", "THEN", "ELSE", "CALL", "NONE", "SOME", "END", "LABEL", "IDENTIFIER", 
			"BLABEL", "AddressLetter", "LabelLetter", "UpLetter", "SimpleLetter", 
			"SimpleLetterOrDigit", "WS", "SP", "COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'@'", "'!'", "'?'", "';'", "'\"'", "'.'", "'+'", "'&'", "'//'", 
			"'('", "')'", "'(+)'", "'['", "']'", "'{'", "'}'", "'|'", "':'", "'If'", 
			"'Then'", "'Else'", "'Call'", "'None'", "'Some'", "'End'", null, null, 
			null, null, "' '"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "AT", "EMARK", "IMARK", "SEQ", "QUOTES", "DOT", "PLUS", "AND", 
			"BRANCH", "LPAR", "RPAR", "SEL", "SQLPAR", "SQRPAR", "CLPAR", "CRPAR", 
			"PAR", "COL", "IF", "THEN", "ELSE", "CALL", "NONE", "SOME", "END", "LABEL", 
			"IDENTIFIER", "BLABEL", "WS", "SP", "COMMENT"
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


	public CommonLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CommonLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u001f\u00c9\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017"+
		"\u0002\u0018\u0007\u0018\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a"+
		"\u0002\u001b\u0007\u001b\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d"+
		"\u0002\u001e\u0007\u001e\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!"+
		"\u0007!\u0002\"\u0007\"\u0002#\u0007#\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014"+
		"\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015"+
		"\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0019\u0001\u0019"+
		"\u0005\u0019\u0093\b\u0019\n\u0019\f\u0019\u0096\t\u0019\u0001\u001a\u0001"+
		"\u001a\u0005\u001a\u009a\b\u001a\n\u001a\f\u001a\u009d\t\u001a\u0001\u001b"+
		"\u0001\u001b\u0004\u001b\u00a1\b\u001b\u000b\u001b\f\u001b\u00a2\u0001"+
		"\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0003\u001c\u00a9\b\u001c\u0001"+
		"\u001d\u0001\u001d\u0003\u001d\u00ad\b\u001d\u0001\u001e\u0001\u001e\u0001"+
		"\u001f\u0001\u001f\u0001 \u0003 \u00b4\b \u0001!\u0004!\u00b7\b!\u000b"+
		"!\f!\u00b8\u0001!\u0001!\u0001\"\u0001\"\u0001\"\u0001\"\u0001#\u0001"+
		"#\u0005#\u00c3\b#\n#\f#\u00c6\t#\u0001#\u0001#\u0000\u0000$\u0001\u0001"+
		"\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r\u0007\u000f"+
		"\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e\u001d\u000f"+
		"\u001f\u0010!\u0011#\u0012%\u0013\'\u0014)\u0015+\u0016-\u0017/\u0018"+
		"1\u00193\u001a5\u001b7\u001c9\u0000;\u0000=\u0000?\u0000A\u0000C\u001d"+
		"E\u001eG\u001f\u0001\u0000\u0006\u0004\u0000./::<<>>\u0001\u0000AZ\u0002"+
		"\u0000AZaz\u0003\u000009AZaz\u0002\u0000\t\n\r\r\u0002\u0000\n\n\r\r\u00ca"+
		"\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000"+
		"\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000"+
		"\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000"+
		"\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011"+
		"\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015"+
		"\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019"+
		"\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d"+
		"\u0001\u0000\u0000\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001"+
		"\u0000\u0000\u0000\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000"+
		"\u0000\u0000\'\u0001\u0000\u0000\u0000\u0000)\u0001\u0000\u0000\u0000"+
		"\u0000+\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/"+
		"\u0001\u0000\u0000\u0000\u00001\u0001\u0000\u0000\u0000\u00003\u0001\u0000"+
		"\u0000\u0000\u00005\u0001\u0000\u0000\u0000\u00007\u0001\u0000\u0000\u0000"+
		"\u0000C\u0001\u0000\u0000\u0000\u0000E\u0001\u0000\u0000\u0000\u0000G"+
		"\u0001\u0000\u0000\u0000\u0001I\u0001\u0000\u0000\u0000\u0003K\u0001\u0000"+
		"\u0000\u0000\u0005M\u0001\u0000\u0000\u0000\u0007O\u0001\u0000\u0000\u0000"+
		"\tQ\u0001\u0000\u0000\u0000\u000bS\u0001\u0000\u0000\u0000\rU\u0001\u0000"+
		"\u0000\u0000\u000fW\u0001\u0000\u0000\u0000\u0011Y\u0001\u0000\u0000\u0000"+
		"\u0013\\\u0001\u0000\u0000\u0000\u0015^\u0001\u0000\u0000\u0000\u0017"+
		"`\u0001\u0000\u0000\u0000\u0019d\u0001\u0000\u0000\u0000\u001bf\u0001"+
		"\u0000\u0000\u0000\u001dh\u0001\u0000\u0000\u0000\u001fj\u0001\u0000\u0000"+
		"\u0000!l\u0001\u0000\u0000\u0000#n\u0001\u0000\u0000\u0000%p\u0001\u0000"+
		"\u0000\u0000\'s\u0001\u0000\u0000\u0000)x\u0001\u0000\u0000\u0000+}\u0001"+
		"\u0000\u0000\u0000-\u0082\u0001\u0000\u0000\u0000/\u0087\u0001\u0000\u0000"+
		"\u00001\u008c\u0001\u0000\u0000\u00003\u0090\u0001\u0000\u0000\u00005"+
		"\u0097\u0001\u0000\u0000\u00007\u009e\u0001\u0000\u0000\u00009\u00a8\u0001"+
		"\u0000\u0000\u0000;\u00ac\u0001\u0000\u0000\u0000=\u00ae\u0001\u0000\u0000"+
		"\u0000?\u00b0\u0001\u0000\u0000\u0000A\u00b3\u0001\u0000\u0000\u0000C"+
		"\u00b6\u0001\u0000\u0000\u0000E\u00bc\u0001\u0000\u0000\u0000G\u00c0\u0001"+
		"\u0000\u0000\u0000IJ\u0005@\u0000\u0000J\u0002\u0001\u0000\u0000\u0000"+
		"KL\u0005!\u0000\u0000L\u0004\u0001\u0000\u0000\u0000MN\u0005?\u0000\u0000"+
		"N\u0006\u0001\u0000\u0000\u0000OP\u0005;\u0000\u0000P\b\u0001\u0000\u0000"+
		"\u0000QR\u0005\"\u0000\u0000R\n\u0001\u0000\u0000\u0000ST\u0005.\u0000"+
		"\u0000T\f\u0001\u0000\u0000\u0000UV\u0005+\u0000\u0000V\u000e\u0001\u0000"+
		"\u0000\u0000WX\u0005&\u0000\u0000X\u0010\u0001\u0000\u0000\u0000YZ\u0005"+
		"/\u0000\u0000Z[\u0005/\u0000\u0000[\u0012\u0001\u0000\u0000\u0000\\]\u0005"+
		"(\u0000\u0000]\u0014\u0001\u0000\u0000\u0000^_\u0005)\u0000\u0000_\u0016"+
		"\u0001\u0000\u0000\u0000`a\u0005(\u0000\u0000ab\u0005+\u0000\u0000bc\u0005"+
		")\u0000\u0000c\u0018\u0001\u0000\u0000\u0000de\u0005[\u0000\u0000e\u001a"+
		"\u0001\u0000\u0000\u0000fg\u0005]\u0000\u0000g\u001c\u0001\u0000\u0000"+
		"\u0000hi\u0005{\u0000\u0000i\u001e\u0001\u0000\u0000\u0000jk\u0005}\u0000"+
		"\u0000k \u0001\u0000\u0000\u0000lm\u0005|\u0000\u0000m\"\u0001\u0000\u0000"+
		"\u0000no\u0005:\u0000\u0000o$\u0001\u0000\u0000\u0000pq\u0005I\u0000\u0000"+
		"qr\u0005f\u0000\u0000r&\u0001\u0000\u0000\u0000st\u0005T\u0000\u0000t"+
		"u\u0005h\u0000\u0000uv\u0005e\u0000\u0000vw\u0005n\u0000\u0000w(\u0001"+
		"\u0000\u0000\u0000xy\u0005E\u0000\u0000yz\u0005l\u0000\u0000z{\u0005s"+
		"\u0000\u0000{|\u0005e\u0000\u0000|*\u0001\u0000\u0000\u0000}~\u0005C\u0000"+
		"\u0000~\u007f\u0005a\u0000\u0000\u007f\u0080\u0005l\u0000\u0000\u0080"+
		"\u0081\u0005l\u0000\u0000\u0081,\u0001\u0000\u0000\u0000\u0082\u0083\u0005"+
		"N\u0000\u0000\u0083\u0084\u0005o\u0000\u0000\u0084\u0085\u0005n\u0000"+
		"\u0000\u0085\u0086\u0005e\u0000\u0000\u0086.\u0001\u0000\u0000\u0000\u0087"+
		"\u0088\u0005S\u0000\u0000\u0088\u0089\u0005o\u0000\u0000\u0089\u008a\u0005"+
		"m\u0000\u0000\u008a\u008b\u0005e\u0000\u0000\u008b0\u0001\u0000\u0000"+
		"\u0000\u008c\u008d\u0005E\u0000\u0000\u008d\u008e\u0005n\u0000\u0000\u008e"+
		"\u008f\u0005d\u0000\u0000\u008f2\u0001\u0000\u0000\u0000\u0090\u0094\u0003"+
		"=\u001e\u0000\u0091\u0093\u0003;\u001d\u0000\u0092\u0091\u0001\u0000\u0000"+
		"\u0000\u0093\u0096\u0001\u0000\u0000\u0000\u0094\u0092\u0001\u0000\u0000"+
		"\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u00954\u0001\u0000\u0000\u0000"+
		"\u0096\u0094\u0001\u0000\u0000\u0000\u0097\u009b\u0003?\u001f\u0000\u0098"+
		"\u009a\u0003A \u0000\u0099\u0098\u0001\u0000\u0000\u0000\u009a\u009d\u0001"+
		"\u0000\u0000\u0000\u009b\u0099\u0001\u0000\u0000\u0000\u009b\u009c\u0001"+
		"\u0000\u0000\u0000\u009c6\u0001\u0000\u0000\u0000\u009d\u009b\u0001\u0000"+
		"\u0000\u0000\u009e\u00a0\u0005\"\u0000\u0000\u009f\u00a1\u00039\u001c"+
		"\u0000\u00a0\u009f\u0001\u0000\u0000\u0000\u00a1\u00a2\u0001\u0000\u0000"+
		"\u0000\u00a2\u00a0\u0001\u0000\u0000\u0000\u00a2\u00a3\u0001\u0000\u0000"+
		"\u0000\u00a3\u00a4\u0001\u0000\u0000\u0000\u00a4\u00a5\u0005\"\u0000\u0000"+
		"\u00a58\u0001\u0000\u0000\u0000\u00a6\u00a9\u0003A \u0000\u00a7\u00a9"+
		"\u0007\u0000\u0000\u0000\u00a8\u00a6\u0001\u0000\u0000\u0000\u00a8\u00a7"+
		"\u0001\u0000\u0000\u0000\u00a9:\u0001\u0000\u0000\u0000\u00aa\u00ad\u0003"+
		"A \u0000\u00ab\u00ad\u0005_\u0000\u0000\u00ac\u00aa\u0001\u0000\u0000"+
		"\u0000\u00ac\u00ab\u0001\u0000\u0000\u0000\u00ad<\u0001\u0000\u0000\u0000"+
		"\u00ae\u00af\u0007\u0001\u0000\u0000\u00af>\u0001\u0000\u0000\u0000\u00b0"+
		"\u00b1\u0007\u0002\u0000\u0000\u00b1@\u0001\u0000\u0000\u0000\u00b2\u00b4"+
		"\u0007\u0003\u0000\u0000\u00b3\u00b2\u0001\u0000\u0000\u0000\u00b4B\u0001"+
		"\u0000\u0000\u0000\u00b5\u00b7\u0007\u0004\u0000\u0000\u00b6\u00b5\u0001"+
		"\u0000\u0000\u0000\u00b7\u00b8\u0001\u0000\u0000\u0000\u00b8\u00b6\u0001"+
		"\u0000\u0000\u0000\u00b8\u00b9\u0001\u0000\u0000\u0000\u00b9\u00ba\u0001"+
		"\u0000\u0000\u0000\u00ba\u00bb\u0006!\u0000\u0000\u00bbD\u0001\u0000\u0000"+
		"\u0000\u00bc\u00bd\u0005 \u0000\u0000\u00bd\u00be\u0001\u0000\u0000\u0000"+
		"\u00be\u00bf\u0006\"\u0000\u0000\u00bfF\u0001\u0000\u0000\u0000\u00c0"+
		"\u00c4\u0005#\u0000\u0000\u00c1\u00c3\b\u0005\u0000\u0000\u00c2\u00c1"+
		"\u0001\u0000\u0000\u0000\u00c3\u00c6\u0001\u0000\u0000\u0000\u00c4\u00c2"+
		"\u0001\u0000\u0000\u0000\u00c4\u00c5\u0001\u0000\u0000\u0000\u00c5\u00c7"+
		"\u0001\u0000\u0000\u0000\u00c6\u00c4\u0001\u0000\u0000\u0000\u00c7\u00c8"+
		"\u0006#\u0000\u0000\u00c8H\u0001\u0000\u0000\u0000\t\u0000\u0094\u009b"+
		"\u00a2\u00a8\u00ac\u00b3\u00b8\u00c4\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}