package TypecheckerTest;

import Parser.*;
import Parser.Expressions.*;
import Parser.Literals.*;
import Parser.Statements.*;
import Parser.Types.BooleanParserType;
import Parser.Types.ClassParserType;
import Parser.Types.IntParserType;
import Parser.Types.StringParserType;
import Tokenizer.*;
import Typechecker.Types.*;
import Typechecker.*;
import Parser.Declarations.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static TypeEnvironment typecheckClass(TypeEnvironment env, final ClassDecl decl) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, IllTypedException {

        if (env == null)
            env = Typechecker.createEmptyTypeEnvironment();

        final Constructor<Typechecker> constructor = Typechecker.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (constructor.newInstance().typecheckClass(env, decl));
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

    /*
    TypeEnvironment Tests
     */

    @Test
    public void checkTypeEnvironmentClassType() throws IllTypedException {
        final TypeEnvironment env = new TypeEnvironment(null, null, "TestClass");
        Assertions.assertEquals(env.thisType(), new ClassType("TestClass"));
    }
    @Test
    public void checkTypeEnvironmentThrowsExceptionLookupFunction() {
        final TypeEnvironment env = new TypeEnvironment(null, null, "Test");
        Assertions.assertThrows(IllTypedException.class, () -> env.lookupFunction("test2"));
    }
    @Test
    public void checkTypeEnvironmentThrowsExceptionLookupVariable() {
        final TypeEnvironment env = new TypeEnvironment(null, null, "Test");
        Assertions.assertThrows(IllTypedException.class, () -> env.lookupVariable("foo"));
    }
    @Test
    public void checkTypeEnvironmentThrowsExceptionThisType() {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        Assertions.assertThrows(IllTypedException.class, () -> env.thisType());
    }
    @Test
    public void checkTypeEnvironmentFunctionsIsEmptyTrue() {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        Assertions.assertEquals(true, env.functionsIsEmpty());
    }
    @Test
    public void checkTypeEnvironmentVariablesIsEmptyTrue() {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        Assertions.assertEquals(true, env.variablesIsEmpty());
    }
    @Test
    public void checkTypeEnvironmentFunctionsIsEmptyFalse() {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final FunctionDefinition func = new FunctionDefinition(new IntType(),
                "foo",
                null,
                null,
                new ReturnStmt(new IntegerLiteral(1)));
        final TypeEnvironment newEnv = env.addFunction("foo", func);
        Assertions.assertEquals(false, newEnv.functionsIsEmpty());
    }
    @Test
    public void checkTypeEnvironmentVariablesIsEmptyFalse() throws IllTypedException {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addVariable("foo", new IntType());
        Assertions.assertEquals(false, newEnv.variablesIsEmpty());
    }

    @Test
    public void checkTypeEnvironmentContainsFunctionTrue() {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final FunctionDefinition func = new FunctionDefinition(new IntType(),
                "foo",
                null,
                null,
                new ReturnStmt(new IntegerLiteral(1)));
        final TypeEnvironment newEnv = env.addFunction("foo", func);
        Assertions.assertEquals(true, newEnv.containsFunction("foo"));
    }

    @Test
    public void checkTypeEnvironmentContainsFunctionFalse() {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        Assertions.assertEquals(false, env.containsFunction("foo"));
    }

    @Test
    public void checkTypeEnvironmentGetClassName() throws IllTypedException {
        final TypeEnvironment env = new TypeEnvironment(null, null, "TestClass");
        Assertions.assertEquals("TestClass", env.getClassName());
    }
    @Test
    public void checkTypeEnvironmentThrowsExceptionGetClassName() {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        Assertions.assertThrows(IllTypedException.class, () -> env.getClassName());
    }

    @Test
    public void checkTypeEnvironmentThrowsExceptionAddingExistingVariable() throws IllTypedException {
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addVariable("foo", new IntType());
        Assertions.assertThrows(IllTypedException.class, () -> newEnv.addVariable("foo", new BoolType()));
    }

    /*
    Typechecker Tests
     */

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
    public void checkTypeOfIdentifierLiteralInt() throws IllTypedException {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new IntType());
        assertTypechecksExp(newEnv, new IntType(), "foo");
    }
    @Test
    public void checkTypeOfIdentifierLiteralBool() throws IllTypedException {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new BoolType());
        assertTypechecksExp(newEnv, new BoolType(), "foo");
    }
    @Test
    public void checkTypeOfIdentifierLiteralString() throws IllTypedException {
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
    public void checkTypeOfCastExpIntToString() {
        assertTypechecksExp(null, new StringType(), "(String) 2;");
    }
    @Test
    public void checkTypeOfCastExpBoolToString() {
        assertTypechecksExp(null, new StringType(), "(String) true;");
    }
    @Test
    public void checkTypeOfCastExpSuperClassToSubclass() throws IllTypedException {
        final ClassDecl fooDef = new ClassDecl(new IdentifierLiteral("Foo"), null);
        final ClassDecl barDef = new ClassDecl(new IdentifierLiteral("Bar"),
                new ClassParserType(new IdentifierLiteral("Foo")), null);

        List<Decl> program = new ArrayList<>();
        program.add(fooDef);
        program.add(barDef);

        final Typechecker typechecker = new Typechecker(program);
        final TypeEnvironment env = typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addVariable("x", new ClassType("Foo"));
        Assertions.assertEquals(new ClassType("Bar"), typechecker.typeof(newEnv,
                new CastExp(new ClassParserType(new IdentifierLiteral("Bar")), new IdentifierLiteral("x"))));
    }

    @Test
    public void checkThrowsExceptionCastedClassTypeDoesntExtendExpressionType() throws IllTypedException {
        final ClassDecl fooDef = new ClassDecl(new IdentifierLiteral("Foo"), null);
        final ClassDecl barDef = new ClassDecl(new IdentifierLiteral("Bar"), null);

        List<Decl> program = new ArrayList<>();
        program.add(fooDef);
        program.add(barDef);

        final Typechecker typechecker = new Typechecker(program);
        final TypeEnvironment env = typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addVariable("x", new ClassType("Foo"));
        Assertions.assertThrows(IllTypedException.class, () -> typechecker.typeof(newEnv,
                new CastExp(new ClassParserType(new IdentifierLiteral("Bar")), new IdentifierLiteral("x"))));
    }

    @Test
    public void checkThrowsExceptionCastToIllegalTypeStringToInt() {
        final CastExp exp = new CastExp(new IntParserType(), new StringLiteral("Foo"));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, exp));
    }
    @Test
    public void checkThrowsExceptionCastToIllegalTypeStringToBool() {
        final CastExp exp = new CastExp(new BooleanParserType(), new StringLiteral("Foo"));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, exp));
    }
    @Test
    public void checkThrowsExceptionCastToIllegalTypeIntToBool() {
        final CastExp exp = new CastExp(new BooleanParserType(), new IntegerLiteral(1));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, exp));
    }
    @Test
    public void checkThrowsExceptionCastToIllegalTypeBoolToInt() {
        final CastExp exp = new CastExp(new IntParserType(), new BooleanLiteral(true));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, exp));
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
    public void checkTypeOfNegateUnaryExpSingleTrue() {
        final NegateUnaryExp negate = new NegateUnaryExp(new BooleanLiteral(true));
        assertTypechecksExp(null, new BoolType(), "!true");
    }

    @Test
    public void checkTypeOfNegateUnaryExpSingleFalse() {
        final NegateUnaryExp negate = new NegateUnaryExp(new BooleanLiteral(true));
        assertTypechecksExp(null, new BoolType(), "!false");
    }

    @Test
    public void checkTypeOfNegateUnaryExpBinopIntEqualsTrue() {
        final NegateUnaryExp negate = new NegateUnaryExp(new BinaryOperatorExp(
                "==", new IntegerLiteral(1), new IntegerLiteral(1)));
        assertTypechecksExp(null, new BoolType(), "!(1 == 1)");
    }

    @Test
    public void checkTypeOfNegateUnaryExpBinopBoolEqualsTrue() {
        final NegateUnaryExp negate = new NegateUnaryExp(new BinaryOperatorExp(
                "==", new BooleanLiteral(true), new BooleanLiteral(true)));
        assertTypechecksExp(null, new BoolType(), "!(true == true)");
    }

    @Test
    public void checkTypeOfNegateUnaryExpBinopIntNotEqualsTrue() {
        final NegateUnaryExp negate = new NegateUnaryExp(new BinaryOperatorExp(
                "!=", new IntegerLiteral(1), new IntegerLiteral(1)));
        assertTypechecksExp(null, new BoolType(), "!(1 != 1)");
    }

    @Test
    public void checkTypeOfNegateUnaryExpBinopBoolNotEqualsTrue() {
        final NegateUnaryExp negate = new NegateUnaryExp(new BinaryOperatorExp(
                "!=", new BooleanLiteral(true), new BooleanLiteral(true)));
        assertTypechecksExp(null, new BoolType(), "!(true != true)");
    }

    @Test
    public void checkTypeOfNegateUnaryExpIntegerLiteralThrowsException() {
        final NegateUnaryExp negate = new NegateUnaryExp(new IntegerLiteral(1));
        Assertions.assertThrows(IllTypedException.class, () -> typeof(null, negate));
    }

    @Test
    public void checkTypeOfAssignmentExpIdentifierInteger() throws IllTypedException {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new IntType());

        assertTypechecksExp(newEnv, new IntType(), "foo = 2");
    }
    @Test
    public void checkTypeOfAssignmentExpIdentifierBoolean() throws IllTypedException {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new BoolType());

        assertTypechecksExp(newEnv, new BoolType(), "foo = true");
    }
    @Test
    public void checkTypeOfAssignmentExpIdentifierString() throws IllTypedException {
        TypeEnvironment env = new TypeEnvironment(null, null, null);
        TypeEnvironment newEnv = env.addVariable("foo", new StringType());

        assertTypechecksExp(newEnv, new StringType(), "foo = \"testString\"");
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentSingleEqualsBoolInt() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new BoolType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("=",
                        new IdentifierLiteral("foo"),
                        new IntegerLiteral(2))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentSingleEqualsBoolString() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new BoolType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("=",
                        new IdentifierLiteral("foo"),
                        new StringLiteral("bar"))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentSingleEqualsIntBool() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new IntType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("=",
                        new IdentifierLiteral("foo"),
                        new BooleanLiteral(true))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentSingleEqualsIntString() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new IntType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("=",
                        new IdentifierLiteral("foo"),
                        new StringLiteral("bar"))));
    }


    @Test
    public void checkExceptionThrownTypeMismatchAssignmentSingleEqualsStringInt() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new StringType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("=",
                        new IdentifierLiteral("foo"),
                        new IntegerLiteral(1))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentSingleEqualsStringBool() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new StringType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("=",
                        new IdentifierLiteral("foo"),
                        new BooleanLiteral(true))));
    }


    @Test
    public void checkExceptionThrownTypeMismatchAssignmentPlusEqualsBoolInt() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new BoolType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("+=",
                        new IdentifierLiteral("foo"),
                        new IntegerLiteral(2))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentPlusEqualsBoolString() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new BoolType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("+=",
                        new IdentifierLiteral("foo"),
                        new StringLiteral("bar"))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentPlusEqualsIntBool() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new IntType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("+=",
                        new IdentifierLiteral("foo"),
                        new BooleanLiteral(true))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentPlusEqualsIntString() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new IntType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("+=",
                        new IdentifierLiteral("foo"),
                        new StringLiteral("bar"))));
    }


    @Test
    public void checkExceptionThrownTypeMismatchAssignmentPlusEqualsStringInt() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new StringType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("+=",
                        new IdentifierLiteral("foo"),
                        new IntegerLiteral(1))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentPlusEqualsStringBool() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new StringType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("+=",
                        new IdentifierLiteral("foo"),
                        new BooleanLiteral(true))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentMinusEqualsBoolInt() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new BoolType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("-=",
                        new IdentifierLiteral("foo"),
                        new IntegerLiteral(2))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentMinusEqualsBoolString() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new BoolType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("-=",
                        new IdentifierLiteral("foo"),
                        new StringLiteral("bar"))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentMinusEqualsIntBool() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new IntType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("-=",
                        new IdentifierLiteral("foo"),
                        new BooleanLiteral(true))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentMinusEqualsIntString() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new IntType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("-=",
                        new IdentifierLiteral("foo"),
                        new StringLiteral("bar"))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentMinusEqualsStringInt() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new StringType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("-=",
                        new IdentifierLiteral("foo"),
                        new IntegerLiteral(1))));
    }

    @Test
    public void checkExceptionThrownTypeMismatchAssignmentMinusEqualsStringBool() {
        final Map<String, Type> mutableEnv = new HashMap<>();
        mutableEnv.put("foo", new StringType());
        final ImmutableMap<String, Type> tempEnv = ImmutableMap.copyOf(mutableEnv);
        final TypeEnvironment env = new TypeEnvironment(null, tempEnv, null);

        Assertions.assertThrows(IllTypedException.class, () ->
                typeof(env, new BinaryOperatorExp("-=",
                        new IdentifierLiteral("foo"),
                        new BooleanLiteral(true))));
    }

    @Test
    public void checkExceptionThrownSuperClassNotDefined() throws TokenizerException, ParseException {
        final String testString = "class TestClass extends TestClass2 {}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());

        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);

        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }

    @Test
    public void checkExceptionThrownWrongReturnTypeMethodIntBoolean() throws TokenizerException, ParseException {
        final String testString = "class TestClass{int testMethod(boolean foo){return true;}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }
    @Test
    public void checkExceptionThrownWrongReturnTypeMethodIntString() throws TokenizerException, ParseException {
        final String testString = "class TestClass{int testMethod(boolean foo){return \"bar\";}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }
    @Test
    public void checkExceptionThrownWrongReturnTypeMethodIntNull() throws TokenizerException, ParseException {
        final String testString = "class TestClass{int testMethod(boolean foo){return null;}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }
    @Test
    public void checkExceptionThrownWrongReturnTypeMethodBooleanInt() throws TokenizerException, ParseException {
        final String testString = "class TestClass{boolean testMethod(boolean foo){return 2;}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }
    @Test
    public void checkExceptionThrownWrongReturnTypeMethodBooleanString() throws TokenizerException, ParseException {
        final String testString = "class TestClass{boolean testMethod(boolean foo){return \"foo\";}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }
    @Test
    public void checkExceptionThrownWrongReturnTypeMethodBooleanNull() throws TokenizerException, ParseException {
        final String testString = "class TestClass{boolean testMethod(boolean foo){return null;}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }
    @Test
    public void checkExceptionThrownWrongReturnTypeMethodStringInt() throws TokenizerException, ParseException {
        final String testString = "class TestClass{String testMethod(boolean foo){return 1;}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }
    @Test
    public void checkExceptionThrownWrongReturnTypeMethodStringBoolean() throws TokenizerException, ParseException {
        final String testString = "class TestClass{String testMethod(boolean foo){return false;}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }
    @Test
    public void checkExceptionThrownWrongReturnTypeMethodStringNull() throws TokenizerException, ParseException {
        final String testString = "class TestClass{String testMethod(boolean foo){return null;}}";
        final Parser parser = new Parser(new Tokenizer(testString).tokenize());
        final ClassDecl decl = (ClassDecl)parser.parseProgram().get(0);
        Assertions.assertThrows(IllTypedException.class, () -> typecheckClass(null, decl));
    }

    @Test
    public void checkTypeOfMethodInvocationInteger() {
        final FunctionDefinition intMethod = new FunctionDefinition(new IntType(),
                "testMethod", null, null, new ReturnStmt(new IntegerLiteral(2)));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", intMethod);
        assertTypechecksExp(newEnv, new IntType(), "testMethod();");
    }
    @Test
    public void checkTypeOfMethodInvocationBoolean() {
        final FunctionDefinition boolMethod = new FunctionDefinition(new BoolType(),
                "testMethod", null, null, new ReturnStmt(new BooleanLiteral(true)));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", boolMethod);
        assertTypechecksExp(newEnv, new BoolType(), "testMethod();");
    }

    @Test
    public void checkTypeOfMethodInvocationString() {
        final FunctionDefinition stringMethod = new FunctionDefinition(new StringType(),
                "testMethod", null, null, new ReturnStmt(new StringLiteral("foo")));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);
        assertTypechecksExp(newEnv, new StringType(), "testMethod();");
    }

    @Test
    public void checkTypeOfMethodInvocationThrowsExceptionWrongNumberOfArguments() {

        final List<FormalParameter> parameterList = new ArrayList<>();
        parameterList.add(new FormalParameter(new IntType(), "test"));
        final FunctionDefinition stringMethod = new FunctionDefinition(new StringType(),
                "testMethod", parameterList, null, new ReturnStmt(new StringLiteral("foo")));

        final MethodInvocation mi = new MethodInvocation(new IdentifierLiteral("foo"), new ArgumentList(new IntegerLiteral(1)));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);
        Assertions.assertThrows(IllTypedException.class, () -> typeof(newEnv, mi));
    }

    @Test
    public void checkTypeOfMethodInvocationThrowsExceptionTypeMismatchArglist() {

        final List<FormalParameter> parameterList = new ArrayList<>();
        parameterList.add(new FormalParameter(new BoolType(), "test"));
        final FunctionDefinition stringMethod = new FunctionDefinition(new StringType(),
                "testMethod", parameterList, null, new ReturnStmt(new StringLiteral("foo")));

        final MethodInvocation mi = new MethodInvocation(new IdentifierLiteral("foo"), new ArgumentList(new IntegerLiteral(1)));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);
        Assertions.assertThrows(IllTypedException.class, () -> typeof(newEnv, mi));
    }

    @Test
    public void checkTypeOfMethodInvocationOneArgInt() {

        final List<FormalParameter> params = new ArrayList<>();
        params.add(new FormalParameter(new IntType(), "testInt"));

        final FunctionDefinition stringMethod = new FunctionDefinition(new StringType(),
                "testMethod", params, null, new ReturnStmt(new StringLiteral("foo")));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);
        assertTypechecksExp(newEnv, new StringType(), "testMethod(2);");
    }

    @Test
    public void checkTypeOfMethodInvocationOneArgBool() {

        final List<FormalParameter> params = new ArrayList<>();
        params.add(new FormalParameter(new BoolType(), "testBool"));

        final FunctionDefinition stringMethod = new FunctionDefinition(new StringType(),
                "testMethod", params, null, new ReturnStmt(new StringLiteral("foo")));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);
        assertTypechecksExp(newEnv, new StringType(), "testMethod(true);");
    }

    @Test
    public void checkTypeOfMethodInvocationOneArgString() {

        final List<FormalParameter> params = new ArrayList<>();
        params.add(new FormalParameter(new StringType(), "testInput"));

        final FunctionDefinition stringMethod = new FunctionDefinition(new StringType(),
                "testMethod", params, null, new ReturnStmt(new StringLiteral("foo")));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);
        assertTypechecksExp(newEnv, new StringType(), "testMethod(\"hello\");");
    }

    @Test
    public void checkTypeOfMethodInvocationTwoArgsIntBool() {

        final List<FormalParameter> params = new ArrayList<>();
        params.add(new FormalParameter(new IntType(), "testInput1"));
        params.add(new FormalParameter(new BoolType(), "testInput2"));

        final FunctionDefinition stringMethod = new FunctionDefinition(new StringType(),
                "testMethod", params, null, new ReturnStmt(new StringLiteral("foo")));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);
        assertTypechecksExp(newEnv, new StringType(), "testMethod(2, false);");
    }

    @Test
    public void checkTypeOfMethodInvocationTwoArgsBoolInt() {

        final List<FormalParameter> params = new ArrayList<>();
        params.add(new FormalParameter(new BoolType(), "testInput1"));
        params.add(new FormalParameter(new IntType(), "testInput2"));

        final FunctionDefinition stringMethod = new FunctionDefinition(new StringType(),
                "testMethod", params, null, new ReturnStmt(new StringLiteral("foo")));
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);
        assertTypechecksExp(newEnv, new StringType(), "testMethod(false, 1);");
    }

    @Test
    public void checkThrowsExceptionNumberOfArgsMismatchMethodInvocString() {
        final List<FormalParameter> parameterList = new ArrayList<>();
        parameterList.add(new FormalParameter(new StringType(), "test"));
        final FunctionDefinition stringMethod = new FunctionDefinition(new BoolType(),
                "testMethod", parameterList, null, new ReturnStmt(new StringLiteral("foo")));

        final MethodInvocation mi = new MethodInvocation(new IdentifierLiteral("foo"), new ArgumentList());
        final TypeEnvironment env = Typechecker.createEmptyTypeEnvironment();
        final TypeEnvironment newEnv = env.addFunction("testMethod", stringMethod);

        Assertions.assertThrows(IllTypedException.class, () -> typeof(newEnv, mi));
    }

}
