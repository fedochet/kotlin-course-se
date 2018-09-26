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
 | exprStmt
 | whileStmt
 | ifStmt
 | assignmentStmt
 | returnStmt
 ;

function: 'fun' IDENT '(' parameterNames ')' blockWithBraces;
variable : 'var' IDENT ('=' expr)?;
parameterNames : (IDENT (',' IDENT)*)?;
whileStmt : 'while' '(' expr ')' blockWithBraces;
ifStmt : 'if' '(' expr ')' thenBlock=blockWithBraces ('else' elseBlock=blockWithBraces)?;
assignmentStmt : IDENT '=' expr;
returnStmt : 'return' expr;
exprStmt : expr;

COMMENT : '//' ~('\t'|'\n')* -> skip;
WS : [ \t\r\n] -> skip;

expr
 : functionCall                                     #functionCallExpr
 | left=expr op=(MULT | DIV | MOD) right=expr       #binaryExpr
 | left=expr op=(PLUS | MINUS) right=expr           #binaryExpr
 | left=expr op=(LT | GT | LTEQ | GTEQ) right=expr  #binaryExpr
 | left=expr op=(EQ | NEQ) right=expr               #binaryExpr
 | left=expr AND right=expr                         #binaryExpr
 | left=expr OR right=expr                          #binaryExpr
 | '(' expr ')'                                     #bracedExpr
 | IDENT                                            #variableExpr
 | LITERAL                                          #literalExpr
 ;

functionCall : IDENT'('arguments')';
arguments : (expr (',' expr)*)?;

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
