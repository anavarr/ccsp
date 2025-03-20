//!A〈U 〉; T | ?A〈U 〉; T | ⊕ A{li : Ti}i∈I | &A{li : Ti}i∈I | rec t; T | t | end

parser grammar LocalTypeParser;

options { tokenVocab=LocalTypesLexer; }

localtype : 'end' #EndType
    | LABEL #CallType
    | IDENTIFIER '!' ';' localtype #SendType
    | IDENTIFIER '?' ';' localtype #ReceiveType
    | IDENTIFIER '+' '{' BLABEL ':'localtype(',' BLABEL ':'localtype)* '}'#SelectType
    | IDENTIFIER '&''{' BLABEL ':'localtype(',' BLABEL ':'localtype)* '}'#BranchType
    | 'µ' LABEL'.'localtype #RecDef
    ;