package Parser.Statements;

import java.util.ArrayList;
import java.util.List;

public class Block implements Stmt {
    public final List<Stmt> blockStmts;

    public Block(final List<Stmt> blockStmts) {
        this.blockStmts = new ArrayList<>();
        if (!(blockStmts.isEmpty())) {
            for (final Stmt stmt : blockStmts)
                this.blockStmts.add(stmt);
        }
    }

    public boolean equals(final Object other) {
        if (other instanceof Block) {
            final Block otherBlock = (Block)other;
            return blockStmts.equals(otherBlock.blockStmts);
        }
        else return false;
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + ": " + blockStmts);
    }
}
