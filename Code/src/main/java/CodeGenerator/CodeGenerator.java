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


    public static void main(String[] args) {

    }
}
