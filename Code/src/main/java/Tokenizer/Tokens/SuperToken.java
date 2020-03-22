package Tokenizer.Tokens;

public class SuperToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof SuperToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
