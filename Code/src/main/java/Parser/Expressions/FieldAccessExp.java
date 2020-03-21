package Parser.Expressions;

// <left>.<right>
public class FieldAccessExp implements Exp{
    public final Exp left;
    public final String dot;
    public final Exp right;

    public FieldAccessExp(final Exp left, final String dot, final Exp right) {
        this.left = left;
        this.dot = dot;
        this.right = right;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof FieldAccessExp) {
            final FieldAccessExp otherField = (FieldAccessExp)other;
            return (left.equals(otherField.left) &&
                    dot.equals(otherField.dot) &&
                    right.equals(otherField.right));
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "<" + left + dot + right + ">");
    }
}
