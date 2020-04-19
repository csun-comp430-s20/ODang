package Typechecker.Types;

public class NullType implements Type {
    @Override
    public boolean equals(final Object other) {
        return other instanceof NullType;
    }
    @Override
    public int hashCode() {
        return 3;
    }
    @Override
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
