package Parser;

import Parser.Literals.*;
import Tokenizer.Tokens.*;

public class Parser {

    private final Token[] tokens;

    public Parser(final Token[] tokens) {
        this.tokens = tokens;
    }

    private class ParseResult<A> {
        public final A result;
        public final int nextPos;
        public ParseResult(final A result, final int nextPos) {
            this.result = result;
            this.nextPos = nextPos;
        }
    }

    /**
     * checks if a token is of a specific subtype
     * @param position position in the token array
     * @param token the token you want to check
     * @throws ParseException if the tokens don't match
     */
    private void checkTokenIs(final int position, final Token token) throws ParseException {
        final Token currentToken = readToken(position);
        if (!currentToken.equals(token)) {
            throw new ParseException("Expected " + token.toString() +
                    "Received" + currentToken.toString());
        }
    }

    /**
     * attempts to read in a token
     * @param position position in the token array
     * @return a Token
     * @throws ParseException
     */
    private Token readToken(final int position) throws ParseException {
        if (position < tokens.length) {
            return tokens[position];
        } else {
            throw new ParseException("Position out of bounds: " + position);
        }
    }

    /**
     * attempts to parse a literal
     * @param startPos position in the token array
     * @return ParseResult<Literal>
     * @throws ParseException
     */
    public ParseResult<Literal> parseLiteral (final int startPos) throws ParseException {
        final Token token = readToken(startPos);
        if (token instanceof IdentifierToken) {
            final IdentifierToken asID = (IdentifierToken)token;
            return new ParseResult<Literal>(new IdentifierLiteral(asID.name), startPos + 1);
        } else if (token instanceof BooleanToken) {
            final BooleanToken asBool = (BooleanToken)token;
            return new ParseResult<Literal>(new BooleanLiteral(asBool.value), startPos + 1);
        } else if(token instanceof IntegerToken) {
            final IntegerToken asInt = (IntegerToken)token;
            return new ParseResult<Literal>(new IntegerLiteral(asInt.value), startPos + 1);
        } else {
            final StringToken asString = (StringToken)token;
            return new ParseResult<Literal>(new StringLiteral(asString.name), startPos + 1);
        }

    }

    //for testing, not the final version
    public Literal parseTest() throws ParseException {
        final ParseResult<Literal> toplevel = parseLiteral(0);
        if (toplevel.nextPos == tokens.length) {
            return toplevel.result;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }

}

