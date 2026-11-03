package integral.ast;

public enum FuncKind {
    SIN("sin"), COS("cos"), TAN("tan"), SEC("sec"), CSC("csc"), COT("cot"),
    LN("ln"), ASIN("asin"), ACOS("acos"), ATAN("atan"),
    ABS("abs"); // written as |...| in input and output, never as a name

    private final String text;

    FuncKind(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    // Looks up a function by its canonical name; ABS is intentionally not reachable by name
    public static FuncKind fromName(String name) {
        for (FuncKind kind : values()) {
            if (kind != ABS && kind.text.equals(name)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("Unknown function: " + name);
    }
}