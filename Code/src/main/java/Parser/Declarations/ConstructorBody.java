package Parser.Declarations;

import Parser.Statements.Stmt;

import java.util.List;

public class ConstructorBody implements Decl {
    public final Decl explConstrInvoc;
    public final List<Stmt> blockStmts;

    public ConstructorBody(final Decl explConstrInvoc, final List<Stmt> blockStmts) {
        this.explConstrInvoc = explConstrInvoc;
        this.blockStmts = blockStmts;
    }

    public boolean equals(Object other) {
        if (other instanceof ConstructorBody) {
            ConstructorBody otherCB = (ConstructorBody) other;
            return explConstrInvoc.equals(otherCB.explConstrInvoc) &&
                    blockStmts.equals(otherCB.blockStmts);
        }
        else return false;
    }

    public String toString() {
        if (explConstrInvoc == null)
            return String.format(this.getClass().getSimpleName() + " {" + blockStmts + "}");
        else
            return String.format(this.getClass().getSimpleName() + " {" + explConstrInvoc +
                " " + blockStmts + "}");
    }
}
