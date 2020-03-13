package Parser.Literals;

public class StringLiteral implements Literal {
    public final String name;

    public StringLiteral(final String name) {
        this.name = name;
    }
    @Override
    public boolean equals(final Object other) {
        return (other instanceof StringLiteral &&
                name.equals(((StringLiteral)other).name));
    }
    public String toString() {
        return String.format(this.getClass().getName() + "<" + name + ">");
    }
}
