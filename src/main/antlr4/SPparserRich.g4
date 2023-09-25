//B := End
// | p!e @! a; B
// | p?x @? a; B
// | p(+)l @+ a; B
// | p & mB1 // mB2
// | If e Then B1 Else B2
// | Call X
//
// mB := None
// | Some (a,B)

parser grammar SPparserRich;

options { tokenVocab=SPlexer; }

program : network (recdef)+ EOF;

network : IDENTIFIER '[' behaviour ']' ( '|' IDENTIFIER '[' behaviour ']' )*;

recdef : LABEL ':' behaviour;

behaviour
    : proc '!' expr AT '!' ann SEQ behaviour #Snd
    | proc '?' expr AT '?' ann SEQ behaviour #Rcv
    | proc SEL BLABEL AT '+' ann SEQ behaviour #Sel
    | proc '&' '{' BLABEL ':' mBehaviour AT '&' ann '}' ('//' '{' BLABEL ':' mBehaviour AT '&' ann '}')+ #BraAnn
    | proc '&' '{' BLABEL ':' mBehaviour '}'  ('//'  '{' BLABEL ':' mBehaviour '}')+ #Bra
    | IF expr THEN behaviour ELSE behaviour #Cdt
    | CALL LABEL AT '(' ann #CalAnn
    | CALL LABEL #Cal
    | END #End
    ;

mBehaviour
    : NONE #Non
    | SOME '(' behaviour ')' #Som
    ;

proc : IDENTIFIER;

expr
    : IDENTIFIER('.' IDENTIFIER)*
    | IDENTIFIER'('expr')'
    | IDENTIFIER'('')'
    | IDENTIFIER('.' IDENTIFIER)*'('expr')'
    | IDENTIFIER('.' IDENTIFIER)*'('')'
    ;

ann
    : '"' IDENTIFIER '"'
    | '"''"'
    ;
