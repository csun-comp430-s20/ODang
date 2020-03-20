package Tokenizer.Tokens;

public class NullToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof NullToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
