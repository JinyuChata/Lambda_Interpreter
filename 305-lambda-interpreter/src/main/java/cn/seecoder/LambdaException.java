package cn.seecoder;

public class LambdaException extends Exception {
    public static void getInfo(String origin, int index) {
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
    String origin;
    int index;

    public InvalidSyntaxException(String origin, int index) {
        this.origin = origin;
        this.index = index;
    }

    public void printInfo() {
        System.out.println("---------------------");
        System.out.println("Syntax Error:");
        getInfo(this.origin, this.index);
        System.out.println("Check your syntax.");
    }


}

class InvalidInputFormatException extends LambdaException {
    String info;
    String origin;
    int index;

    public InvalidInputFormatException(String info, int index, String origin) {
        this.info = info;
        this.index = index;
        this.origin = origin;
    }

    public void printInfo() {
        System.out.println("---------------------");
        System.out.println("Invalid input format:");
        InvalidSyntaxException.getInfo(this.origin, this.index);
        System.out.println("Check your input.");
    }
}