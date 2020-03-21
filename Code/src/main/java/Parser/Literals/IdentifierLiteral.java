package Parser.Literals;

import Parser.Expressions.Exp;

public class IdentifierLiteral implements Exp {
    public final String name;

    public IdentifierLiteral(final String name) {
        this.name = name;
    }

    public boolean equals(final Object other) {
        if (other instanceof IdentifierLiteral) {
            final IdentifierLiteral asId = (IdentifierLiteral) other;
            return name.equals(asId.name);
        } else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "<" + name + ">");
    }
}
