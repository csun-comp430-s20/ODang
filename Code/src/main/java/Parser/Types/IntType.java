package Parser.Types;

public class IntType implements Type {
    public boolean equals(Object other) {
        return (other instanceof IntType);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
