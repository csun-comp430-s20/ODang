package Parser.Literals;

import Parser.Expressions.Exp;

public class NullLiteral implements Exp {

    @Override
    public boolean equals(final Object other) {
        return (other instanceof NullLiteral);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
