package Parser.Declarations;

import Parser.Types.Type;

public class FieldDecl implements Decl {
    public final Type type;
    public final Decl varDeclarators;

    public FieldDecl(final Type type, final Decl varDeclarators) {
        this.type = type;
        this.varDeclarators = varDeclarators;
    }
    public boolean equals(Object other) {
        if (other instanceof FieldDecl) {
            FieldDecl otherFD = (FieldDecl) other;
            return type.equals(otherFD.type) && varDeclarators.equals(otherFD.varDeclarators);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + type + " " + varDeclarators + ")");
    }
}
