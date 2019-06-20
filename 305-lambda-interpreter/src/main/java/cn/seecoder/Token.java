package cn.seecoder;

public class Token {
    public static final int LPAREN = 1;
    public static final int RPAREN = 2;
    public static final int LAMBDA = 3;
    public static final int DOT = 4;
    public static final int LCID = 5;
    public static final int EOF = 0;

    String value;
    int type;

    public Token(int type) {
        this.type = type;
    }

    public Token(int type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 供给Parser得到index使用
     *
     * @param obj 比较的对象
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Token)) return false;
        if (((Token) obj).type != LCID) return ((Token) obj).type == type;
        return ((Token) obj).value.equals(value);
    }

    @Override
    public String toString() {
        String typeStr;
        switch (type) {
            case 0:
                typeStr = "EOF";
                break;
            case 1:
                typeStr = "LPAREN";
                break;
            case 2:
                typeStr = "RPAREN";
                break;
            case 3:
                typeStr = "LAMBDA";
                break;
            case 4:
                typeStr = "DOT";
                break;
            case 5:
                typeStr = "LCID";
                break;
            default:
                typeStr = "FALSE";

        }
        return typeStr;
    }
}
