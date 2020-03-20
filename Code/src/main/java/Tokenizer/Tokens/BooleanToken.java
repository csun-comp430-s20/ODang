package Tokenizer.Tokens;

public class BooleanToken implements Token {
    public final boolean value;

    public BooleanToken(final boolean value) {
        this.value = value;
    }
    public boolean equals(final Object other) {
        if (other instanceof BooleanToken) {
            final BooleanToken otherVal = (BooleanToken) other;
            return value == otherVal.value;
        } else
            return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "<" + value + ">");
    }
}
