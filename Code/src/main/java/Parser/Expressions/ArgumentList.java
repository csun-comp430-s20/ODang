package Parser.Expressions;

import java.util.List;
import java.util.ArrayList;

public class ArgumentList implements Exp {
    public final List<Exp> expList;

    public ArgumentList(final Exp... expList) {
        this.expList = new ArrayList<Exp>();

        if (expList != null) {
            for (Exp e : expList) {
                this.expList.add(e);
            }
        }
    }
    public boolean equals(final Object other) {
        if (other instanceof ArgumentList) {
            final ArgumentList otherList = (ArgumentList) other;
            return expList.equals(otherList.expList);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + "< " + expList +  " >");
    }
}
