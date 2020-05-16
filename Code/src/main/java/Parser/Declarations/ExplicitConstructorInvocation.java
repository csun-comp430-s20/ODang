package Parser.Declarations;

import Parser.Expressions.Exp;

public class ExplicitConstructorInvocation implements Decl {
    public final Exp argList;
    
    public ExplicitConstructorInvocation(final Exp argList) {
        this.argList = argList;
    }

    public boolean equals(Object other) {
        if (other instanceof ExplicitConstructorInvocation) {
            ExplicitConstructorInvocation otherECI = (ExplicitConstructorInvocation) other;
            return argList.equals(otherECI.argList);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + argList);
    }
}
