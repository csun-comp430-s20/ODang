package Tokenizer.Tokens;

public class IntegerToken implements Token {
    public final int value;

    public IntegerToken(final int value) {
        this.value = value;
    }

    public boolean equals(final Object other) {
        if (other instanceof IntegerToken) {
            final IntegerToken otherVal = (IntegerToken) other;
            return value == otherVal.value;
        } else
            return false;
    }
    public String toString() {
        return String.format(this.getClass().getName() + "<" + value + ">");
    }
}
