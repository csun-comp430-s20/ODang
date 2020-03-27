package Parser.Declarations;

import Parser.Expressions.Exp;

public class ExplicitConstructorInvocation implements Decl {
    public final String name;
    public final Exp argList;
    
    public ExplicitConstructorInvocation(final String name, final Exp argList) {
        this.name = name;
        this.argList = argList;
    }

    public boolean equals(Object other) {
        if (other instanceof ExplicitConstructorInvocation) {
            ExplicitConstructorInvocation otherECI = (ExplicitConstructorInvocation) other;
            return name.equals(otherECI.name) &&
                    argList.equals(otherECI.argList);
        }
        else return false;
    }

    public String toString() {
        return String.format(this.getClass().getSimpleName() + " " + name +
                " " + argList);
    }
}
