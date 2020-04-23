package Typechecker.Types;

public class ObjectType implements Type {
    public final String className;

    public ObjectType(final String className) {
        this.className = className;
    }
    @Override
    public boolean equals(final Object other) {
        return other instanceof ObjectType;
    }
    @Override
    public int hashCode() {
        return className.hashCode();
    }
    @Override
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
