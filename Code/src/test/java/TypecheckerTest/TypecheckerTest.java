package TypecheckerTest;

import Parser.*;
import Parser.Expressions.*;
import Parser.Literals.BooleanLiteral;
import Parser.Literals.IntegerLiteral;
import Parser.Literals.StringLiteral;
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
    public void checkTypeOfStringLiteral() {
        assertTypechecksExp(new StringType(), "\"hello world\"");
    }
    @Test
    public void checkTypeOfIntegerLiteral() {
        assertTypechecksExp(new IntType(), "1");
    }
    @Test
    public void checkTypeOfBooleanLiteral() {
        assertTypechecksExp(new BoolType(), "true");
    }
    @Test
    public void checkTypeOfIdentifierLiteral() {
        assertTypechecksExp(new IdentifierType(), "foo");
    }
    @Test
    public void checkTypeOfNullLiteral() {
        assertTypechecksExp(new NullType(), "null");
    }
    @Test
    public void checkTypeOfBinopPlus() {
        assertTypechecksExp(new IntType(), "1 + 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopPlus() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("+",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopMinus() {
        assertTypechecksExp(new IntType(), "1 - 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopMinus() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("-",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopMult() {
        assertTypechecksExp(new IntType(), "1 * 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopMult() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("*",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopDiv() {
        assertTypechecksExp(new IntType(), "1 / 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopDiv() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("/",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopLessThan() {
        assertTypechecksExp(new BoolType(), "1 < 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopLessThan() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("<",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopGreaterThan() {
        assertTypechecksExp(new BoolType(), "1 > 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopGreaterThan() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp(">",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopReferenceEqualsIntegers() {
        assertTypechecksExp(new BoolType(), "1 == 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopReferenceEquals() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("==",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopReferenceEqualsBooleans() {
        assertTypechecksExp(new BoolType(), "true == true");
    }
    @Test
    public void checkTypeOfPreIncrExp() {
        assertTypechecksExp(new IntType(), "++2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPreIncrExpBool() {
        final PreIncrDecrExp PreIncrExp = new PreIncrDecrExp(
                new BooleanLiteral(true),
                "++"
        );
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, PreIncrExp));
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPreIncrExpStr() {
        final PreIncrDecrExp PreIncrExp = new PreIncrDecrExp(
                new StringLiteral("foo"),
                "++"
        );
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, PreIncrExp));
    }
    @Test
    public void checkTypeOfPreDecrExp() {
        assertTypechecksExp(new IntType(), "--2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPreDecrExpBool() {
        final PreIncrDecrExp PreDecrExp = new PreIncrDecrExp(
                new BooleanLiteral(true),
                "--"
        );
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, PreDecrExp));
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPreDecrExpStr() {
        final PreIncrDecrExp PreDecrExp = new PreIncrDecrExp(
                new StringLiteral("foo"),
                "--"
        );
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, PreDecrExp));
    }
    @Test
    public void checkTypeOfPostIncrExp() {
        assertTypechecksExp(new IntType(), "2++");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPostIncrExpBool() {
        final PostIncrDecrExp PostIncrExp = new PostIncrDecrExp(
                new BooleanLiteral(true),
                "++"
        );
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, PostIncrExp));
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPostIncrExpStr() {
        final PostIncrDecrExp PostIncrExp = new PostIncrDecrExp(
                new StringLiteral("foo"),
                "++"
        );
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, PostIncrExp));
    }
    @Test
    public void checkTypeOfPostDecrExp() {
        assertTypechecksExp(new IntType(), "2--");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPostDecrExpBool() {
        final PostIncrDecrExp PostDecrExp = new PostIncrDecrExp(
                new BooleanLiteral(true),
                "--"
        );
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, PostDecrExp));
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPostDecrExpStr() {
        final PostIncrDecrExp PostDecrExp = new PostIncrDecrExp(
                new StringLiteral("foo"),
                "--"
        );
        Assertions.assertThrows(IllTypedException.class, () -> Typechecker.typeof(null, PostDecrExp));
    }
}
