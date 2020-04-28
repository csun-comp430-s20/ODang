package Parser.Statements;

import java.util.ArrayList;
import java.util.List;

public class BlockStmt implements Stmt {

    public final List<Stmt> block;

    public BlockStmt(final Stmt... statements) {
        block = new ArrayList<>();

        if(statements != null) {
            for (final Stmt stmt : statements) {
                block.add(stmt);
            }
        }
    }
    public boolean equals(Object other) {
        if (other instanceof BlockStmt) {
            BlockStmt otherBlockStmt = (BlockStmt) other;
            return block.equals(otherBlockStmt.block);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + " " + block);
    }
}
