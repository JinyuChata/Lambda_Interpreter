package cn.seecoder;

import java.util.LinkedList;

public class Parser {
//    private int stack = 0;
    private Lexer lexer;
    private LinkedList<Token> ctx = new LinkedList<>();
    public Parser(Lexer l){
        lexer = l;
    }
    public AST parse(){
        return term(ctx);
    }

//    next(Token): 返回下一个 token 是否匹配 Token
//    skip(Token): 和 next 一样, 但如果匹配的话会跳过
//    match(Token): 断言 next 方法返回 true 并 skip
//    token(Token): 断言 next 方法返回 true 并返回 token

//    Term ::= Application| LAMBDA LCID DOT Term
    private AST term(LinkedList<Token> ctx) {
        if (lexer.skip(Token.LAMBDA)) {
            Token tmp = lexer.token(Token.LCID);
            lexer.skip(Token.DOT);
            LinkedList<Token> newCtx = new LinkedList<Token>(ctx);
            newCtx.push(tmp);
            return new Abstraction(tmp.value, term(newCtx));
        } else {
            return application(ctx);
        }
    }
//    Application ::= Application Atom| Atom
    private AST application(LinkedList<Token> ctx){
        AST lhs, rhs = null;
        lhs = atom(ctx);

        //
        while (true) {
            rhs = atom(ctx);
            if (rhs == null) {
                return lhs;
            } else {
                lhs = new Application(lhs, rhs);
            }
        }


    }

//    Atom ::= LPAREN Term RPAREN| LCID
    private AST atom(LinkedList<Token> ctx) {
        if (lexer.skip(Token.LPAREN)) {
            AST tmpTerm = term(ctx);
            lexer.match(Token.RPAREN);
            return tmpTerm;
        } else if (lexer.next(Token.LCID)) {
            Token tmp = lexer.token(Token.LCID);
            return new Identifier(ctx.indexOf(tmp), ctx.size(), tmp.value);
        } else {
            return null;
        }
    }
}
