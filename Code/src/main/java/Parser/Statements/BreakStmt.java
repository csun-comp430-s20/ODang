package Parser.Statements;
import Parser.Expressions.*;

public class BreakStmt implements Stmt{
    public final Exp identifier;

    public BreakStmt(final Exp identifier){
        this.identifier = identifier;
    }

    public boolean equals(final Object other){
        if (other instanceof  BreakStmt){
            final BreakStmt otherBrkStmt = (BreakStmt)other;
            return this.identifier.equals(otherBrkStmt.identifier);
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (break " + " " + identifier + ")");
    }
}
