package Tokenizer.Tokens;

public class DotToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof DotToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
