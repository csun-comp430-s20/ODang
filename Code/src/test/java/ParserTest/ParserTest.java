package ParserTest;

import Parser.*;
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
        Assertions.assertEquals(expected, (new Parser(tokenizer.tokenize())).parseTest());
        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName());
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
        assertParsesFromString(new ClassInstance(new IdentifierLiteral("foo"), new ArgumentList()), "new foo()");
    }
    @Test
    public void checkParsesClassInstanceCreation() {
        final Exp expected = new ClassInstance(new IdentifierLiteral("Foo"),
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
    public void checkParsesFieldAccessOneDot() {
        final Exp expected = new FieldAccess(new IdentifierLiteral("foo"),
                                             new MethodInvocation(new IdentifierLiteral("testMethod"),
                                                     new ArgumentList(
                                                             new IntegerLiteral(2),
                                                             new BooleanLiteral(true)
                                                     )));
        assertParsesFromString(expected, "foo.testMethod(2, true)");
    }
    @Test
    public void checkParsesFieldAccessTwoDots() {
        final Exp expected = new FieldAccess(new IdentifierLiteral("foo"),
                new FieldAccess(new IdentifierLiteral("bar"),
                new MethodInvocation(new IdentifierLiteral("testMethod"),
                        new ArgumentList(
                                new IntegerLiteral(2),
                                new BooleanLiteral(true)
                        ))));
        assertParsesFromString(expected, "foo.bar.testMethod(2, true)");
    }
    @Test
    public void checkThrowsParseExceptionInvalidInput() {
        final List<Token> testTokens = Arrays.asList(new IdentifierToken("foo"), new LeftCurlyToken());
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTest);
    }
}
