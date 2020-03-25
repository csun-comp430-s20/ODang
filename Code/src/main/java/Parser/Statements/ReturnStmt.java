package Parser.Statements;
import Parser.Expressions.*;

public class ReturnStmt implements Stmt {
    public final String returnString;
    public final Exp right;

    public ReturnStmt(final String returnString, final Exp right){
        this.returnString = returnString;
        this.right = right;
    }

    public boolean equals(final Object other){
        if (other instanceof  ReturnStmt){
            final ReturnStmt otherRtnStmt = (ReturnStmt)other;
            return (returnString.equals(otherRtnStmt.returnString) &&
                    right.equals(otherRtnStmt.right));
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " (" + returnString + " " + right + ")");
    }
}


