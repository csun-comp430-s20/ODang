package Typechecker.Types;

import java.util.List;

public class Constructor {

    public final String className;
    public final List<FormalParameter> formalParameters;

    public Constructor(final String className, final List<FormalParameter> formalParameters) {
        this.className = className;
        this.formalParameters = formalParameters;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Constructor) {
            final Constructor asConstructor = (Constructor) other;
            return className.equals(asConstructor.className) &&
                    formalParameters.equals(asConstructor.formalParameters);
        }

        else return false;
    }

    @Override
    public String toString() {
        return String.format(className + formalParameters);
    }
}
