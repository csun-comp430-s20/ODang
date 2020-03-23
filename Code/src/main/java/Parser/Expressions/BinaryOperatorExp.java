package Parser.Expressions;

public class BinaryOperatorExp implements Exp {

    public final String op;
    public final Exp left;
    public final Exp right;

    public BinaryOperatorExp(final String op, final Exp left, final Exp right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public boolean equals(Object other) {
        if (other instanceof BinaryOperatorExp) {
            BinaryOperatorExp otherBinOp = (BinaryOperatorExp) other;
            return (op.equals(otherBinOp.op) &&
                    left.equals(otherBinOp.left) &&
                    right.equals(otherBinOp.right));
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (" + left + " " + op + " " + right + ")");
    }
}
