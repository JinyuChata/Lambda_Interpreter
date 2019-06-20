package cn.seecoder;

public class Identifier extends AST{
    int param;  // 德布鲁因值
    String name = "";

    public Identifier(int param, String name) {
        this.param = param;
        this.name = name;
    }

    @Override
    public String toString() {
        if (param == -1) return name;
        // 自由变量 不输出德布鲁因值
        return ""+param;
    }

}
