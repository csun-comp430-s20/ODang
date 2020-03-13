package ParserTest;

import Parser.*;
import Parser.Literals.*;
import Tokenizer.Tokens.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ParserTest {

    public static void assertParsesLiteral(final Literal expected,
                                    final Token... tokens)
            throws ParseException {
        Assertions.assertEquals(expected, (new Parser(tokens)).parseLiteral(0));
    }

    @Test
    public void checkParsesInteger() throws ParseException {
        assertParsesLiteral(new IntegerLiteral(1), new IntegerToken(1));
    }
    @Test
    public void checkParsesString() throws ParseException {
        assertParsesLiteral(new StringLiteral("foo"), new StringToken("foo"));
    }
    @Test
    public void checkParsesBoolean() throws ParseException {
        assertParsesLiteral(new BooleanLiteral(true), new BooleanToken(true));
    }
    @Test
    public void checkParsesIdentifier() throws ParseException {
        assertParsesLiteral(new IdentifierLiteral("foobar"), new IdentifierToken("foobar"));
    }

}
