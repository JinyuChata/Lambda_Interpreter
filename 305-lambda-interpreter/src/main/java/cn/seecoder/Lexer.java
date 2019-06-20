package cn.seecoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    public String source;
    public int index = 0;
    public Token token;

    public Lexer(String s) {
        source = s;
        token = nextTokenWithPrint();
    }

    /**
     * print token需求
     * @return 下一个token
     */
    public Token nextTokenWithPrint() {
        Token tmp = null;
        try {
            tmp = nextToken();
        } catch (InvalidInputFormatException e) {
            e.printInfo();
            System.exit(1);
        }
        System.out.println(tmp);
        return tmp;
    }

    /**
     * 读取下一个token
     * @return 下一个token
     * @throws InvalidInputFormatException 输入格式错误
     */
    public Token nextToken() throws InvalidInputFormatException{
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

        // 匹配空格
        Pattern pSpace = Pattern.compile("\\s");
        Matcher pSMatcher = pSpace.matcher(Character.toString(nextChr));
        if (pSMatcher.find()) {
            index++;
            return nextToken();
        }

        // 匹配id, 格式[a-z][a-zA-Z]*
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
            // flag true: 已经不是第一个字符
            if (flag) {
                flag = false;
                pDigit = Pattern.compile("[a-zA-Z]");
                pDMatcher = pDigit.matcher(Character.toString(nextChr));
            } else {
                pDMatcher.reset(Character.toString(nextChr));
            }

        }

        // flag false: 读到有效的id
        if (!flag) {
            token = new Token(Token.LCID, sb.toString());
            return token;
        }

        // flag true: 未读到有效的id
        throw new InvalidInputFormatException(String.valueOf(source.charAt(index)), index, source);
    }

    /**
     * index是否读到最后
     * @return boolean
     */
    private boolean checkIndex() {
        return index < source.length();
    }

    /**
     * 安全地为index自增1
     * @throws IndexOutOfBoundsException EOF的条件
     */
    private void incIndex() throws IndexOutOfBoundsException {
        index++;
        if (!checkIndex()) throw new IndexOutOfBoundsException();
    }

    /**
     * 返回下一个token是否匹配type
     * @param tokenType 要匹配的type
     * @return boolean: 是否匹配
     */
    public boolean next(int tokenType) {
        boolean isType = false;
        try {
            isType = tokenType == token.type;
        } catch (NullPointerException e) {
            System.out.println("Invalid lambda expression.");
            System.out.println("Check your input.");
            System.exit(1);
        }
        return isType;
    }

    /**
     * 和 next 一样, 但如果匹配的话会跳过
     * @param tokenType 要匹配的type
     * @return 是否匹配
     */
    public boolean skip(int tokenType) {
        if (next(tokenType)) {
            token = nextTokenWithPrint();
            return true;
        }
        return false;
    }

    /**
     * 断言 next 方法返回 true, 并skip
     * Version 2: 若false则抛出异常
     * @param tokenType 下一个token应有的type
     * @throws InvalidSyntaxException false 意味着语法错误
     */
    public void match(int tokenType) throws InvalidSyntaxException {
//        assert next(tokenType);
        if (!next(tokenType)) {
            throw new InvalidSyntaxException(source, index);
        }
        token = nextTokenWithPrint();
    }


    /**
     * 断言 next 方法返回 true, 并返回token
     * Version 2: 若false则抛出异常
     * @param tokenType 下一个token应有的type
     * @return 匹配的token
     * @throws InvalidSyntaxException false 意味着语法错误
     */
    public Token token(int tokenType) throws InvalidSyntaxException {
//        assert next(tokenType);
        if (!next(tokenType)) {
            throw new InvalidSyntaxException(source, index);
        }
        Token tmp = token;
        token = nextTokenWithPrint();
        return tmp;
    }

}

