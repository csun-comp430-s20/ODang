package Typechecker.Types;

public class IntType implements Type {
    @Override
    public boolean equals(final Object other) {
        return other instanceof IntType;
    }
    //TODO implement hashcode
}
