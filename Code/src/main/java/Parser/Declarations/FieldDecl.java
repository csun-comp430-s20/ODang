package Parser.Declarations;

import Parser.Types.ParserType;

public class FieldDecl implements Decl {
    public final ParserType parserType;
    public final Decl varDeclarators;

    public FieldDecl(final ParserType parserType, final Decl varDeclarators) {
        this.parserType = parserType;
        this.varDeclarators = varDeclarators;
    }
    public boolean equals(Object other) {
        if (other instanceof FieldDecl) {
            FieldDecl otherFD = (FieldDecl) other;
            return parserType.equals(otherFD.parserType) && varDeclarators.equals(otherFD.varDeclarators);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + parserType + " " + varDeclarators + ")");
    }
}
