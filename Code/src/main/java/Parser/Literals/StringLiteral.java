package Parser.Literals;

import Parser.Expressions.Exp;

public class StringLiteral implements Exp {
    public final String name;

    public StringLiteral(final String name) {
        this.name = name;
    }
    @Override
    public boolean equals(final Object other) {
        return (other instanceof StringLiteral &&
                name.equals(((StringLiteral)other).name));
    }
    public String toString() {
        return String.format(this.getClass().getName() + "<" + name + ">");
    }
}
