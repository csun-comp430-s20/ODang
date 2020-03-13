package Parser;

import Parser.Expressions.Exp;
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
    private void checkTokenIs(final int position, final Token token) throws ParseException {
        final Token currentToken = readToken(position);
        if (!currentToken.equals(token)) {
            throw new ParseException("Expected " + token.toString() +
                    "Received" + currentToken.toString());
        }
    }
    private Token readToken(final int position) throws ParseException {
        if (position < tokens.length) {
            return tokens[position];
        } else {
            throw new ParseException("Position out of bounds: " + position);
        }
    }
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

}

