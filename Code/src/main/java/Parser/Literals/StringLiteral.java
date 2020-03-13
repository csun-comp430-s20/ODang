package Parser.Literals;

public class StringLiteral implements Literal {
    public final String name;

    public StringLiteral(final String name) {
        this.name = name;
    }
    public boolean equals(final StringLiteral other) {
        return this.name.equals(other.name);
    }
}
