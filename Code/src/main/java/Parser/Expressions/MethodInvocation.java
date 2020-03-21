package Parser.Expressions;

import Parser.Literals.IdentifierLiteral;

public class MethodInvocation implements Exp {
    public final Exp exp;
    public final Exp argList;

    public MethodInvocation(final Exp exp, final Exp argList) {
        this.exp = exp;
        this.argList = argList;
    }

    public boolean equals(final Object other) {
        if (other instanceof MethodInvocation) {
            final MethodInvocation otherExp = (MethodInvocation) other;
            return exp.equals(otherExp.exp) &&
                    argList.equals(otherExp.argList);
        } else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "<" + "exp: " + exp + " arglist: " + argList + ">");
    }
}
