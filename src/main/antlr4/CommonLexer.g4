lexer grammar CommonLexer;

AT : '@';
EMARK : '!';
IMARK : '?';
SEQ: ';';
QUOTES : '"';
DOT: '.';
PLUS : '+';
AND : '&';
BRANCH : '//';
LPAR : '(';
RPAR : ')';
SEL : '(+)';
SQLPAR : '[';
SQRPAR : ']';
CLPAR : '{';
CRPAR : '}';
PAR : '|';
COL : ':';

IF : 'If';
THEN : 'Then';
ELSE: 'Else';
CALL : 'Call';
NONE : 'None';
SOME : 'Some';
END : 'End';

LABEL : UpLetter LabelLetter*;
IDENTIFIER : SimpleLetter SimpleLetterOrDigit*;
BLABEL: '"'(AddressLetter)+'"';

fragment AddressLetter : SimpleLetterOrDigit | '/' | '.' | ':' | '<' | '>';
fragment LabelLetter : SimpleLetterOrDigit | '_';
fragment UpLetter: [A-Z];
fragment SimpleLetter : [a-zA-Z];
fragment SimpleLetterOrDigit : [a-zA-Z]
    | [0-9]
    ;

WS  : [\t\n\r]+ -> skip;
SP : ' ' -> skip;
COMMENT : '#' ~[\r\n]* -> skip;