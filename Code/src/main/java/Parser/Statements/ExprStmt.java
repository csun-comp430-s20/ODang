package Parser.Statements;

import Parser.Expressions.Exp;

public class ExprStmt implements Stmt {
    public final Stmt stmt;

    public ExprStmt(final Stmt stmt) {
        this.stmt = stmt;
    }

    public boolean equals(Object other) {
        if (other instanceof ExprStmt) {
            ExprStmt otherExprStmt = (ExprStmt) other;
            return stmt.equals(otherExprStmt.stmt);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + " (" + stmt + ");");
    }
}
