package Parser.Expressions;

public class PreIncrDecrExp implements Exp {
    public final Exp prefixExp;
    public final String preOp;

    public PreIncrDecrExp(final Exp prefixExp, final String preOp) {
        this.prefixExp = prefixExp;
        this.preOp = preOp;
    }

    public boolean equals(final Object other) {
        if (other instanceof PreIncrDecrExp) {
            final PreIncrDecrExp otherExp = (PreIncrDecrExp)other;
            return (prefixExp.equals(otherExp.prefixExp) &&
                    preOp.equals(otherExp.preOp));
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + "<" + preOp + prefixExp + " >");
    }
}

