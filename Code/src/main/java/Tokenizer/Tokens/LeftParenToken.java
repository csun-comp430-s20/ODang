package Tokenizer.Tokens;

public class LeftParenToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof LeftParenToken);
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
