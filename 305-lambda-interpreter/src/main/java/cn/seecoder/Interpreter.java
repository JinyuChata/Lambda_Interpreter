package cn.seecoder;

public class Interpreter {
    Parser parser;
    AST astAfterParser;

    public Interpreter(Parser p) {
        parser = p;
        astAfterParser = p.parse();
        //System.out.println("After parser:"+astAfterParser.toString());
    }

    /**
     * 包装的计算函数
     *
     * @return 计算后的表达式
     */
    public AST eval() {
        AST before = astAfterParser;
        return eval(before);
    }

    /**
     * 真正的计算函数（静态工具类）
     *
     * @param ast 计算前的表达式
     * @return 计算后的表达式
     */
    public static AST eval(AST ast) {
        while (true) {
            if (ast instanceof Application) {
                // ast: Application: 分左右树

                // 处理右树
                if (((Application) ast).t2 instanceof Application) {
                    ((Application) ast).t2 = eval(((Application) ast).t2);
                } else if (((Application) ast).t2 instanceof Abstraction) {
                    ((Abstraction) ((Application) ast).t2).body = eval(((Abstraction) ((Application) ast).t2).body);
                } else if (((Application) ast).t1 instanceof Identifier) {
                    return ast;
                }

                // 处理左树
                if (((Application) ast).t1 instanceof Abstraction) {
                    ast = finalSub(((Application) ast).t2, ((Application) ast).t1);
                    ast = ((Abstraction) ast).body;
                } else if (((Application) ast).t1 instanceof Application) {
                    // 此处消除循环: (id (id id))
//                    String tmp1 = ((Application) ast).t1.toString();
                    ((Application) ast).t1 = eval(((Application) ast).t1);
//                    if (tmp1.equals(((Application) ast).t1.toString())) return ast;
                    if (((Application) ast).t1 instanceof Application) return ast;
                } else {
                    return ast;
                }

            } else if (ast instanceof Abstraction) {
                // ast: Abstraction: 处理body部分即可
                Abstraction tmp = ((Abstraction) ast);
                tmp.body = eval(tmp.body);
                return tmp;
            } else {
                // ast: identifier: 直接return
                return ast;
            }
        }
    }

    /**
     * 替换函数的最终处理：自由变量德布鲁因 -1
     *
     * @param insertTerm 要插入的AST
     * @param dest       目的地
     * @return 处理后的AST
     */
    public static AST finalSub(AST insertTerm, AST dest) {
        AST tmp = substitute(DeepCopy.deepCopy(insertTerm), dest, 0);
        return shift(-1, 0, tmp);
    }

    /**
     * 替换操作主干
     *
     * @param insertTerm 要插入的AST
     * @param dest       目的地
     * @param index      当前所在层数
     * @return 替换后的AST
     */
    public static AST substitute(AST insertTerm, AST dest, int index) {
        if (dest instanceof Abstraction) {
            // Abstraction: 进入body部分且加一层
            AST destBody = ((Abstraction) dest).body;
            destBody = substitute(DeepCopy.deepCopy(insertTerm), destBody, index + 1);
            return new Abstraction(((Abstraction) dest).param, destBody);
        } else if (dest instanceof Application) {
            // Application: 分左树和右树替换
            AST t1 = ((Application) dest).t1;
            AST t2 = ((Application) dest).t2;
            return new Application(substitute(DeepCopy.deepCopy(insertTerm), t1, index), substitute(DeepCopy.deepCopy(insertTerm), t2, index));
        } else {
            // Identifier: 若进入后的index层数恰好是id的param (对应), 则替换
            if (((Identifier) dest).param == index - 1) {
                AST tmp = shift(index, 0, DeepCopy.deepCopy(insertTerm));
                return tmp;
            }
            // 否则 原样返回
            return dest;
        }
    }

    /**
     * 改变自由变量的德布鲁因值
     *
     * @param by         需要变化的层数增量
     * @param from       insertItem的当前层数
     * @param insertItem 要替换进去的insertItem
     * @return AST
     */
    public static AST shift(int by, int from, AST insertItem) {
        if (insertItem instanceof Identifier) {
            // param > from: 是自由变量
            ((Identifier) insertItem).param += (((Identifier) insertItem).param >= from ? by : 0);
        } else if (insertItem instanceof Application) {
            ((Application) insertItem).t1 = shift(by, from, DeepCopy.deepCopy(((Application) insertItem).t1));
            ((Application) insertItem).t2 = shift(by, from, DeepCopy.deepCopy(((Application) insertItem).t2));
        } else {
            // Abstraction
            ((Abstraction) insertItem).body = shift(by, from + 1, DeepCopy.deepCopy(((Abstraction) insertItem).body));
        }
        return insertItem;
    }

    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m " + SUCC + ") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n" + PRED + "m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x." + FALSE + ")" + TRUE + ")";
    static String LEQ = "(\\m.\\n." + ISZERO + "(" + SUB + "m n))";
    static String EQ = "(\\m.\\n." + AND + "(" + LEQ + "m n)(" + LEQ + "n m))";
    static String MAX = "(\\m.\\n." + IF + "(" + LEQ + " m n)n m)";
    static String MIN = "(\\m.\\n." + IF + "(" + LEQ + " m n)m n)";

    private static String app(String func, String x) {
        return "(" + func + x + ")";
    }

    private static String app(String func, String x, String y) {
        return "(" + "(" + func + x + ")" + y + ")";
    }

    private static String app(String func, String cond, String x, String y) {
        return "(" + func + cond + x + y + ")";
    }

    public static void main(String[] args) {
        // write your code here

        String[] sources = {
                ZERO,//0
                ONE,//1
                TWO,//2
                THREE,//3
                app(PLUS, ZERO, ONE),//4
                app(PLUS, TWO, THREE),//5
                app(POW, TWO, TWO),//6
                app(PRED, ONE),//7
                app(PRED, TWO),//8
                app(SUB, FOUR, TWO),//9
                app(AND, TRUE, TRUE),//10
                app(AND, TRUE, FALSE),//11
                app(AND, FALSE, FALSE),//12
                app(OR, TRUE, TRUE),//13
                app(OR, TRUE, FALSE),//14
                app(OR, FALSE, FALSE),//15
                app(NOT, TRUE),//16
                app(NOT, FALSE),//17
                app(IF, TRUE, TRUE, FALSE),//18
                app(IF, FALSE, TRUE, FALSE),//19
                app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
                app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
                app(ISZERO, ZERO),//22
                app(ISZERO, ONE),//23
                app(LEQ, THREE, TWO),//24
                app(LEQ, TWO, THREE),//25
                app(EQ, TWO, FOUR),//26
                app(EQ, FIVE, FIVE),//27
                app(MAX, ONE, TWO),//28
                app(MAX, FOUR, TWO),//29
                app(MIN, ONE, TWO),//30
                app(MIN, FOUR, TWO),//31
        };

//        for (int i = 4; i < 10; i++) {
//
//
//            String source = PLUS;
//            int len = source.length();
//            System.out.println(len);
//            Lexer lexer = new Lexer(source);
//            Parser parser = new Parser(lexer);
//
//            Interpreter interpreter = new Interpreter(parser);
//
//            AST result = interpreter.eval();
//
//            System.out.println(i + ":" + result.toString());
//
//        }

        Lexer lexer = new Lexer(app(POW, TWO, TWO));
        Parser parser = new Parser(lexer);
        Interpreter interpreter = new Interpreter(parser);
        System.out.println(interpreter.eval());

        // Exception 1:
//        Lexer lexer = new Lexer("\\b.a");
//        Parser parser = new Parser(lexer);
//        Interpreter interpreter = new Interpreter(parser);
//        System.out.println(interpreter.eval());


//         Exception 2:
//        Lexer lexer = new Lexer("(\\.\\n.\\f.\\x.f (n f x)) " + ZERO);
//        Parser parser = new Parser(lexer);
//        Interpreter interpreter = new Interpreter(parser);
//        System.out.println(interpreter.eval());
    }
}