package Parser.Declarations;

import Parser.Expressions.Exp;
import Parser.Types.Type;

public class ClassDecl implements Decl {

    public final Exp identifier;
    public final Decl classBody;

    public ClassDecl(final Exp identifier, final Decl classBody) {
        this.identifier = identifier;
        this.classBody = classBody;
    }
    public boolean equals(Object other) {
        if (other instanceof ClassDecl) {
            ClassDecl otherClassDecl = (ClassDecl) other;
            return identifier.equals(otherClassDecl.identifier) &&
                    classBody.equals(otherClassDecl.classBody);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + identifier +
                " " + classBody + ")");
    }
}
