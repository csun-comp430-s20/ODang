package Parser.Declarations;

import java.util.List;
import java.util.ArrayList;
public class VarDeclaratorList implements Decl {
    public final List<Decl> varDeclList;

    public VarDeclaratorList(final Decl... varDecls) {
        this.varDeclList = new ArrayList<>();

        if (varDecls != null) {
            for (Decl d : varDecls) {
                this.varDeclList.add(d);
            }
        }
    }

    public boolean equals(Object other) {
        if (other instanceof VarDeclaratorList) {
            VarDeclaratorList otherList = (VarDeclaratorList) other;
            return varDeclList.equals(otherList.varDeclList);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + varDeclList + "");
    }
}
