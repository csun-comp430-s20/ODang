package Parser.Statements;

import Parser.Expressions.ArgumentList;
import Parser.Expressions.Exp;

import java.util.ArrayList;
import java.util.List;

public class ExpStmtList implements Stmt{
    public final List<Stmt> list;

    public ExpStmtList(final Stmt... expStmtList) {
        this.list = new ArrayList<Stmt>();

        if (expStmtList != null) {
            for (Stmt s : expStmtList) {
                this.list.add(s);
            }
        }
    }
    public boolean equals(final Object other) {
        if (other instanceof ExpStmtList) {
            final ExpStmtList otherList = (ExpStmtList) other;
            return list.equals(otherList.list);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + list);
    }



}
