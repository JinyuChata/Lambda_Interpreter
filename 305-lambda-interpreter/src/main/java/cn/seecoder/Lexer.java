package cn.seecoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    public String source;
    public int index = 0;
    public Token token;

    public Lexer(String s) {
        source = s;
        token = nextToken();
    }

    public Token nextToken() {
        char nextChr;
        try {
            nextChr = source.charAt(index);
        } catch (Exception e) {
            return new Token(Token.EOF);
        }

        if (nextChr == '(') {
            token = new Token(Token.LPAREN);
            index++;
            return token;
        }

        if (nextChr == ')') {
            token = new Token(Token.RPAREN);
            index++;
            return token;
        }

        if (nextChr == '\\') {
            token = new Token(Token.LAMBDA);
            index++;
            return token;
        }

        if (nextChr == '.') {
            token = new Token(Token.DOT);
            index++;
            return token;
        }

        Pattern pSpace = Pattern.compile("\\s");
        Matcher pSMatcher = pSpace.matcher(Character.toString(nextChr));
        if (pSMatcher.find()) {
            index++;
            return nextToken();
        }

        Pattern pDigit = Pattern.compile("[a-z]");
        Matcher pDMatcher = pDigit.matcher(Character.toString(nextChr));
        StringBuilder sb = new StringBuilder();
        boolean flag = true;
        while (pDMatcher.find()) {
            sb.append(pDMatcher.group());
            try {
                incIndex();
            } catch (IndexOutOfBoundsException e) {
                token = new Token(Token.LCID, sb.toString());
                return token;
            }
            nextChr = source.charAt(index);
            if (flag) {
                flag = false;
                pDigit = Pattern.compile("[a-zA-Z]");
                pDMatcher = pDigit.matcher(Character.toString(nextChr));
            } else {
                pDMatcher.reset(Character.toString(nextChr));
            }


        }
        token = new Token(Token.LCID, sb.toString());
        return token;
    }

    private boolean checkIndex() {
        return index < source.length();
    }

    private void incIndex() throws IndexOutOfBoundsException {
        index++;
        if (!checkIndex()) throw new IndexOutOfBoundsException();
    }

    public boolean next(int tokenType) {    // is this type?
        return token.type == tokenType;
    }

    public boolean skip(int tokenType) {
        if (next(tokenType)) {
            token = nextToken();
            return true;
        }
        return false;
    }

    //
    public void match(int tokenType) {
        assert next(tokenType);
        token = nextToken();
    }
//
    public Token token(int tokenType) {
        Token tmp = token;
        token = nextToken();
        return tmp;
    }

    public Token token() {
        Token tmp = token;
        token = nextToken();
        return tmp;
    }
}

