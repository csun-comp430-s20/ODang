package Parser.Declarations;

public class ConstructorDecl implements Decl {
    public final Decl constructorDeclarator;
    public final Decl constructorBody;

    public ConstructorDecl(final Decl constructorDeclarator, final Decl constructorBody) {
        this.constructorDeclarator = constructorDeclarator;
        this.constructorBody = constructorBody;
    }

    public boolean equals(Object other) {
        if (other instanceof ConstructorDecl) {
            ConstructorDecl otherCD = (ConstructorDecl) other;
            return constructorDeclarator.equals(otherCD.constructorDeclarator) &&
                    constructorBody.equals(otherCD.constructorBody);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + constructorDeclarator +
                " " + constructorBody + ")");
    }
}
