package Parser.Literals;

import Parser.Expressions.Exp;

public class BooleanLiteral implements Exp {
    public final boolean value;

    public BooleanLiteral(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof BooleanLiteral) {
            final BooleanLiteral asBool = (BooleanLiteral) other;
            return value == asBool.value;
        } else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "<" + value + ">");
    }
}
