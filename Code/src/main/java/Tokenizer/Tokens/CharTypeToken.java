package Tokenizer.Tokens;

public class CharTypeToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof CharTypeToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
