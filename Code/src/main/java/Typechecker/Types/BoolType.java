package Typechecker.Types;

public class BoolType implements Type {
    @Override
    public boolean equals(final Object other) {
        return other instanceof BoolType;
    }
    //TODO implement hashcode
}
