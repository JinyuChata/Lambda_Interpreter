package cn.seecoder;

public class Interpreter {
    Parser parser;
    AST astAfterParser;

    public Interpreter(Parser p){
        parser = p;
        astAfterParser = p.parse();
        //System.out.println("After parser:"+astAfterParser.toString());
    }

    public AST eval() {
        AST before = astAfterParser;
        return eval(before, 0, before);
    }



    public static AST eval(AST ast, int i ,AST org) {
        while (true) {
            if (ast instanceof Application) {
                if (((Application) ast).t2 instanceof Application) {
                    ((Application) ast).t2 = eval(((Application) ast).t2, i , org);
                } else if (((Application) ast).t2 instanceof Abstraction) {
                    ((Abstraction) ((Application) ast).t2).body = eval(((Abstraction) ((Application) ast).t2).body, i+1 , org);
                } else if (((Application) ast).t1 instanceof Identifier) {
//                    System.out.println(org);
                    return ast;
                }

                if (((Application) ast).t1 instanceof Abstraction) {
                    ast = finalSub(((Application) ast).t2, ((Application) ast).t1);
//                    if (ast.toString().equals("\\f.\\x.(x (x2 x0))"))
                    ast = ((Abstraction) ast).body;
                } else if (((Application) ast).t1 instanceof Application) {
                    String tmp1 = ((Application) ast).t1.toString();
                    ((Application) ast).t1 = eval(((Application) ast).t1, i+1 , org);
                    if (tmp1.equals(((Application) ast).t1.toString())) return ast;
                } else {
//                    System.out.println(org);
                    return ast;
                }

            } else if (ast instanceof Abstraction) {

                Abstraction tmp = ((Abstraction) ast);
                tmp.body = eval(tmp.body, i+1, org);
//                System.out.println(org);
                return tmp;
            } else {
//                System.out.println(org);
                return ast;
            }
        }
    }

    public static AST finalSub(AST insertTerm, AST dest) {
        AST tmp = substitute(DeepCopy.deepCopy(insertTerm), dest, 0);
        return shift(-1, 0, tmp);
    }

    public static AST substitute(AST insertTerm, AST dest, int index) {
        if (dest instanceof Abstraction) {
            AST destBody = ((Abstraction) dest).body;
            destBody = substitute(DeepCopy.deepCopy(insertTerm), destBody, index+1);
            return new Abstraction(((Abstraction) dest).param, destBody);
        } else if (dest instanceof Application) {
            AST t1 = ((Application) dest).t1;
            AST t2 = ((Application) dest).t2;
            return new Application(substitute(DeepCopy.deepCopy(insertTerm), t1, index), substitute(DeepCopy.deepCopy(insertTerm), t2, index));
        } else {
            if (((Identifier) dest).param == index - 1) {
                AST tmp = shift(index, 0, DeepCopy.deepCopy(insertTerm));
                return tmp;
            }
            return dest;
        }
    }

    public static AST shift(int by, int from, AST node) {
        if (node instanceof Identifier) {
            ((Identifier) node).param += (((Identifier) node).param >= from ? by : 0);
        } else if (node instanceof Application) {
            ((Application) node).t1 = shift(by, from, DeepCopy.deepCopy(((Application) node).t1));
            ((Application) node).t2 = shift(by, from, DeepCopy.deepCopy(((Application) node).t2));
        } else {
            // Abstraction
            ((Abstraction) node).body = shift(by, from + 1, DeepCopy.deepCopy(((Abstraction) node).body));
        }
        return node;
    }

    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m "+SUCC+") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n"+PRED+"m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x."+FALSE+")"+TRUE+")";
    static String LEQ = "(\\m.\\n."+ISZERO+"("+SUB+"m n))";
    static String EQ = "(\\m.\\n."+AND+"("+LEQ+"m n)("+LEQ+"n m))";
    static String MAX = "(\\m.\\n."+IF+"("+LEQ+" m n)n m)";
    static String MIN = "(\\m.\\n."+IF+"("+LEQ+" m n)m n)";

    private static String app(String func, String x){
        return "(" + func + x + ")";
    }
    private static String app(String func, String x, String y){
        return "(" +  "(" + func + x +")"+ y + ")";
    }
    private static String app(String func, String cond, String x, String y){
        return "(" + func + cond + x + y + ")";
    }

    public static void main(String[] args) {
        // write your code here
        Lexer lexer = new Lexer(app(PRED, ONE));
        Parser parser = new Parser(lexer);
        AST ast = parser.parse();
    }
}