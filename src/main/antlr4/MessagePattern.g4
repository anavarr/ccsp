grammar MessagePattern;

import CommonLexer;

pattern
    : pattern_single+ EOF
    ;

pattern_single
    : LABEL '"' expression '"'
    ;

expression
    : exchange #SingleExchange
    | expression ';' expression #Sequent
    | expression '|' expression #Choice
    | '(' expression ')' (REPEATER)? #Repeat
    ;

exchange
    : IDENTIFIER TO IDENTIFIER
    ;

TO : '->';

REPEATER
    : '*'
    | '+'
    | '?';