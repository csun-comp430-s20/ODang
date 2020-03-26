package Parser.Statements;

import Parser.Declarations.Decl;
import Parser.Types.Type;

public class LocalVardecStmt implements Stmt {
    public final Type type;
    public final Decl varDeclarators;

    public LocalVardecStmt(final Type type, final Decl varDeclarators) {
        this.type = type;
        this.varDeclarators = varDeclarators;
    }

    public boolean equals(Object other) {
        if (other instanceof LocalVardecStmt) {
            LocalVardecStmt otherLocVarStmt = (LocalVardecStmt) other;
            return type.equals(otherLocVarStmt.type) &&
                    varDeclarators.equals(otherLocVarStmt.varDeclarators);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (" + type + " " + varDeclarators + ")");
    }
}
