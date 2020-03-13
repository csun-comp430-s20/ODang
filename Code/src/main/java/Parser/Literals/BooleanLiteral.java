package Parser.Literals;

public class BooleanLiteral implements Literal {
    public final boolean value;

    public BooleanLiteral(final boolean value) {
        this.value = value;
    }
    public boolean equals(final BooleanLiteral other) {
        return this.value == other.value;
    }
}
