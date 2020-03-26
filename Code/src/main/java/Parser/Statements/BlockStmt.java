package Parser.Statements;

public class BlockStmt implements Stmt {
    public final Stmt left;
    public final Stmt right;
    public BlockStmt(final Stmt left, final Stmt right) {
        this.left = left;
        this.right = right;
    }
    public boolean equals(Object other) {
        if (other instanceof BlockStmt) {
            BlockStmt otherBlockStmt = (BlockStmt) other;
            return left.equals(otherBlockStmt.left) &&
                    right.equals(otherBlockStmt.right);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + " [" + left + " " + right +"]");
    }
}
