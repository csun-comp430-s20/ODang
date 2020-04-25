package Parser.Expressions;

import Parser.Types.*;

public class CastExp implements Exp {
    public final ParserType parserType;
    public final Exp exp;

    public CastExp(final ParserType parserType, final Exp exp) {
        this.parserType = parserType;
        this.exp = exp;
    }

    public boolean equals(final Object other) {
        if (other instanceof CastExp) {
            final CastExp otherExp = (CastExp) other;

            return parserType.equals(otherExp.parserType) && exp.equals(otherExp.exp);
        }
        else return false;
    }
    public String toString() {
        return String.format(this.getClass().getSimpleName() + " (" + parserType + ") " + exp);
    }
}
