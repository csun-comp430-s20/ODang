package Parser.Types;

public class BooleanType implements Type{

    public boolean equals(Object other) {
        return (other instanceof BooleanType);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
