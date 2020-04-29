package Typechecker.Types;
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
        this.formalParams = formalParams;
        this.body = body;
        this.returnStmt = returnStmt;
    }
}
