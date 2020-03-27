package Parser.Statements;

import Parser.Expressions.Exp;

public class PrintlnStmt implements Stmt {
    public final Exp exp;

    public PrintlnStmt(final Exp exp) {
        this.exp = exp;
    }

    public boolean equals(Object other) {
        if (other instanceof PrintlnStmt) {
            PrintlnStmt otherPrint = (PrintlnStmt) other;
            return exp.equals(otherPrint.exp);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + "(" + exp + ")");
    }
}
