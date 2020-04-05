package Parser.Declarations;

public class ClassBodyDecl implements Decl {
    
    public final Decl decl;

    public ClassBodyDecl(final Decl decl) {
        this.decl = decl;
    }
    public boolean equals(Object other) {
        if (other instanceof ClassBodyDecl) {
            ClassBodyDecl otherClassBodyDecl = (ClassBodyDecl) other;
            return decl.equals(otherClassBodyDecl.decl);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + decl);
    }
}
