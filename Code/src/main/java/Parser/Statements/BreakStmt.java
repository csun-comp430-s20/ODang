package Parser.Statements;
import Parser.Expressions.*;

public class BreakStmt implements Stmt{
    public final String breakString;
    public final Exp identifier;
    public final String semicolon;

    public BreakStmt(final String breakString, final Exp identifier, final String semicolon){
        this.breakString = breakString;
        this.identifier = identifier;
        this.semicolon = semicolon;
    }

    public boolean equals(final Object other){
        if (other instanceof  BreakStmt){
            final BreakStmt otherBrkStmt = (BreakStmt)other;
            return (breakString.equals(otherBrkStmt.breakString) &&
                    identifier.equals(otherBrkStmt.identifier) &&
                    semicolon.equals(otherBrkStmt.semicolon));
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (" + breakString + " " + identifier + " " + semicolon + ")");
    }
}
