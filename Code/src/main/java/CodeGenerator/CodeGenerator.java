package CodeGenerator;

import Parser.Declarations.*;
import Parser.Statements.*;
import Parser.Expressions.*;
import Parser.Literals.*;
import Parser.Types.Void;
import Parser.Types.*;
import java.util.List;

public class CodeGenerator  {
    private final String codeOutput ="";
    private final List<Decl> AST;

    public CodeGenerator(final List<Decl> AST) {
        this.AST = AST;
    }

    public String generateExp(final String codeIn, final Exp e) throws CodeGeneratorException{
        if ( e instanceof IntegerLiteral){
            final IntegerLiteral asInt = (IntegerLiteral)e;
            return codeOutput + Integer.toString(asInt.value);
        }
        else {
            assert (false);
            throw new CodeGeneratorException("Unrecognizable expression.");
        }
    }

    public static void main(String[] args) {

    }
}
