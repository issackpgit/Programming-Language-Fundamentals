/* 		OBJECT-ORIENTED PARSER FOR SIMPLE EXPRESSIONS

 expr    ->  term   [ (+ | -) expr ]
 term    ->  factor [ (* | /) term ]
 factor  ->  int_lit  |  '(' expr ')'
 
*/

public class Parser {
	static Expr e;

	public static void main(String[] args) {
		System.out.println("Enter expression and terminate with semi-colon!\n");
		Lexer.lex();
	    Code.init();
		e = new Expr();     
		Code.output();
	}
}

class Expr { 				 // expr -> term (+ | -) expr | term
	Term t;
	char op;
	Expr e;

	public Expr() {	 // C is an inherited attribute for Expr
		t = new Term();
		if (Lexer.nextToken == Token.ADD_OP || Lexer.nextToken == Token.SUB_OP) {
			op = Lexer.nextChar;
			Lexer.lex();    // scan over op
			e = new Expr();
			Code.gen(op);      // generate the byte-code for op
		}
	}
}

class Term { 				// term -> factor (* | /) term | factor
	Factor f;
	char op;
	Term t;

	public Term() {
		f = new Factor();
		if (Lexer.nextToken == Token.MULT_OP || Lexer.nextToken == Token.DIV_OP) {
			op = Lexer.nextChar;
			Lexer.lex();     // scan over op
			t = new Term();
			Code.gen(op);
		}
	}
}

class Factor { 				// factor -> number | '(' expr ')'
	int i;
	Expr e;

	public Factor() {
		switch (Lexer.nextToken) {
		case Token.INT_LIT: // number
			i = Lexer.intValue;
			Lexer.lex();         // scan over int
			Code.gen(i);            // generate byte-code for i
			break;
		case Token.LEFT_PAREN: 
			Lexer.lex();        // scan over '('
			e = new Expr();
			Lexer.lex();        // scan over ')'
			break;
		default:
			break;
		}
	}
}

class Code {

	public static String[] code;
	
	public static int codeptr;
	
	public static void init() {
		code = new String[100];
		codeptr = 0;
	}

	public static void gen(String s) {
		code[codeptr] = s;
		codeptr++;
	}

	public static void gen(char c) {
		gen(opcode(c));
	}

	public static void gen(int i) {
		if (i < 6 && i > -1)
			gen("iconst_" + i);
		else if (i < 128) {
			gen("bipush " + i);
			skip(1);
		} else {
			gen("sipush " + i);
			skip(2);
		}
	}

	public static void skip(int n) {
		codeptr = codeptr + n;
	}

	public static String opcode(char c) {
		switch (c) {
		case '+':
			return "iadd";
		case '-':
			return "isub";
		case '*':
			return "imul";
		case '/':
			return "idiv";
		default:
			return "";
		}
	}

	public static void output() {
		
		System.out.println("Code:");
		
		for (int i = 0; i < codeptr; i++)
			if (code[i] != null && code[i] != "")
				System.out.println("     " + i + ": " + code[i]);
	}

}
