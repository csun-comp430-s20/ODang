package Typechecker.Types;
import java.util.ArrayList;
import java.util.List;
import Parser.Expressions.*;
import Parser.Statements.*;

public class FunctionDefinition {
    public final Type returnType;
    public final String name;
    public final List<FormalParameter> formalParams;
    public final Stmt body;
    // simplification: return can only be at the end of the function,
    // and we must always return a value.  There is no void.
    public final ReturnStmt returnStmt;

    public FunctionDefinition(final Type returnType,
                                        final String name,
                                        final List<FormalParameter> formalParams,
                                        final Stmt body,
                                        final ReturnStmt returnStmt) {
        this.returnType = returnType;
        this.name = name;
        this.formalParams = (formalParams == null) ?
                new ArrayList<>() : formalParams;
        this.body = body;
        this.returnStmt = returnStmt;
    }

    public boolean equals(final Object other) {
        if (other instanceof FunctionDefinition) {
            final FunctionDefinition asFD = (FunctionDefinition) other;

            return returnType.equals(asFD.returnType) &&
                    name.equals(asFD.name) &&
                    formalParams.equals(asFD.formalParams) &&
                    body.equals(asFD.body) &&
                    returnStmt.equals(asFD.returnStmt);
        }
        else return false;
    }

    public int hashCode() {
        return returnType.hashCode() +
                name.hashCode() +
                formalParams.hashCode();
    }

    public String toString() {
        return String.format(getClass().getSimpleName() + ": " +returnType + " " + name + formalParams);
    }
}
