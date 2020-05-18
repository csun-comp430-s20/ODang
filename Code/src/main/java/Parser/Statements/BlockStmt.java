package Parser.Statements;

import java.util.ArrayList;
import java.util.List;

public class BlockStmt implements Stmt {

    public final Stmt stmt;

    public BlockStmt(final Stmt stmt) {
        this.stmt = stmt;

    }
    public boolean equals(Object other) {
        if (other instanceof BlockStmt) {
            BlockStmt otherBlockStmt = (BlockStmt) other;
            return stmt.equals(otherBlockStmt.stmt);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + " " + stmt);
    }
}
