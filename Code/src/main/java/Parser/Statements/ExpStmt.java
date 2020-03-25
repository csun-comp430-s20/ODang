package Parser.Statements;

import Parser.Expressions.Exp;

import java.util.Objects;

public class ExpStmt implements Stmt {
    public final Exp exp;

    public ExpStmt(final Exp exp) {
        this.exp = exp;
    }

    public boolean equals(Object other) {
        if (other instanceof ExpStmt) {
            ExpStmt otherExpStmt = (ExpStmt) other;
            return exp.equals(otherExpStmt.exp);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + " exp: " + exp);
    }
}
