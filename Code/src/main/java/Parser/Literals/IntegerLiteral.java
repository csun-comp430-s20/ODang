package Parser.Literals;

import Parser.Expressions.Exp;

public class IntegerLiteral implements Exp {
    public final int value;

    public IntegerLiteral(final int value) {
        this.value = value;
    }
    @Override
    public boolean equals(final Object other) {
        return (other instanceof IntegerLiteral &&
                value == ((IntegerLiteral)other).value);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "<" + value + ">");
    }
}
