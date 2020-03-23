package Parser.Expressions;

//TODO might be redundant
public class PrimaryExp implements Exp {
    public final Exp exp;

    public PrimaryExp(final Exp exp) {
        this.exp = exp;
    }

    public boolean equals(final Object other) {
        if (other instanceof PrimaryExp) {
            final PrimaryExp otherExp = (PrimaryExp) other;

            return exp.equals(otherExp.exp);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "< " + exp + " >");
    }

}