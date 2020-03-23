package Parser.Expressions;

public class FieldAccessExp implements Exp {
    public final Exp left;
    public final Exp right;

    public FieldAccessExp(final Exp left, final Exp right) {
        this.left = left;
        this.right = right;
    }

    public boolean equals(final Object other) {
        if (other instanceof FieldAccessExp) {
            final FieldAccessExp asFieldAccessExp = (FieldAccessExp) other;
            return left.equals(asFieldAccessExp.left) &&
                    right.equals(asFieldAccessExp.right);
        } else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + left + "." + right);
    }
}
