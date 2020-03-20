package Tokenizer.Tokens;

public class CommaToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof CommaToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
