package Tokenizer.Tokens;

public class PrintlnToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof PrintlnToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
