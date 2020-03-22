package Parser.Types;


public class PrimitiveType implements Type {
    public final Type type;

    public PrimitiveType(final Type type) {
        this.type = type;
    }

    public boolean equals(Object other) {
        if (other instanceof PrimitiveType) {
            final PrimitiveType otherType = (PrimitiveType) other;
            return this.type.equals(otherType.type);
        } else
            return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " type <" + type + ">");
    }
}
