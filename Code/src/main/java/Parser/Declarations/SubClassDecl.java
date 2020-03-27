package Parser.Declarations;

import Parser.Expressions.Exp;
import Parser.Types.Type;

public class SubClassDecl extends ClassDecl {

    public final Type classType;

    public SubClassDecl(final Exp identifier, final Type classType, final Decl classBody) {
        super(identifier, classBody);
        this.classType = classType;
    }

    public boolean equals(Object other) {
        if (other instanceof SubClassDecl) {
            SubClassDecl otherSCL = (SubClassDecl) other;
            return identifier.equals(otherSCL.identifier) &&
                    classBody.equals(otherSCL.classBody) &&
                    classType.equals(otherSCL.classType);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSuperclass().getSimpleName() +
                "(" + identifier +  " extends " + classType + " {" + classBody + "})");
    }
}
