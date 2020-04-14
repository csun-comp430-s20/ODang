package Typechecker;

import Parser.Expressions.Exp;
import Parser.Declarations.Decl;
import Parser.Parser;
import Parser.Statements.Stmt;
import Parser.Literals.*;
import Tokenizer.Tokenizer;
import Typechecker.Types.BoolType;
import Typechecker.Types.IntType;
import Typechecker.Types.Type;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
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
    public static void main(String[] args) {

        try {
            File file = new File("testProgram.odang");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String tokenizerInput = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                tokenizerInput += line;
            }
            br.close();
            final Tokenizer tokenizer = new Tokenizer(tokenizerInput);
            final Parser parser = new Parser(tokenizer.tokenize());
            final List<Decl> parsed = parser.parseProgram();


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
