package Parser.Literals;

public class IntegerLiteral implements Literal {
    public final int value;

    public IntegerLiteral(final int value) {
        this.value = value;
    }
    @Override
    public boolean equals(final Object other) {
        return (other instanceof IntegerLiteral &&
                value == ((IntegerLiteral)other).value);
    }
}
