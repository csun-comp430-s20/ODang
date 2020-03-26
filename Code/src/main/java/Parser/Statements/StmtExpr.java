package Parser.Statements;

import Parser.Expressions.Exp;

public class StmtExpr implements Stmt {
    public final Exp exp;

    public StmtExpr(final Exp exp) {
        this.exp = exp;
    }

    public boolean equals(Object other) {
        if (other instanceof StmtExpr) {
            StmtExpr otherStmtExpr = (StmtExpr) other;
            return exp.equals(otherStmtExpr.exp);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + " (" + exp + ");");
    }
}
