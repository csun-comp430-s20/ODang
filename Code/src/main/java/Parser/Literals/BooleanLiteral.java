package Parser.Literals;

import Tokenizer.Tokens.BooleanToken;

public class BooleanLiteral implements Literal {
    public final boolean value;

    public BooleanLiteral(final boolean value) {
        this.value = value;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof BooleanLiteral &&
                value == ((BooleanLiteral)other).value);
    }
    public String toString() {
        return String.format(this.getClass().getName() + "<" + value + ">");
    }
}
