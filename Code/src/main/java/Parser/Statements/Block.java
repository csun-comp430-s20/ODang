package Parser.Statements;

public class Block implements Stmt {
    public final Stmt blockStmts;

    public Block(final Stmt blockStmts) {
        this.blockStmts = blockStmts;
    }

    public boolean equals(Object other) {
        if (other instanceof Block) {
            Block otherBlock = (Block) other;
            return this.blockStmts.equals(otherBlock.blockStmts);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + blockStmts + ")");
    }
}
