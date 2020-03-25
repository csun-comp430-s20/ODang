package Parser.Statements;
import Parser.Expressions.*;

public class ReturnStmt implements Stmt {
    public final Exp exp;

    public ReturnStmt(final Exp exp){
        this.exp = exp;
    }

    public boolean equals(final Object other){
        if (other instanceof  ReturnStmt){
            final ReturnStmt otherRtnStmt = (ReturnStmt)other;
            return this.exp.equals(otherRtnStmt.exp);
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (return " + exp + ")");
    }
}


