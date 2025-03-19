//!A〈U 〉; T | ?A〈U 〉; T | ⊕ A{li : Ti}i∈I | &A{li : Ti}i∈I | rec t; T | t | end

parser grammar LocalTypeParser;

options { tokenVocab=LocalTypesLexer; }

localtype : 'end' #EndType
    | IDENTIFIER #CallType
    | IDENTIFIER '!' ';' localtype #SendType
    | IDENTIFIER '?' ';' localtype #ReceiveType
    | IDENTIFIER '+' '{' '"'IDENTIFIER'"'':'localtype(',''"'IDENTIFIER'"'':'localtype)* '}'#SelectType
    | IDENTIFIER '&''{' '"'IDENTIFIER'"'':'localtype(',''"'IDENTIFIER'"'':'localtype)* '}'#BranchType
    | 'µ' IDENTIFIER'.'localtype #RecDef
    ;