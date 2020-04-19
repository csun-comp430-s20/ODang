package Typechecker.Types;

public class BoolType implements Type {
    @Override
    public boolean equals(final Object other) {
        return other instanceof BoolType;
    }
    @Override
    public int hashCode() {
        return 4;
    }
    @Override
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
