package Parser.Expressions;

public class MultiplicativeExp implements Exp{
    public final Exp left;
    public final String operator;
    public final Exp right;

    public MultiplicativeExp(final Exp left, final String operator, final Exp right){
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public boolean equals(final Object other) {
        if (other instanceof MultiplicativeExp) {
            final MultiplicativeExp otherExp = (MultiplicativeExp) other;

            return (left.equals(otherExp.left) &&
                    right.equals(otherExp.right) &&
                    operator.equals(otherExp.operator));
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + "< " + left + operator + right + " >");
    }
}
