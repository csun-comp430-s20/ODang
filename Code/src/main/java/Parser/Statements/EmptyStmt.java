package Parser.Statements;

public class EmptyStmt implements Stmt {

    public boolean equals(Object other) {
        return (other instanceof EmptyStmt);
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName());
    }
}
