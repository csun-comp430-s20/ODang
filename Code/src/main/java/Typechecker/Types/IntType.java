package Typechecker.Types;

public class IntType implements Type {
    @Override
    public boolean equals(final Object other) {
        return other instanceof IntType;
    }
    @Override
    public int hashCode() {
        return 1;
    }
}
