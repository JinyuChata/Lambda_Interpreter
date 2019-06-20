package cn.seecoder;

import java.util.LinkedList;

public class Parser {
    private Lexer lexer;
    private LinkedList<Token> ctx = new LinkedList<>();

    public Parser(Lexer l) {
        lexer = l;
    }

    public AST parse() {
        AST res = null;
        try {
            res = term(ctx);
        } catch (InvalidSyntaxException e) {
            e.printInfo();
            System.exit(2);
        }
        return res;
    }

//    next(Token): 返回下一个 token 是否匹配 Token
//    skip(Token): 和 next 一样, 但如果匹配的话会跳过
//    match(Token): 断言 next 方法返回 true 并 skip
//    token(Token): 断言 next 方法返回 true 并返回 token

    /**
     * Term ::= Application| LAMBDA LCID DOT Term
     *
     * @param ctx 上下文栈
     * @return 得到的AST
     * @throws InvalidSyntaxException 抛出语法错误
     */
    private AST term(LinkedList<Token> ctx) throws InvalidSyntaxException {
        if (lexer.skip(Token.LAMBDA)) {
            Token tmp = lexer.token(Token.LCID);
            lexer.match(Token.DOT);
            // 复制成新ctx以再次调用term
            LinkedList<Token> newCtx = new LinkedList<Token>(ctx);
            newCtx.push(tmp);
            return new Abstraction(tmp.value, term(newCtx));
        } else {
            return application(ctx);
        }
    }

    /**
     * Application ::= Application Atom| Atom
     *
     * @param ctx 上下文栈
     * @return 得到的AST
     * @throws InvalidSyntaxException 抛出的语法错误
     */
    private AST application(LinkedList<Token> ctx) throws InvalidSyntaxException {
        AST lhs, rhs;
        lhs = atom(ctx);

        while (true) {
            rhs = atom(ctx);
            if (rhs == null) {
                return lhs;
            } else {
                lhs = new Application(lhs, rhs);
            }
        }
    }

    /**
     * Atom ::= LPAREN Term RPAREN| LCID
     *
     * @param ctx 上下文栈
     * @return 得到的AST
     * @throws InvalidSyntaxException 抛出的语法错误
     */
    private AST atom(LinkedList<Token> ctx) throws InvalidSyntaxException {
        if (lexer.skip(Token.LPAREN)) {
            AST tmpTerm = term(ctx);
            lexer.match(Token.RPAREN);
            return tmpTerm;
        } else if (lexer.next(Token.LCID)) {
            Token tmp = lexer.token(Token.LCID);
            return new Identifier(ctx.indexOf(tmp), tmp.value);
        } else {
            return null;
        }
    }
}
