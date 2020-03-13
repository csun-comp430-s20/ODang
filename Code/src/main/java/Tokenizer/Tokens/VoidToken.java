package Tokenizer.Tokens;

public class VoidToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof VoidToken);
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
