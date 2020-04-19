package TypecheckerTest;

import Parser.*;
import Parser.Expressions.*;
import Tokenizer.*;
import Typechecker.Types.*;
import Typechecker.*;
import Parser.Declarations.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;

public class TypecheckerTest {

    public static void assertTypechecksExp(final Type expected, final String received) {

        try {
            final Parser parser = new Parser(new Tokenizer(received).tokenize());
            final Exp parsedExp = (parser.parseExp(0)).getResult();

            Assertions.assertEquals(expected, Typechecker.typeof(null, parsedExp));
        }
        catch (Exception e) {
            System.out.println(e.getClass() + ": " + e.getMessage());
        }
    }

    @Test
    public void checkTypechecksStringLiteral() {
        assertTypechecksExp(new StringType(), "\"hello world\"");
    }
    @Test
    public void checkTypechecksIntegerLiteral() {
        assertTypechecksExp(new IntType(), "1");
    }
    @Test
    public void checkTypechecksBooleanLiteral() {
        assertTypechecksExp(new BoolType(), "true");
    }
    @Test
    public void checkTypechecksIdentifierLiteral() {
        assertTypechecksExp(new IdentifierType(), "foo");
    }
    @Test
    public void checkTypechecksNullLiteral() {
        assertTypechecksExp(new NullType(), "null");
    }
}
