package Parser.Expressions;

import Parser.Types.*;

public class CastExp implements Exp {
    public final Type type;
    public final Exp exp;

    public CastExp(final Type type, final Exp exp) {
        this.type = type;
        this.exp = exp;
    }

    public boolean equals(final Object other) {
        if (other instanceof CastExp) {
            final CastExp otherExp = (CastExp) other;

            return type.equals(otherExp.type) && exp.equals(otherExp.exp);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " type: < " + type + " >" +
                " exp: <" + exp + ">");
    }
}
