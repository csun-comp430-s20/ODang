package Tokenizer.Tokens;

public class LeftCurlyToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof LeftCurlyToken);
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
