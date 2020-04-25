package Typechecker.Types;

public class VoidType implements Type {
    @Override
    public boolean equals(final Object other) {
        return other instanceof VoidType;
    }
    @Override
    public int hashCode() {
        return 5;
    }

    @Override
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
