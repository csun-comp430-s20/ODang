package ParserTest;

import Parser.*;
import Parser.Literals.*;
import Parser.Expressions.*;
import Tokenizer.*;
import Tokenizer.Tokens.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.ArrayList;

public class ParserTest {

    public static void assertParsesExp(final Exp expected, final Token... tokens)
            throws ParseException {
        Assertions.assertEquals(expected, (new Parser(tokens)).parseTest());
    }
    @Test
    public void checkParsesInteger() throws ParseException {
        assertParsesExp(new IntegerLiteral(1), new IntegerToken(1));
    }
    @Test
    public void checkParsesString() throws ParseException {
        assertParsesExp(new StringLiteral("foo"), new StringToken("foo"));
    }
    @Test
    public void checkParsesBoolean() throws ParseException {
        assertParsesExp(new BooleanLiteral(true), new BooleanToken(true));
    }
    @Test
    public void checkParsesIdentifier() throws ParseException {
        assertParsesExp(new IdentifierLiteral("foobar"), new IdentifierToken("foobar"));
    }
    @Test
    public void checkParsesPrimaryThis() throws ParseException {
        assertParsesExp(new ThisExp(), new ThisToken());
    }
    @Test
    public void checkParsesClassInstanceCreation() throws ParseException, TokenizerException {
        final Tokenizer tokenizer = new Tokenizer(" new Foo(2, true)");
        final List<Token> testTokens = tokenizer.tokenize();
        System.out.println(testTokens);
        assertParsesExp(new ClassInstance(new IdentifierLiteral("Foo"),
                                          new ArgumentList(new IntegerLiteral(2),
                                                           new BooleanLiteral(true))), (Token) testTokens);
    }
    @Test
    public void checkThrowsParseExceptionInvalidInput() {
        final Token[] testTokens = {new IdentifierToken("foo"), new LeftCurlyToken()};
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTest);
    }
}
