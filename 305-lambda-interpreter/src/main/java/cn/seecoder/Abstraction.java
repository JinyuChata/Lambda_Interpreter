package cn.seecoder;

public class Abstraction extends AST{
    String param;
    AST body;

    public Abstraction(String param, AST body) {
        this.param = param;
        this.body = body;
    }

    @Override
    public String toString() {
        return "\\"   + "." + body + "";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Abstraction)) return false;
        Abstraction a = (Abstraction) obj;
        return param.equals(a.param) && body.equals(a.body);
    }
}
