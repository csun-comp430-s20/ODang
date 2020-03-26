package Parser.Declarations;
import Parser.Statements.BreakStmt;
import Parser.Types.*;
import Parser.Expressions.*;

public class FormalParam implements Decl {
    public final Type paramType;
    public final Exp paramIdentifier;

    public FormalParam(final Type paramType, final Exp paramIdentifier){
        this.paramType = paramType;
        this.paramIdentifier = paramIdentifier;
    }

    public boolean equals(final Object other){
        if (other instanceof FormalParam){
            final FormalParam otherFormalParam = (FormalParam) other;
            return this.paramType.equals(otherFormalParam.paramType) &&
                    this.paramIdentifier.equals(otherFormalParam.paramIdentifier);
        } else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() +
                " " + paramType + " " + paramIdentifier);
    }
}
