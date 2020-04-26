package Typechecker.Types;
import java.util.List;
import java.util.ArrayList;

public class FunctionType implements Type {
    public final List<Type> paramTypes;
    public final Type returnType;

    public FunctionType(final List<Type> paramTypes, final Type returnType) {
        this.paramTypes = new ArrayList<Type>();
        this.returnType = returnType;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof FunctionType) {
            final FunctionType asFunc = (FunctionType)other;
            return (paramTypes.equals(asFunc.paramTypes) &&
                    returnType.equals(asFunc.returnType));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return paramTypes.hashCode() + returnType.hashCode();
    }

    @Override
    public String toString(){
        return "(" + paramTypes.toString() + " => " + returnType.toString() + ")";
    }

}
