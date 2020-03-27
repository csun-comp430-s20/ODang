package Parser.Declarations;

import Parser.Expressions.Exp;

public class ConstructorDeclarator implements Decl {
    public final Exp identifier;
    public final FormalParamList paramList;

    public ConstructorDeclarator(final Exp identifier, final FormalParamList paramList) {
        this.identifier = identifier;
        this.paramList = paramList;
    }

    public boolean equals(Object other) {
        if (other instanceof ConstructorDeclarator) {
            ConstructorDeclarator otherCD = (ConstructorDeclarator) other;
            return identifier.equals(otherCD.identifier) && paramList.equals(otherCD.paramList);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + identifier +
                " " + paramList + ")");
    }
}
