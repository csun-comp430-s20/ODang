package Tokenizer.Tokens;

public class RightCurlyToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof RightCurlyToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
