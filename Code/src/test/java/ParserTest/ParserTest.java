package ParserTest;

import Parser.*;
import Parser.Literals.*;
import Parser.Expressions.*;
import Tokenizer.Tokens.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

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
    public void checkThrowsParseExceptionInvalidInput() {
        final Token[] testTokens = {new IdentifierToken("foo"), new LeftCurlyToken()};
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTest);
    }
}
