package Tokenizer.Tokens;

public class ElseToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof ElseToken);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
