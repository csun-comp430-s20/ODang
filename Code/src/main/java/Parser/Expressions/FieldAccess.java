package Parser.Expressions;

public class FieldAccess implements Exp {
    public final Exp primary;
    public final Exp identifier;

    public FieldAccess(final Exp primary, final Exp identifier) {
        this.primary = primary;
        this.identifier = identifier;
    }

    public boolean equals(final Object other) {
        if (other instanceof FieldAccess) {
            final FieldAccess asFieldAccess = (FieldAccess) other;
            return primary.equals(asFieldAccess.primary) &&
                    identifier.equals(asFieldAccess.identifier);
        } else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "<" + primary + "." + identifier + ">");
    }
}
