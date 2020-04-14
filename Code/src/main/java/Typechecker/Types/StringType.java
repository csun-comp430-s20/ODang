package Typechecker.Types;

public class StringType implements Type {
    @Override
    public boolean equals(final Object other) {
        return other instanceof StringType;
    }
    @Override
    public int hashCode() {
        return 2;
    }
}
