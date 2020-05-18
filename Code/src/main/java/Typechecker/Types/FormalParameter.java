package Typechecker.Types;

public class FormalParameter {
    public final Type theType;
    public final String theVariable;

    public FormalParameter(final Type theType,
                           final String theVariable) {
        this.theType = theType;
        this.theVariable = theVariable;
    }

    public boolean equals(final Object other) {
        if (other instanceof FormalParameter) {
            final FormalParameter asFormalParam = (FormalParameter)other;
            return theType.equals(asFormalParam.theType) && theVariable.equals(asFormalParam.theVariable);
        }
        else return false;
    }

    public int hashCode() {
        return theType.hashCode() + theVariable.hashCode();
    }

    public String toString() {
        return String.format(theType + " " + theVariable);
    }
}
