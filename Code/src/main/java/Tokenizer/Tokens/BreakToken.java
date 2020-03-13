package Tokenizer.Tokens;

public class BreakToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof BreakToken);
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
