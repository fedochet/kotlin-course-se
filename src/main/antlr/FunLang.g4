grammar FunLang;


//FILE: BLOCK;
parse
 : block
 ;

block
 : statement*
 ;

blockWithBraces : '{' block '}';

statement
 : function
 | variable
 | expr
 | whileStmt
 | ifStmt
 | assignmentStmt
 | returnStmt
 ;

function: 'fun' IDENT '(' parameterNames ')' blockWithBraces;
variable : 'var' IDENT ('=' expr)?;
parameterNames : (IDENT (',' IDENT)*)?;
whileStmt : 'while' '(' expr ')' blockWithBraces;
ifStmt : 'if' '(' expr ')' blockWithBraces ('else' blockWithBraces)?;
assignmentStmt : IDENT '=' expr;
returnStmt : 'return' expr;

expr
 : functionCall                                     #functionCallExpr
 | left=expr op=(MULT | DIV | MOD) right=expr       #multiplicationExpr
 | left=expr op=(PLUS | MINUS) right=expr           #additiveExpr
 | left=expr op=(LT | GT | LTEQ | GTEQ) right=expr  #relationalExpr
 | left=expr op=(EQ | NEQ) right=expr               #equalityExpr
 | left=expr AND right=expr                         #andExpr
 | left=expr OR right=expr                          #orExpr
 | '(' expr ')'                                     #bracedExpr
 | variableRef                                      #variableExpr
 | LITERAL                                          #literalExpr
 ;

functionCall : IDENT'('arguments')';
arguments : (expr (',' expr)*)?;
variableRef : IDENT;

/* Идентификатор как в Си */
IDENT : [a-zA-Z_][a-zA-Z_0-9]*;
/* Десятичный целочисленный литерал без ведущих нулей */
LITERAL : '0' | ([1-9][0-9]*);

OR : '||';
AND : '&&';
EQ : '::';
NEQ : '!:';
GT : '>';
LT : '<';
GTEQ : '>:';
LTEQ : '<:';
PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
MOD : '%';
