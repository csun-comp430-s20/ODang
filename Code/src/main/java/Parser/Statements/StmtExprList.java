package Parser.Statements;

import java.util.ArrayList;
import java.util.List;

public class StmtExprList implements Stmt{
    public final List<Stmt> list;

    public StmtExprList(final Stmt... expStmtList) {
        this.list = new ArrayList<Stmt>();

        if (expStmtList != null) {
            for (Stmt s : expStmtList) {
                this.list.add(s);
            }
        }
    }
    public boolean equals(final Object other) {
        if (other instanceof StmtExprList) {
            final StmtExprList otherList = (StmtExprList) other;
            return list.equals(otherList.list);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + list);
    }



}
