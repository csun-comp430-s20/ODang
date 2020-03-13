package Tokenizer.Tokens;

public class ReturnToken implements Token {
    public boolean equals(final Object other) {
        return (other instanceof ReturnToken);
    }
    public String toString() {
        return String.format(this.getClass().getName());
    }
}
