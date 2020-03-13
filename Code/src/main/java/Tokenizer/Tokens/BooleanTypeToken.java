package Tokenizer.Tokens;

public class BooleanTypeToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof BooleanTypeToken);
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
