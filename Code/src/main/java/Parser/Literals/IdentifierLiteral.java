package Parser.Literals;

public class IdentifierLiteral implements Literal {
    public final String name;

    public IdentifierLiteral(final String name) {
        this.name = name;
    }
    public boolean equals(final IdentifierLiteral other) {
        return this.name.equals(other.name);
    }
}
