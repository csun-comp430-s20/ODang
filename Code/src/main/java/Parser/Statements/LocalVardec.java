package Parser.Statements;

import Parser.Declarations.Decl;
import Parser.Types.ParserType;

public class LocalVardec implements Stmt {
    public final ParserType parserType;
    public final Decl varDeclarators;

    public LocalVardec(final ParserType parserType, final Decl varDeclarators) {
        this.parserType = parserType;
        this.varDeclarators = varDeclarators;
    }

    public boolean equals(Object other) {
        if (other instanceof LocalVardec) {
            LocalVardec otherLocVarStmt = (LocalVardec) other;
            return parserType.equals(otherLocVarStmt.parserType) &&
                    varDeclarators.equals(otherLocVarStmt.varDeclarators);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (" + parserType + " " + varDeclarators + ")");
    }
}
