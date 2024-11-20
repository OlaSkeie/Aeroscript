grammar AeroScript;

@header {
package no.uio.aeroscript.antlr;
}

// Whitespace and comments added
WS: [ \t\r\n\u000C]+ -> channel(HIDDEN);
COMMENT: '/*' .*? '*/' -> channel(HIDDEN);
LINE_COMMENT: '//' ~[\r\n]* -> channel(HIDDEN);

LCURL: '{';
RCURL: '}';
LSQUARE: '[';
RSQUARE: ']';
LPAREN: '(';
RPAREN: ')';

NEG: '--';
SEMI: ';';
COMMA: ',';
GREATER: '>';

PLUS: '+';
MINUS: '-';
TIMES: '*';
LEFT: 'left';
RIGHT: 'right';
ARROW: '->';
LOWB: 'low battery';
OBSTACLE: 'obstacle';
MESSAGE: 'message';
RETURNTOBASE: 'return to base';
MOVE: 'move';
TO: 'to';
BY: 'by';
TURN: 'turn';
ASCBY: 'ascend by';
DECBY: 'descend by';
DESCGRND: 'descend to ground';
ON: 'on';
SPEED: 'at speed';

// Define all the elements of the language for the various keywords that you need
RANDOM: 'random';
POINT: 'point';

// Keywords
ID: [a-zA-Z_][a-zA-Z_0-9]*;
NUMBER: [0-9]+ ('.' [0-9]+)?;

// Entry point
program: execution (execution)* EOF;
execution:
	arrow1 = ARROW? id1 = ID LCURL (statement)* RCURL (
		ARROW id2 = ID
	)?;
statement: action | reaction | execution;

// Statements
reaction: ON event ARROW id3 = ID;
event: OBSTACLE | LOWB | MESSAGE LSQUARE ID RSQUARE;
action: (acDock | acMove | acTurn | acAscend | acDescend) (
		'for' expression 's'
		| SPEED expression
	)?;

acDock: RETURNTOBASE;
acMove: MOVE (TO POINT point | BY NUMBER) (SPEED expression)?;
acTurn: TURN (RIGHT | LEFT)? BY expression;
acAscend: ASCBY expression;
acDescend: DECBY expression | DESCGRND;

expression:
	NEG expression
	| expression TIMES expression
	| expression (PLUS | MINUS) expression
	| RANDOM range?
	| POINT point
	| NUMBER
	| LPAREN expression RPAREN;

point: LPAREN expression COMMA expression RPAREN;
range:
	LSQUARE expression COMMA expression RSQUARE
	| LPAREN expression COMMA expression RPAREN;