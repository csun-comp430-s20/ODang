package Parser.Literals;

public class IdentifierLiteral implements Literal {
    public final String name;

    public IdentifierLiteral(final String name) {
        this.name = name;
    }
    @Override
    public boolean equals(final Object other) {
        return (other instanceof IdentifierLiteral &&
                name.equals(((IdentifierLiteral)other).name));
    }
}
