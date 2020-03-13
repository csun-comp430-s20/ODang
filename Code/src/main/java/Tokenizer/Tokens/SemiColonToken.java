package Tokenizer.Tokens;

public class SemiColonToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof SemiColonToken);
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
