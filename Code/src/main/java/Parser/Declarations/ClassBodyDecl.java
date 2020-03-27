package Parser.Declarations;

public class ClassBodyDecl implements Decl {
    
    public final Decl left;
    public final Decl right;

    public ClassBodyDecl(final Decl left, final Decl right) {
        this.left = left;
        this.right = right;
    }
    public boolean equals(Object other) {
        if (other instanceof ClassBodyDecl) {
            ClassBodyDecl otherClassBodyDecl = (ClassBodyDecl) other;
            return left.equals(otherClassBodyDecl.left) &&
                    right.equals(otherClassBodyDecl.right);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + " [" + left + " " + right +"]");
    }
}
