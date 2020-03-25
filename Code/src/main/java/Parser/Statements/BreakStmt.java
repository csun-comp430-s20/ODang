package Parser.Statements;
import Parser.Expressions.*;

public class BreakStmt implements Stmt{
    public final String breakString;
    public final Exp identifier;

    public BreakStmt(final String breakString, final Exp identifier){
        this.breakString = breakString;
        this.identifier = identifier;
    }

    public boolean equals(final Object other){
        if (other instanceof  BreakStmt){
            final BreakStmt otherBrkStmt = (BreakStmt)other;
            return (breakString.equals(otherBrkStmt.breakString) &&
                    identifier.equals(otherBrkStmt.identifier));
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (" + breakString + " " + identifier + ")");
    }
}
