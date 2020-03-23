package Parser.Expressions;

public class NegateUnaryExp implements Exp {
    public final Exp exp;

    public NegateUnaryExp(final Exp exp) {
        this.exp = exp;
    }

    public boolean equals(final Object other) {
        if (other instanceof NegateUnaryExp) {
            final NegateUnaryExp otherExp = (NegateUnaryExp) other;

            return exp.equals(otherExp.exp);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "< " + exp + " >");
    }

}
