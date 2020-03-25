package Parser.Statements;
import Parser.Expressions.*;

public class ReturnStmt implements Stmt {
    public final String returnString;
    public final Exp right;
    public final String semicolon;

    public ReturnStmt(final String returnString, final Exp right, final String semicolon){
        this.returnString = returnString;
        this.right = right;
        this.semicolon = semicolon;
    }

    public boolean equals(final Object other){
        if (other instanceof  ReturnStmt){
            final ReturnStmt otherRtnStmt = (ReturnStmt)other;
            return (returnString.equals(otherRtnStmt.returnString) &&
                    right.equals(otherRtnStmt.right) &&
                    semicolon.equals(otherRtnStmt.semicolon));
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (" + returnString + " " + right + " " + semicolon + ")");
    }
}


