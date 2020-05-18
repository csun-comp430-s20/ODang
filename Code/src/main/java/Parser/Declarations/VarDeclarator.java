package Parser.Declarations;

import Parser.Expressions.Exp;

public class VarDeclarator implements Decl {
    public final Exp identifier;
    public final Exp exp;

    public VarDeclarator(final Exp identifier, final Exp exp) {
        this.identifier = identifier;
        this.exp = exp;
    }

    public boolean equals(Object other) {
        if (other instanceof VarDeclarator) {
            VarDeclarator otherVarDecl = (VarDeclarator) other;
            return identifier.equals(otherVarDecl.identifier) &&
                    exp.equals(otherVarDecl.exp);
        }
        else return false;
    }

    public String toString() {
        if (exp == null)
            return String.format(this.getClass().getSimpleName()) + " (" + identifier + ")";
        else
            return String.format(this.getClass().getSimpleName() + " (" + identifier + " = " + exp + ")");
    }
}
