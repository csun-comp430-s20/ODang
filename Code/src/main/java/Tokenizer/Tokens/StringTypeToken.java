package Tokenizer.Tokens;

public class StringTypeToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof StringTypeToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
