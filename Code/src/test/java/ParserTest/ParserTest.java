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

    public static void assertParses(final String received, final Exp... expected) throws ParseException, TokenizerException {
        final Tokenizer tokenizer = new Tokenizer(received);
        Assertions.assertEquals(expected, (new Parser(tokenizer.tokenize())).parseTest());
    }

    public static void assertParsesExp(final Exp expected, final Token... tokens)
            throws ParseException {
        List<Token> tokenList = Arrays.asList(tokens);
        Assertions.assertEquals(expected, (new Parser(tokenList)).parseTest());
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
        final ClassInstance expected = new ClassInstance(new IdentifierLiteral("Foo"),
                                                          new ArgumentList(
                                                                  new IntegerLiteral(2),
                                                                  new BooleanLiteral(true)
                                                          ));
        assertParses("new Foo(2 true)", expected);
    }
    @Test
    public void checkThrowsParseExceptionInvalidInput() {
        final List<Token> testTokens = Arrays.asList(new IdentifierToken("foo"), new LeftCurlyToken());
        final Parser testParser = new Parser(testTokens);
        Assertions.assertThrows(ParseException.class, testParser::parseTest);
    }
}
