package Tokenizer.Tokens;

public class ThisToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof ThisToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
