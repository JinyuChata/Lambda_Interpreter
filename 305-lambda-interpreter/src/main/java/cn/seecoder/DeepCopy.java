package cn.seecoder;

public class DeepCopy {
    /**
     * 工具方法：深度复制AST
     *
     * @param origin 原AST
     * @return 复制后AST
     */
    public static AST deepCopy(AST origin) {
        if (origin instanceof Identifier) {
            return new Identifier(((Identifier) origin).param, ((Identifier) origin).name);
        } else if (origin instanceof Application) {
            return new Application(deepCopy(((Application) origin).t1), deepCopy(((Application) origin).t2));
        } else {
            return new Abstraction(((Abstraction) origin).param, ((Abstraction) origin).body);
        }
    }
}
