package TypecheckerTest;

import Parser.*;
import Parser.Expressions.*;
import Parser.Literals.BooleanLiteral;
import Parser.Literals.IdentifierLiteral;
import Parser.Literals.IntegerLiteral;
import Parser.Literals.StringLiteral;
import Tokenizer.*;
import Typechecker.Types.*;
import Typechecker.*;
import Parser.Declarations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.*;

public class TypecheckerTest {

    public static Type typeof(final TypeEnvironment env, final Exp e)
            throws IllTypedException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        //using reflection to access private constructor, dont use this outside of testing
        final Constructor<Typechecker> constructor = Typechecker.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (constructor.newInstance().typeof(env, e));
    }

    public static void assertTypechecksExp(TypeEnvironment env,
                                           final Type expected, final String received) {

        //in case of no bound vars, needs an empty map rather than a null
        if (env == null) {
            env = Typechecker.createEmptyTypeEnvironment();
        }
        try {
            final Parser parser = new Parser(new Tokenizer(received).tokenize());
            final Exp parsedExp = (parser.parseExp(0)).getResult();
            Assertions.assertEquals(expected, typeof(env, parsedExp));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void checkTypeOfStringLiteral() {
        assertTypechecksExp( null, new StringType(), "\"hello world\"");
    }
    @Test
    public void checkTypeOfIntegerLiteral() {
        assertTypechecksExp(null, new IntType(), "1");
    }
    @Test
    public void checkTypeOfBooleanLiteral() {
        assertTypechecksExp( null, new BoolType(), "true");
    }
    @Test
    public void checkTypeOfIdentifierLiteralInt() {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new IntType());
        assertTypechecksExp(newEnv, new IntType(), "foo");
    }
    @Test
    public void checkTypeOfIdentifierLiteralBool() {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new BoolType());
        assertTypechecksExp(newEnv, new BoolType(), "foo");
    }
    @Test
    public void checkTypeOfIdentifierLiteralString() {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new StringType());
        assertTypechecksExp(newEnv, new StringType(), "foo");
    }
    @Test
    public void checkThrowsExceptionVarNotInScope() {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        Assertions.assertThrows(IllTypedException.class,
                () -> typeof(env, new IdentifierLiteral("foo")));
    }
    @Test
    public void checkTypeOfNullLiteral() {
        assertTypechecksExp(null, new NullType(), "null");
    }
    @Test
    public void checkTypeOfBinopPlus() {
        assertTypechecksExp(null, new IntType(), "1 + 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopPlus() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("+",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopMinus() {
        assertTypechecksExp(null, new IntType(), "1 - 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopMinus() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("-",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopMult() {
        assertTypechecksExp(null, new IntType(), "1 * 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopMult() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("*",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopDiv() {
        assertTypechecksExp(null, new IntType(), "1 / 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopDiv() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("/",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopLessThan() {
        assertTypechecksExp(null, new BoolType(), "1 < 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopLessThan() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("<",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopGreaterThan() {
        assertTypechecksExp(null, new BoolType(), "1 > 2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopGreaterThan() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp(">",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopReferenceEqualsIntegers() {
        assertTypechecksExp(null, new BoolType(), "1 == 2");
    }
    @Test
    public void checkTypeOfBinopReferenceEqualsBooleans() {
        assertTypechecksExp(null, new BoolType(), "true == true");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopReferenceEquals() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("==",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfBinopNotEqualsIntegers() {
        assertTypechecksExp(null, new BoolType(), "1 != 2");
    }
    @Test
    public void checkTypeOfBinopNotEqualsBools() {
        assertTypechecksExp(null, new BoolType(), "true != true");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchBinopNotEquals() {
        final BinaryOperatorExp BOPExp = new BinaryOperatorExp("!=",
                new IntegerLiteral(1),
                new BooleanLiteral(false));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, BOPExp));
    }
    @Test
    public void checkTypeOfPreIncrExp() {
        assertTypechecksExp(null, new IntType(), "++2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPreIncrExpBool() {
        final PreIncrDecrExp PreIncrExp = new PreIncrDecrExp(
                new BooleanLiteral(true),
                "++"
        );
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, PreIncrExp));
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPreIncrExpStr() {
        final PreIncrDecrExp PreIncrExp = new PreIncrDecrExp(
                new StringLiteral("foo"),
                "++"
        );
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, PreIncrExp));
    }
    @Test
    public void checkTypeOfPreDecrExp() {
        assertTypechecksExp(null, new IntType(), "--2");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPreDecrExpBool() {
        final PreIncrDecrExp PreDecrExp = new PreIncrDecrExp(
                new BooleanLiteral(true),
                "--"
        );
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, PreDecrExp));
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPreDecrExpStr() {
        final PreIncrDecrExp PreDecrExp = new PreIncrDecrExp(
                new StringLiteral("foo"),
                "--"
        );
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, PreDecrExp));
    }
    @Test
    public void checkTypeOfPostIncrExp() {
        assertTypechecksExp(null, new IntType(), "2++");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPostIncrExpBool() {
        final PostIncrDecrExp PostIncrExp = new PostIncrDecrExp(
                new BooleanLiteral(true),
                "++"
        );
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, PostIncrExp));
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPostIncrExpStr() {
        final PostIncrDecrExp PostIncrExp = new PostIncrDecrExp(
                new StringLiteral("foo"),
                "++"
        );
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, PostIncrExp));
    }
    @Test
    public void checkTypeOfPostDecrExp() {
        assertTypechecksExp(null, new IntType(), "2--");
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPostDecrExpBool() {
        final PostIncrDecrExp PostDecrExp = new PostIncrDecrExp(
                new BooleanLiteral(true),
                "--"
        );
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, PostDecrExp));
    }
    @Test
    public void checkThrowsExceptionTypeMismatchPostDecrExpStr() {
        final PostIncrDecrExp PostDecrExp = new PostIncrDecrExp(
                new StringLiteral("foo"),
                "--"
        );
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, PostDecrExp));
    }
    @Test
    public void checkTypeOfAssignmentExpIdentifierInteger() {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new IntType());

        assertTypechecksExp(newEnv, new IntType(), "foo = 2");
    }
    @Test
    public void checkTypeOfAssignmentExpIdentifierBoolean() {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new BoolType());

        assertTypechecksExp(newEnv, new BoolType(), "foo = true");
    }
    @Test
    public void checkTypeOfAssignmentExpIdentifierString() {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new StringType());

        assertTypechecksExp(newEnv, new StringType(), "foo = \"testString\"");
    }
}
