package cn.seecoder;

public class Identifier extends AST{
    int param; int size;
    String name = "";

    public Identifier(int param, int size, String name) {
        this.param = param; this.size = size; this.name = name;
    }

    @Override
    public String toString() {
        return ""+param;
    }


    @Override
    public int hashCode() {
        return 0;
    }


//    @Override
//    public boolean equals(Object obj) {
//        if (!(obj instanceof Identifier)) return false;
//        Identifier i = (Identifier) obj;
//
//        return (param == i.param && size == i.size && nameForFreeVariables.equals(i.nameForFreeVariables) && ctx.equals(i.ctx));
//    }
}
