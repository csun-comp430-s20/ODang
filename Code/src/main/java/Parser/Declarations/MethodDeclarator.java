package Parser.Declarations;

import Parser.Expressions.Exp;

public class MethodDeclarator implements Decl {
    public final Exp identifier;
    public final Decl paramList;

    public MethodDeclarator(final Exp identifier, final Decl paramList) {
        this.identifier = identifier;
        this.paramList = paramList;
    }
    public boolean equals(Object other) {
        if (other instanceof MethodDeclarator) {
            MethodDeclarator otherMD = (MethodDeclarator) other;
            return identifier.equals(otherMD.identifier) && paramList.equals(otherMD.paramList);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + identifier + " " + paramList + ")");
    }
}
