package Parser.Declarations;
import Parser.Statements.*;

public class MethodOverloadDecl implements Decl {
    public final Decl left;
    public final Stmt right;

    public MethodOverloadDecl(final Decl left, final Stmt right) {
        this.left = left;
        this.right = right;
    }

    public boolean equals(final Object other) {
        if(other instanceof MethodOverloadDecl){
            final MethodOverloadDecl otherMethodOverload = (MethodOverloadDecl) other;
            return this.left.equals(otherMethodOverload.left) &&
                    this.right.equals(otherMethodOverload.right);
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + left + " " + right);
    }
}
