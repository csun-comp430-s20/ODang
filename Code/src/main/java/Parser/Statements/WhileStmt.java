package Parser.Statements;

import Parser.Expressions.Exp;

public class WhileStmt implements Stmt {
    public final Exp guard;
    public final Stmt body;

    public WhileStmt(final Exp guard, final Stmt body) {
        this.guard = guard;
        this.body = body;
    }

    public boolean equals(Object other) {
        if (other instanceof WhileStmt) {
            WhileStmt otherWhile = (WhileStmt) other;
            return guard.equals(otherWhile.guard)
                    && body.equals(otherWhile.body);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + "( guard: " + guard +
                " body: " + body + ")");
    }
}
