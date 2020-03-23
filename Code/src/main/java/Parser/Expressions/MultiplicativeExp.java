package Parser.Expressions;

public class MultiplicativeExp implements Exp {

    public final String op;
    public final Exp left;
    public final Exp right;


    public MultiplicativeExp(final String op, final Exp left, final Exp right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public boolean equals(Object other) {
        if (other instanceof MultiplicativeExp) {
            MultiplicativeExp otherMult = (MultiplicativeExp) other;
            return (op.equals(otherMult.op) &&
                    left.equals(otherMult.left) &&
                    right.equals(otherMult.right));
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (" + left + " " + op + " " + right + ")");
    }
}
