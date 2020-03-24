package ParserTest;

import Parser.*;
import Parser.Types.*;
import Parser.Literals.*;
import Parser.Expressions.*;
import Tokenizer.*;
import Tokenizer.Tokens.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.List;

public class ParserTest {

    public static void assertParsesFromString(final Exp expected, final String received) {
        final Tokenizer tokenizer = new Tokenizer(received);
        try {
            Assertions.assertEquals(expected, (new Parser(tokenizer.tokenize())).parseTopLevel()); //TODO change to parseExp
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void checkParsesInteger() throws ParseException {
        assertParsesFromString(new IntegerLiteral(1), "1");
    }

    @Test
    public void checkParsesString() throws ParseException {
        assertParsesFromString(new StringLiteral("foo"), "\"foo\"");
    }

    @Test
    public void checkParsesBoolean() throws ParseException {
        assertParsesFromString(new BooleanLiteral(true), "true");
    }

    @Test
    public void checkParsesIdentifier() throws ParseException {
        assertParsesFromString(new IdentifierLiteral("foobar"), "foobar");
    }

    @Test
    public void checkParsesNull() throws ParseException {
        assertParsesFromString(new NullLiteral(), "null");
    }

    @Test
    public void checkParsesPrimaryThis() throws ParseException {
        assertParsesFromString(new ThisExp(), "this");
    }

    @Test
    public void checkParsesClassInstanceCreationNoArgs() {
        assertParsesFromString(new ClassInstanceExp(new IdentifierLiteral("foo"), new ArgumentList()), "new foo()");
    }

    @Test
    public void checkParsesClassInstanceCreation() {
        final Exp expected = new ClassInstanceExp(new IdentifierLiteral("Foo"),
                new ArgumentList(
                        new IntegerLiteral(2),
                        new BooleanLiteral(true)
                ));
        assertParsesFromString(expected, "new Foo(2, true)");
    }

    @Test
    public void checkParsesMethodInvocationMethodNameArgList() {
        final Exp expected = new MethodInvocation(new IdentifierLiteral("testMethod"),
                new ArgumentList(
                        new IntegerLiteral(2),
                        new BooleanLiteral(true)
                ));
        assertParsesFromString(expected, "testMethod(2, true)");
    }
    @Test
    public void checkParsesFieldAccessThis() {
        final Exp expected = new FieldAccessExp(new ThisExp(),
                new MethodInvocation(new IdentifierLiteral("testMethod"),
                        new ArgumentList(
                                new IntegerLiteral(2),
                                new BooleanLiteral(true)
                        )));
        assertParsesFromString(expected, "this.testMethod(2, true)");
    }
    @Test
    public void checkParsesFieldAccessSuper() {
        final Exp expected = new FieldAccessExp(new SuperExp(),
                new MethodInvocation(new IdentifierLiteral("testMethod"),
                        new ArgumentList(
                                new IntegerLiteral(2),
                                new BooleanLiteral(true)
                        )));
        assertParsesFromString(expected, "super.testMethod(2, true)");
    }
    @Test
    public void checkParsesFieldAccessOneDot() {
        final Exp expected = new FieldAccessExp(new IdentifierLiteral("foo"),
                new MethodInvocation(new IdentifierLiteral("testMethod"),
                        new ArgumentList(
                                new IntegerLiteral(2),
                                new BooleanLiteral(true)
                        )));
        assertParsesFromString(expected, "foo.testMethod(2, true)");
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
        assertParsesFromString(expected, "foo.bar.testMethod(2, true)");
    }
    @Test
    public void checkParsesBinaryOperatorExpOnePlus() {
        final Exp expected = new BinaryOperatorExp("+",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesFromString(expected, "1+2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoPlus() {
        final Exp expected = new BinaryOperatorExp("+",
                new BinaryOperatorExp("+",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesFromString(expected, "1+2+3");
    }
    //TODO rework minus?
    @Test
    public void checkParsesBinaryOperatorExpOneMinus() {
        final Exp expected = new BinaryOperatorExp("-",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesFromString(expected, "1 - 2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoMinus() {
        final Exp expected = new BinaryOperatorExp("-",
                new BinaryOperatorExp("-",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesFromString(expected, "1 - 2 - 3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneDivide() {
        final Exp expected = new BinaryOperatorExp("/",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesFromString(expected, "1/2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoDivide() {
        final Exp expected = new BinaryOperatorExp("/",
                new BinaryOperatorExp("/",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesFromString(expected, "1/2/3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneMult() {
        final Exp expected = new BinaryOperatorExp("*",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesFromString(expected, "1*2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoMult() {
        final Exp expected = new BinaryOperatorExp("*",
                new BinaryOperatorExp("*",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesFromString(expected, "1*2*3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneLessThan() {
        final Exp expected = new BinaryOperatorExp("<",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesFromString(expected, "1<2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoLessThan() {
        final Exp expected = new BinaryOperatorExp("<",
                new BinaryOperatorExp("<",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesFromString(expected, "1<2<3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneGreaterThan() {
        final Exp expected = new BinaryOperatorExp(">",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesFromString(expected, "1>2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoGreaterThan() {
        final Exp expected = new BinaryOperatorExp(">",
                new BinaryOperatorExp(">",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesFromString(expected, "1>2>3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneReferenceEquals() {
        final Exp expected = new BinaryOperatorExp("==",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesFromString(expected, "1==2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoReferenceEquals() {
        final Exp expected = new BinaryOperatorExp("==",
                new BinaryOperatorExp("==",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesFromString(expected, "1==2==3");
    }
    @Test
    public void checkParsesBinaryOperatorExpOneNotEquals() {
        final Exp expected = new BinaryOperatorExp("!=",
                new IntegerLiteral(1),
                new IntegerLiteral(2));
        assertParsesFromString(expected, "1!=2");
    }
    @Test
    public void checkParsesBinaryOperatorExpTwoNotEquals() {
        final Exp expected = new BinaryOperatorExp("!=",
                new BinaryOperatorExp("!=",
                        new IntegerLiteral(1),
                        new IntegerLiteral(2)),
                new IntegerLiteral(3));
        assertParsesFromString(expected, "1!=2!=3");
    }
    @Test
    public void checkparsesTypeInt() {
        final Exp expected = new CastExp(
                new PrimitiveType(
                        new IntType()), new IdentifierLiteral("foo"));
        assertParsesFromString(expected, "(int) foo");
    }
    @Test
    public void checkThrowsParseExceptionInvalidOperatorSequence() {
        final List<Token> testTokens = Arrays.asList(new IntegerToken(1),
                new OperatorToken("+"), new OperatorToken("/"));
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTopLevel);
    }
    @Test
    public void checkThrowsParseExceptionInvalidInputMethodOpenLeftParen() {
        final List<Token> testTokens = Arrays.asList(new IdentifierToken("foo"), new LeftParenToken());
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTopLevel);
    }
    @Test
    public void checkThrowsParseExceptionInvalidInputMethodOnlyRightParen() {
        final List<Token> testTokens = Arrays.asList(new IdentifierToken("foo"), new RightParenToken());
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTopLevel);
    }
}