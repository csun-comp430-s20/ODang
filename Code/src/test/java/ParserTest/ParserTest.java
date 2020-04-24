package ParserTest;

import Parser.*;
import Parser.Types.*;
import Parser.Literals.*;
import Parser.Expressions.*;
import Parser.Declarations.*;
import Parser.Statements.*;
import Tokenizer.*;
import Tokenizer.Tokens.*;
//import com.sun.org.apache.xpath.internal.operations.String;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;

public class ParserTest {

    public static void assertParsesExpFromString(final Exp expected, final String received) {
        final Tokenizer tokenizer = new Tokenizer(received);
        try {
            Assertions.assertEquals(expected, (new Parser(tokenizer.tokenize())).parseTopLevelExp());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void assertParsesStmtFromString(final Stmt expected, final String received) {
        final Tokenizer tokenizer = new Tokenizer(received);
        try {
            Assertions.assertEquals(expected, (new Parser(tokenizer.tokenize())).parseTopLevelStmt());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static void assertParsesClassFromString(final Decl expected, final String received) {
        final Tokenizer tokenizer = new Tokenizer(received);
        try {
            Assertions.assertEquals(expected, (new Parser(tokenizer.tokenize())).parseTopLevelClass());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void assertCorrectStmtToString (final String expected, final String received) {
        final Tokenizer tokenizer = new Tokenizer(received);
        try {
            Assertions.assertEquals(expected, (new Parser(tokenizer.tokenize())).parseTopLevelStmt().toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    //***EXP TESTS***//

    @Test
    public void checkParsesInteger() throws ParseException {
        assertParsesExpFromString(new IntegerLiteral(1), "1");
    }

    @Test
    public void checkParsesString() throws ParseException {
        assertParsesExpFromString(new StringLiteral("foo"), "\"foo\"");
    }

    @Test
    public void checkParsesBoolean() throws ParseException {
        assertParsesExpFromString(new BooleanLiteral(true), "true");
    }

    @Test
    public void checkParsesIdentifier() throws ParseException {
        assertParsesExpFromString(new IdentifierLiteral("foobar"), "foobar");
    }

    @Test
    public void checkParsesNull() throws ParseException {
        assertParsesExpFromString(new NullLiteral(), "null");
    }

    @Test
    public void checkParsesPrimaryThis() throws ParseException {
        assertParsesExpFromString(new ThisExp(), "this");
    }

    @Test
    public void checkParsesClassInstanceCreationNoArgs() {
        assertParsesExpFromString(new ClassInstanceExp(new IdentifierLiteral("foo"), new ArgumentList()), "new foo()");
    }

    @Test
    public void checkParsesClassInstanceCreation() {
        final Exp expected = new ClassInstanceExp(new IdentifierLiteral("Foo"),
                new ArgumentList(
                        new IntegerLiteral(2),
                        new BooleanLiteral(true)
                ));
        assertParsesExpFromString(expected, "new Foo(2, true)");
    }

    @Test
    public void checkParsesMethodInvocationMethodNameArgList() {
        final Exp expected = new MethodInvocation(new IdentifierLiteral("testMethod"),
                new ArgumentList(
                        new IntegerLiteral(2),
                        new BooleanLiteral(true)
                ));
        assertParsesExpFromString(expected, "testMethod(2, true)");
    }
    @Test
    public void checkParsesFieldAccessThis() {
        final Exp expected = new FieldAccessExp(new ThisExp(),
                new MethodInvocation(new IdentifierLiteral("testMethod"),
                        new ArgumentList(
                                new IntegerLiteral(2),
                                new BooleanLiteral(true)
                        )));
        assertParsesExpFromString(expected, "this.testMethod(2, true)");
    }
    @Test
    public void checkParsesFieldAccessSuper() {
        final Exp expected = new FieldAccessExp(new SuperExp(),
                new MethodInvocation(new IdentifierLiteral("testMethod"),
                        new ArgumentList(
                                new IntegerLiteral(2),
                                new BooleanLiteral(true)
                        )));
        assertParsesExpFromString(expected, "super.testMethod(2, true)");
    }
    @Test
    public void checkParsesFieldAccessOneDot() {
        final Exp expected = new FieldAccessExp(new IdentifierLiteral("foo"),
                new MethodInvocation(new IdentifierLiteral("testMethod"),
                        new ArgumentList(
                                new IntegerLiteral(2),
                                new BooleanLiteral(true)
                        )));
        assertParsesExpFromString(expected, "foo.testMethod(2, true)");
    }

    @Test
    public void checkParsesFieldAccessTwoDots() {
        final Exp expected = new FieldAccessExp(new IdentifierLiteral("foo"),
                new FieldAccessExp(new IdentifierLiteral("bar"),
                        new MethodInvocation(new IdentifierLiteral("testMethod"),
                                new ArgumentList(
                                        new IntegerLiteral(2),
                                        new BooleanLiteral(true)
                                ))));
        assertParsesExpFromString(expected, "foo.bar.testMethod(2, true)");
    }
    @Test
    public void checkParsesBinaryOperatorExpOnePlus() {
        final Exp expected = new BinaryOperatorExp("+",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesExpFromString(expected, "1+2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoPlus() {
        final Exp expected = new BinaryOperatorExp("+",
                new BinaryOperatorExp("+",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesExpFromString(expected, "1+2+3");
    }
    //TODO rework minus?
    @Test
    public void checkParsesBinaryOperatorExpOneMinus() {
        final Exp expected = new BinaryOperatorExp("-",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesExpFromString(expected, "1 - 2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoMinus() {
        final Exp expected = new BinaryOperatorExp("-",
                new BinaryOperatorExp("-",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesExpFromString(expected, "1 - 2 - 3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneDivide() {
        final Exp expected = new BinaryOperatorExp("/",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesExpFromString(expected, "1/2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoDivide() {
        final Exp expected = new BinaryOperatorExp("/",
                new BinaryOperatorExp("/",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesExpFromString(expected, "1/2/3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneMult() {
        final Exp expected = new BinaryOperatorExp("*",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesExpFromString(expected, "1*2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoMult() {
        final Exp expected = new BinaryOperatorExp("*",
                new BinaryOperatorExp("*",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesExpFromString(expected, "1*2*3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneLessThan() {
        final Exp expected = new BinaryOperatorExp("<",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesExpFromString(expected, "1<2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoLessThan() {
        final Exp expected = new BinaryOperatorExp("<",
                new BinaryOperatorExp("<",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesExpFromString(expected, "1<2<3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneGreaterThan() {
        final Exp expected = new BinaryOperatorExp(">",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesExpFromString(expected, "1>2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoGreaterThan() {
        final Exp expected = new BinaryOperatorExp(">",
                new BinaryOperatorExp(">",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesExpFromString(expected, "1>2>3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneReferenceEquals() {
        final Exp expected = new BinaryOperatorExp("==",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesExpFromString(expected, "1==2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoReferenceEquals() {
        final Exp expected = new BinaryOperatorExp("==",
                new BinaryOperatorExp("==",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesExpFromString(expected, "1==2==3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneNotEquals() {
        final Exp expected = new BinaryOperatorExp("!=",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesExpFromString(expected, "1!=2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoNotEquals() {
        final Exp expected = new BinaryOperatorExp("!=",
                new BinaryOperatorExp("!=",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesExpFromString(expected, "1!=2!=3");
    }
    @Test
    public void checkParsesCastExpInt() {
        final Exp expected = new CastExp(
                new PrimitiveType(
                        new IntType()), new IdentifierLiteral("foo"));
        assertParsesExpFromString(expected, "(int) foo");
    }
    @Test
    public void checkParsesCastExpString() {
        final Exp expected = new CastExp(
                new PrimitiveType(
                        new StringType()), new IdentifierLiteral("foo"));
        assertParsesExpFromString(expected, "(String) foo");
    }
    @Test
    public void checkParsesCastExpBoolean() {
        final Exp expected = new CastExp(
                new PrimitiveType(
                        new BooleanType()), new IdentifierLiteral("foo"));
        assertParsesExpFromString(expected, "(boolean) foo");
    }
    @Test
    public void checkParsesCastExpClass() {
        final Exp expected = new CastExp(
                new ClassType(
                        new IdentifierLiteral("Foo")), new IdentifierLiteral("bar"));
        assertParsesExpFromString(expected, "(Foo) bar");
    }
    @Test
    public void checkParsesAssignmentEqualsOneOperator() {
        final Exp expected = new BinaryOperatorExp("=", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo = 1");
    }
    @Test
    public void checkParsesAssignmentTwoOperatorsWithParen() {
        final Exp expected = new BinaryOperatorExp("=", new IdentifierLiteral("foo"),
                new BinaryOperatorExp("=", new IdentifierLiteral("bar"), new IntegerLiteral(1)));
        assertParsesExpFromString(expected, "foo = (bar = 1)");
    }
    @Test
    public void checkParsesAssignmentPlusEqualsOneOperator() {
        final Exp expected = new BinaryOperatorExp("+=", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo += 1");
    }
    @Test
    public void checkParsesAssignmentMinusEqualsOneOperator() {
        final Exp expected = new BinaryOperatorExp("-=", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo -= 1");
    }
    @Test
    public void checkParsesEqualityExpNotEqualsOneOperator() {
        final Exp expected = new BinaryOperatorExp("!=", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo != 1");
    }
    @Test
    public void checkParsesEqualityExpReferenceEqualsOneOperator() {
        final Exp expected = new BinaryOperatorExp("==", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo == 1");
    }
    @Test
    public void checkParseRelationalGreaterThanOneOperator() {
        final Exp expected = new BinaryOperatorExp(">", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo > 1");
    }
    @Test
    public void checkParseRelationalLessThanOneOperator() {
        final Exp expected = new BinaryOperatorExp("<", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo < 1");
    }
    @Test
    public void checkParsesAdditivePlusOneOperator() {
        final Exp expected = new BinaryOperatorExp("+", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo + 1");
    }
    @Test
    public void checkParsesAdditiveMinusOneOperator() {
        final Exp expected = new BinaryOperatorExp("-", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo - 1");
    }
    @Test
    public void checkParsesMultiplicativeMultiplicationOneOperator() {
        final Exp expected = new BinaryOperatorExp("*", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo * 1");
    }
    @Test
    public void checkParsesMultiplicativeDivisionOneOperator() {
        final Exp expected = new BinaryOperatorExp("/", new IdentifierLiteral("foo"), new IntegerLiteral(1));
        assertParsesExpFromString(expected, "foo / 1");
    }
    @Test
    public void checkThrowsParseExceptionInvalidOperatorSequence() {
        final List<Token> testTokens = Arrays.asList(new IntegerToken(1),
                new OperatorToken("+"), new OperatorToken("/"));
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTopLevelExp);
    }
    @Test
    public void checkThrowsParseExceptionInvalidInputMethodOpenLeftParen() {
        final List<Token> testTokens = Arrays.asList(new IdentifierToken("foo"), new LeftParenToken());
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTopLevelExp);
    }
    @Test
    public void checkThrowsParseExceptionInvalidInputMethodOnlyRightParen() {
        final List<Token> testTokens = Arrays.asList(new IdentifierToken("foo"), new RightParenToken());
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTopLevelExp);
    }

    //***STMT TESTS***//

    @Test
    public void checkParsesEmptyStmt() {
        final Stmt expected = new EmptyStmt();
        assertParsesStmtFromString(expected, ";");
    }
    @Test
    public void checkPrintsParsedEmptyStmt() {
        assertCorrectStmtToString("EmptyStmt", ";");
    }
    @Test
    public void checkParsesReturnStmt() {
        final Stmt expected = new ReturnStmt(new IdentifierLiteral("foo"));
        assertParsesStmtFromString(expected, "return foo;");
    }
    @Test
    public void checkPrintsParsedReturnStmt() {
        assertCorrectStmtToString("ReturnStmt (IdentifierLiteral<foo>)", "return foo;");
    }

    @Test
    public void checkParsesBreakStmt() {
        final Stmt expected = new BreakStmt(new IdentifierLiteral("foo"));
        assertParsesStmtFromString(expected, "break foo;");
    }
    @Test
    public void checkPrintsParsedBreakStmt() {
        assertCorrectStmtToString("BreakStmt (IdentifierLiteral<foo>)", "break foo;");
    }
    @Test
    public void checkParsesForStmtOneInit() {
        final Stmt expected = new ForStmt(new LocalVardec(
                new PrimitiveType(new IntType()), new VarDeclaratorList(
                new VarDeclarator(new IdentifierLiteral("foo"), new IntegerLiteral(0)))),
                new BinaryOperatorExp("<", new IdentifierLiteral("foo"), new IntegerLiteral(5)),
                new StmtExprList(new StmtExpr(new PostIncrDecrExp(new IdentifierLiteral("foo"), "++"))),
                new ReturnStmt(new IdentifierLiteral("foo")));

        assertParsesStmtFromString(expected, "for(int foo = 0; foo < 5; foo++) {return foo;}");
    }
    @Test
    public void checkParsesForStmtTwoInit() {
        final Stmt expected = new ForStmt(new LocalVardec(
                new PrimitiveType(new IntType()), new VarDeclaratorList(
                new VarDeclarator(new IdentifierLiteral("foo"), new IntegerLiteral(0)),
                new VarDeclarator(new IdentifierLiteral("bar"), new IntegerLiteral(1)))),
                new BinaryOperatorExp("<", new IdentifierLiteral("foo"), new IntegerLiteral(5)),
                new StmtExprList(new StmtExpr(new PostIncrDecrExp(new IdentifierLiteral("foo"), "++"))),
                new ReturnStmt(new IdentifierLiteral("foo")));

        assertParsesStmtFromString(expected, "for(int foo = 0, bar = 1; foo < 5; foo++) {return foo;}");
    }
    @Test
    public void checkParsesWhileStmt() {
        final Stmt expected = new WhileStmt(
                new BinaryOperatorExp("<", new IdentifierLiteral("foo"), new IntegerLiteral(5)),
                new StmtExpr(new BinaryOperatorExp(
                        "+=", new IdentifierLiteral("foo"), new IntegerLiteral(1))));

        assertParsesStmtFromString(expected, "while (foo < 5) {foo += 1;}");
    }
    @Test
    public void checkParsesIfElseStmtEmptyTrueFalseBranches() {
        final Stmt expected = new IfElseStmt(new BinaryOperatorExp(
                "==", new IdentifierLiteral("foo"), new IntegerLiteral(2)),
                null,
                null);
        assertParsesStmtFromString(expected, "if (foo == 2) {} else {}");

    }
    @Test
    public void checkParsesIfElseStmtWithFieldAccess() {
        final Stmt expected = new IfElseStmt(new BinaryOperatorExp(
                "==", new IdentifierLiteral("foo"), new IntegerLiteral(2)),
                new StmtExpr(new FieldAccessExp(new IdentifierLiteral("foo"), new MethodInvocation(
                        new IdentifierLiteral("method"), new ArgumentList(null)))),
                new StmtExpr(new FieldAccessExp(new IdentifierLiteral("bar"), new MethodInvocation(
                        new IdentifierLiteral("method2"), new ArgumentList(null)))));

        assertParsesStmtFromString(expected, "if (foo == 2) {foo.method();} else {bar.method2();}");
    }
    @Test
    public void checkParsesBlockStmts() {
        final Stmt expected = new BlockStmt(new BlockStmt(new StmtExpr(new BinaryOperatorExp("=",
                new IdentifierLiteral("x"), new IntegerLiteral(2))),
                new StmtExpr(new BinaryOperatorExp("=", new IdentifierLiteral("y"), new IntegerLiteral(5)))),
                new ReturnStmt(new IdentifierLiteral("x")));
        assertParsesStmtFromString(expected, "{x = 2; y = 5; return x;}");
    }

    //***DECL TESTS***//
    @Test
    public void checkParsesEmptyClassNoSuper() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"), null);
        assertParsesClassFromString(expected, "class Foo{}");
    }
    @Test
    public void checkParsesEmptyClassSuper() {
        final Decl expected = new SubClassDecl(
                new IdentifierLiteral("Foo"), new ClassType(
                new IdentifierLiteral("Bar")), null);
        assertParsesClassFromString(expected, "class Foo extends Bar{}");
    }
    @Test
    public void checkParsesClassWithEmptyConstructor() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassBodyDecs(
                        new ConstructorDecl(
                        new ConstructorDeclarator(
                                new IdentifierLiteral("Foo"), null),
                        null)));
        assertParsesClassFromString(expected, "class Foo{Foo() {}}");
    }
    @Test
    public void checkParsesClassSuperWithEmptyConstructor() {
        final Decl expected = new SubClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassType(
                        new IdentifierLiteral("Bar")),
                new ClassBodyDecs(
                        new ConstructorDecl(
                        new ConstructorDeclarator(
                                new IdentifierLiteral("Foo"), null),
                        null)));
        assertParsesClassFromString(expected, "class Foo extends Bar {Foo() {}}");
    }
    @Test
    public void checkParsesClassOneParamConstructor() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassBodyDecs(
                        new ConstructorDecl(
                        new ConstructorDeclarator(
                                new IdentifierLiteral("Foo"), new FormalParamList(
                                new FormalParam(
                                        new PrimitiveType(new IntType()),
                                        new IdentifierLiteral("x")))), null)));
        assertParsesClassFromString(expected, "class Foo{Foo(int x) {}}");
    }
    @Test
    public void checkParsesClassTwoParamsConstructor() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassBodyDecs(
                        new ConstructorDecl(
                        new ConstructorDeclarator(
                                new IdentifierLiteral("Foo"), new FormalParamList(
                                new FormalParam(
                                        new PrimitiveType(new IntType()),
                                        new IdentifierLiteral("x")),
                                new FormalParam(
                                        new PrimitiveType(new StringType()),
                                        new IdentifierLiteral("testString"))
                        )), null)));
        assertParsesClassFromString(expected, "class Foo{Foo(int x, String testString) {}}");
    }
    @Test
    public void checkParsesSubClassOneParamConstructor() {
        final Decl expected = new SubClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassType(new IdentifierLiteral("Bar")),
                new ClassBodyDecs(
                        new ConstructorDecl(
                        new ConstructorDeclarator(
                                new IdentifierLiteral("Foo"), new FormalParamList(
                                new FormalParam(
                                        new PrimitiveType(new IntType()),
                                        new IdentifierLiteral("x")))), null)));
        assertParsesClassFromString(expected, "class Foo extends Bar{Foo(int x) {}}");
    }
    @Test
    public void checkParsesSubClassTwoParamsConstructor() {
        final Decl expected = new SubClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassType(new IdentifierLiteral("Bar")),
                new ClassBodyDecs(
                        new ConstructorDecl(
                        new ConstructorDeclarator(
                                new IdentifierLiteral("Foo"), new FormalParamList(
                                new FormalParam(
                                        new PrimitiveType(new IntType()),
                                        new IdentifierLiteral("x")),
                                new FormalParam(
                                        new PrimitiveType(new StringType()),
                                        new IdentifierLiteral("testString")))),
                                new ConstructorBody(null, null))));
        assertParsesClassFromString(expected, "class Foo extends Bar{Foo(int x, String testString) {}}");
    }
    @Test
    public void checkParsesClassWithFieldDec() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassBodyDecs(
                        new FieldDecl(
                        new PrimitiveType(new IntType()),
                        new VarDeclaratorList(
                                new VarDeclarator(
                                        new IdentifierLiteral("x"),
                                        new IntegerLiteral(2)
                                )
                        )
                )));
        assertParsesClassFromString(expected, "class Foo{int x = 2;}");
    }
    //Always succeed no matter what, have to debug
   /* @Test
    public void checkParsesClassWithMethodDecWithNoBody() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"),
                new MethodDecl(new MethodHeader(
                        new PrimitiveType(new IntType()),
                        new MethodDeclarator(
                                new IdentifierLiteral("func"),
                                new FormalParamList(
                                        new FormalParam(
                                                new PrimitiveType(new StringType()),
                                                new IdentifierLiteral("c"))))

                        ), null));
        assertParsesClassFromString(expected, "class Foo{int func(String c);}");
    }*/
    @Test
    public void checkParsesClassWithTwoVarDecs() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassBodyDecs(
                                new FieldDecl(
                                        new PrimitiveType(new IntType()),
                                        new VarDeclaratorList(
                                                new VarDeclarator(
                                                        new IdentifierLiteral("x"),
                                                        new IntegerLiteral(2)))),
                                new FieldDecl(
                                        new PrimitiveType(new IntType()),
                                        new VarDeclaratorList(
                                                new VarDeclarator(
                                                        new IdentifierLiteral("y"),
                                                        new IntegerLiteral(0))))
                )
        );
        assertParsesClassFromString(expected, "class Foo{int x = 2; int y = 0;}");
    }
    @Test
    public void checkParsesClassWithMethodOverloadWithNoBody() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassBodyDecs(
                        new MethodOverloadDecl(
                        new OverloadDecl(
                                new IdentifierLiteral("Foo"), "+",
                                new FormalParamList(
                                        new FormalParam(
                                                new PrimitiveType(new IntType()),
                                                new IdentifierLiteral("x")))),
                        null
                )));
        assertParsesClassFromString(expected, "class Foo{Foo operator + (int x);}");
    }
    @Test
    public void checkParsesClassWithMethodOverloadWithVarDecl() {
        final Decl expected = new ClassDecl(
                new IdentifierLiteral("Foo"),
                new ClassBodyDecs(
                new MethodOverloadDecl(
                        new OverloadDecl(
                                new IdentifierLiteral("Foo"), "+",
                                new FormalParamList(
                                        new FormalParam(
                                                new PrimitiveType(
                                                        new IntType()),
                                                new IdentifierLiteral("x")))),
                        new LocalVardec (
                                new PrimitiveType(
                                        new IntType()),
                                new VarDeclaratorList (
                                        new VarDeclarator (
                                                new IdentifierLiteral("y"),
                                                new IntegerLiteral(5)))))));
        assertParsesClassFromString(expected, "class Foo{Foo operator + (int x){int y = 5;}}");

    }
}