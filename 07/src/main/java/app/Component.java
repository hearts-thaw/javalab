package app;

public class Component {
    private int int1;
    private static String str;

    public Component(int int1, String str) {
        this.int1 = int1;
        this.str = str;
    }

    private int methodWithArgs(int a, int b) {
        return int1 + a + b;
    }
}
