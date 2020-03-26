package Parser.Declarations;

import java.util.List;
import java.util.ArrayList;

public class FormalParamList implements Decl {
    public final List<Decl> declList;

    public FormalParamList(final Decl ... declList) {
        this.declList = new ArrayList<Decl>();

        if (declList != null) {
            for (Decl d : declList) {
                this.declList.add(d);
            }
        }
    }

    public boolean equals(final Object other) {
        if (other instanceof FormalParamList) {
            final FormalParamList otherList = (FormalParamList) other;
            return declList.equals(otherList.declList);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + "( " + declList +  " )");
    }
}
