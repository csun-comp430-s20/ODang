package Parser.Literals;

public class IntegerLiteral implements Literal {
    public final int value;

    public IntegerLiteral(final int value) {
        this.value = value;
    }
    public boolean equals(final IntegerLiteral other) {
        return this.value == other.value;
    }
}
