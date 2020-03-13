package Tokenizer.Tokens;

public class ForToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof ForToken);
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
