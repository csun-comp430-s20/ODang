package Parser.Types;

public class StringType implements Type {
    public boolean equals(Object other) {
        return (other instanceof StringType);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
