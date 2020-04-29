package Typechecker.Types;
@Deprecated
public class FunctionName {
    public final String name;

    public FunctionName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof FunctionName &&
                ((FunctionName) other).name.equals(name));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
