import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
%%

%public
%class Lexer
%cup
%full
%implements sym
%char
%line
%column

%{
    StringBuffer string = new StringBuffer();
    public Lexer(java.io.Reader in, ComplexSymbolFactory sf){
      this(in);
      symbolFactory = sf;
    }
    ComplexSymbolFactory symbolFactory;

  private Symbol symbol(String name, int sym) {
      return symbolFactory.newSymbol(name, sym, new Location(yyline+1,yycolumn+1,(int)yychar), new Location(yyline+1,yycolumn+yylength(),(int)(yychar+yylength())));
  }
  private Symbol symbol(String name, int sym, Object val) {
      Location left = new Location(yyline+1,yycolumn+1,(int)yychar);
      Location right= new Location(yyline+1,yycolumn+yylength(), (int)(yychar+yylength()));
      return symbolFactory.newSymbol(name, sym, left, right,val);
  } 
  private Symbol symbol(String name, int sym, Object val,int buflength) {
      Location left = new Location(yyline+1,yycolumn+yylength()-buflength,(int)(yychar+yylength()-buflength));
      Location right= new Location(yyline+1,yycolumn+yylength(), (int)(yychar+yylength()));
      return symbolFactory.newSymbol(name, sym, left, right,val);
  }       
  private void error(String message) {
    System.out.println("Error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
  }
%} 

%eofval{
     return symbolFactory.newSymbol("EOF", EOF, new Location(yyline+1,yycolumn+1,(int)yychar), new Location(yyline+1,yycolumn+1,(int)(yychar+1)));
%eofval}

Ident = [a-zA-Z$_] [a-zA-Z0-9$_]*

IntLiteral = 0 | [1-9][0-9]*
FloatLiteral = [0-9]*\.[0-9]+
new_line = \r|\n|\r\n;

white_space = {new_line} | [ \t\f]

%state STRING

%%

<YYINITIAL>{
/* keywords */

"else"           { return symbol("else",ELSE); }
"if"              { return symbol("if",IF); }
"while"           { return symbol("while",WHILE); }
"read"            { return symbol("read",READ); }
"write"           { return symbol("write",WRITE); }


/* types */
"int" { return symbol("int", TYPE, Type.INT);}
"string" { return symbol("string",TYPE, Type.STRING);}
"float" { return symbol("float",TYPE, Type.FLOAT);}
/* names */
{Ident}           { return symbol("Identifier",IDENT, yytext()); }
  
/* string literals */

/* char literal */

/* literals */
{IntLiteral} { return symbol("Intconst",INTCONST, new Integer(Integer.parseInt(yytext()))); }

/* float literal */
{FloatLiteral} { return symbol("Floatconst",FLOATCONST, new Float(Float.parseFloat(yytext()))); }



/* separators */
  \"              { string.setLength(0); yybegin(STRING); }
";"               { return symbol("semicolon",SEMICOLON); }
","               { return symbol("comma",COMMA); }
"("               { return symbol("(",LPAR); }
")"               { return symbol(")",RPAR); }
"{"               { return symbol("{",BEGIN); }
"}"               { return symbol("}",END); }
"="               { return symbol("=",ASSIGN); }
"+"               { return symbol("plus",BINOP,  OpArit.PLUS  ); }
"-"               { return symbol("minus",BINOP,  OpArit.MINUS  ); }
"*"               { return symbol("mult",BINOP,  OpArit.MULT  ); }
"/"               { return symbol("div",BINOP,  OpArit.DIV  ); }
"%"               { return symbol("mod",BINOP,  OpArit.MOD  ); }
"<="              { return symbol("leq",COMP,  OpRel.LEQ  ); }
">="              { return symbol("gtq",COMP,  OpRel.GTQ  ); }
"=="              { return symbol("eq",COMP,  OpRel.EQ   ); }
"!="              { return symbol("neq",COMP,  OpRel.NEQ  ); }
"<"               { return symbol("le",COMP,  OpRel.LE   ); }
">"               { return symbol("gt",COMP,  OpRel.GT   ); }



{white_space}     { /* ignore */ }

}

<STRING> {
  \"                             { yybegin(YYINITIAL); 
      return symbol("StringConst",STRINGCONST,string.toString(),string.length()); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}


/* error fallback */
.|\n              {  /* throw new Error("Illegal character <"+ yytext()+">");*/
		    error("Illegal character <"+ yytext()+">");
                  }
