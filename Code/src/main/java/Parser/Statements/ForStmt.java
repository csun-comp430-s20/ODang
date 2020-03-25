package Parser.Statements;

import Parser.Expressions.*;

public class ForStmt implements Stmt {
    public final Stmt forInit;
    public final Exp conditional;
    public final Stmt forUpdate;
    public final Stmt body;

    public ForStmt(final Stmt forInit, final Exp conditional, final Stmt forUpdate, final Stmt body) {
        this.forInit = forInit;
        this.conditional = conditional;
        this.forUpdate = forUpdate;
        this.body = body;
    }

    public boolean equals(Object other) {
        if (other instanceof ForStmt) {
            ForStmt otherFor = (ForStmt) other;

            return forInit.equals(otherFor.forInit) &&
                    conditional.equals(otherFor.conditional) &&
                    forUpdate.equals(otherFor.forUpdate) &&
                    body.equals(otherFor.body);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() +
                " forInit: " + forInit + " conditional: " + conditional +
                " forUpdate: " + forUpdate + " body: " + body);
    }
}
