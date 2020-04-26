package Typechecker.Types;

public class ClassType implements Type {
    public final String className;

    public ClassType(final String className) {
        this.className = className;
    }
    @Override
    public boolean equals(final Object other) {
        return other instanceof ClassType;
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
