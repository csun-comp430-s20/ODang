package Typechecker;

import Parser.Expressions.Exp;
import Parser.Declarations.Decl;
import Parser.Statements.Stmt;
import Parser.Literals.*;
import Typechecker.Types.BoolType;
import Typechecker.Types.IntType;
import Typechecker.Types.Type;

import java.util.Map;
import java.util.HashMap;
import com.google.common.collect.ImmutableMap;

public class Typechecker {

    public static Type typeof(final ImmutableMap<String, Type> gamma, final Exp e) throws IllTypedException{
        if (e instanceof IntegerLiteral) {
            return new IntType();
        }
        else if (e instanceof BooleanLiteral) {
            return new BoolType();
        }
        else {
            assert(false);
            throw new IllTypedException("unrecognized expression");
        }
    }
}
