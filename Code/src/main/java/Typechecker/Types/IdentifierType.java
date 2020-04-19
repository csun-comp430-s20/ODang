package Typechecker.Types;

public class IdentifierType implements Type {
    @Override
    public boolean equals(final Object other) {
        return other instanceof IdentifierType;
    }
    @Override
    public int hashCode() {
        return 0;
    }
    @Override
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
