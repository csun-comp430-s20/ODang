package Parser.Statements;

import Parser.Expressions.*;

public class IfElseStmt implements Stmt {
    public final Exp guard;
    public final Stmt trueBranch;
    public final Stmt falseBranch;

    public IfElseStmt(final Exp guard, final Stmt trueBranch, final Stmt falseBranch) {
        this.guard = guard;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    public boolean equals(Object other) {
        if (other instanceof IfElseStmt) {
            IfElseStmt otherIfElse = (IfElseStmt) other;
            return (guard.equals(otherIfElse.guard) &&
                    trueBranch.equals(otherIfElse.trueBranch) &&
                    falseBranch.equals(otherIfElse.falseBranch));
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() +
                " guard: " + guard + " trueBranch: " + trueBranch + " falseBranch: " + falseBranch);
    }
}
