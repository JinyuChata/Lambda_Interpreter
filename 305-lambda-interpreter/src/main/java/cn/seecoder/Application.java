package cn.seecoder;

public class Application extends AST{
    AST t1, t2;

    public Application(AST t1, AST t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    @Override
    public String toString() {
        return "(" + t1.toString() + " " + t2.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Application)) return false;
        if (((Application)obj).t1.equals(t1) && ((Application)obj).t2.equals(t2)) return true;
        return false;
    }
}
