package Parser.Declarations;

import Parser.Expressions.*;

public class OverloadDecl implements Decl{
    public final Exp identifier;
    public final String op;
    public final Decl paramList;

    public OverloadDecl(final Exp identifier, final String op, final Decl paramList) {
        this.identifier = identifier;
        this.op = op;
        this.paramList = paramList;
    }
    public boolean equals(Object other) {
        if (other instanceof OverloadDecl) {
            OverloadDecl otherOverload = (OverloadDecl) other;
            return identifier.equals(otherOverload.identifier) && op.equals(otherOverload.op) &&
                    paramList.equals(otherOverload.paramList);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + identifier + " " + op + " " + paramList + ")");
    }
}
