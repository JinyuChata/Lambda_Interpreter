package cn.seecoder;

public class LambdaException extends Exception {
    /**
     * 所有异常共同的工具方法
     * 输出异常信息的公共部分
     *
     * @param origin 发生异常的字符串
     * @param index  发生异常的位置
     */
    static void getInfo(String origin, int index) {
        System.out.println("\"" + origin + "\"");
        System.out.print(" ");
        for (int i = 0; i < origin.length(); i++) {
            if (i == index) {
                System.out.print("^");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}

class InvalidSyntaxException extends LambdaException {
    private String origin;
    private int index;

    InvalidSyntaxException(String origin, int index) {
        this.origin = origin;
        this.index = index;
    }

    void printInfo() {
        System.out.println("---------------------");
        System.out.println("Syntax Error:");
        getInfo(this.origin, this.index);
        System.out.println("Check your syntax.");
    }


}

class InvalidInputFormatException extends LambdaException {
    private String info;
    private String origin;
    private int index;

    InvalidInputFormatException(String info, int index, String origin) {
        this.info = info;
        this.index = index;
        this.origin = origin;
    }

    public void printInfo() {
        System.out.println("---------------------");
        System.out.println("Invalid input format: " + info);
        InvalidSyntaxException.getInfo(this.origin, this.index);
        System.out.println("Check your input.");
    }
}