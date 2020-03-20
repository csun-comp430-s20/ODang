package Parser;

import Parser.Expressions.*;
import Parser.Literals.*;
import Tokenizer.Tokens.*;
import Tokenizer.*;

import java.util.List;
import java.util.ArrayList;

public class Parser {

    private final List<Token> tokens;

    public Parser(final List<Token> tokens) {
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
     * @param tokens list of possible tokens you want to check
     * @throws ParseException if the tokens don't match
     */
    private void checkTokenIs(final int position, final Token... tokens) throws ParseException {
        boolean member = false;
        final Token currentToken = readToken(position);
        for (Token t : tokens) {
            if (t.equals(currentToken))
                member = true;
        }
        if (!member) {
            throw new ParseException("Expected " + tokens.toString() +
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
        if (position < tokens.size()) {
            return tokens.get(position);
        } else {
            throw new ParseException("Position out of bounds: " + position);
        }
    }
    //TODO implement
    public ParseResult<Exp> parseExp(final int startPos) throws ParseException {
        return null;
    }
    /**
     * attempts to parse an expression
     * @param startPos position in the token array
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parseAdditive(final int startPos) throws ParseException {
        final ParseResult<Exp> starting = parseLiteral(startPos);
        final ParseResult<List<Exp>> rest = parseAdditiveExpHelper(starting.nextPos);
        Exp resultExp = starting.result;

        if (starting.nextPos < tokens.size()) {

        }
        return null;
    }
    //TODO finish
    public ParseResult<List<Exp>> parseAdditiveExpHelper(final int startPos) {
        final List<Exp> resultList = new ArrayList<Exp>();
        int curPos = startPos;

        while (curPos < tokens.size()) {
            try {
                checkTokenIs(curPos, new OperatorToken("+"), new OperatorToken("-"));
                final ParseResult<Exp> curLiteral = parseLiteral(curPos + 1);
                curPos = curLiteral.nextPos;
                resultList.add(curLiteral.result);
            } catch (final ParseException e) {
                break;
            }
        }
        return null;
    }

    /**
     * attempts to parse a primary, ie identifier/string/integer/boolean/null
     * @param startPos position in the token list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    //TODO implement method invocation
    public ParseResult<Exp> parsePrimary (final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);
        if (currentToken instanceof ThisToken) {
            return new ParseResult<Exp>(new ThisExp(), startPos + 1);
        } else if (currentToken instanceof LeftParenToken) {
            final ParseResult<Exp> inner = parseExp(startPos + 1);
            checkTokenIs(inner.nextPos, new RightParenToken());
            return new ParseResult<Exp>(inner.result, inner.nextPos + 1);
        } else if (currentToken instanceof NewToken) {
            final ParseResult<Exp> classType = parseLiteral(startPos + 1);
            checkTokenIs(classType.nextPos, new LeftParenToken());
            final ParseResult<ArgumentList> argumentList = parseArgumentList(classType.nextPos + 1);
            checkTokenIs(argumentList.nextPos, new RightParenToken());
            return new ParseResult<Exp>(new ClassInstance(classType.result, argumentList.result), argumentList.nextPos + 1);
        }
        //Literal
        else {
            final ParseResult<Exp> literal = parseLiteral(startPos);
            return new ParseResult<Exp> (literal.result, literal.nextPos);
        }

    }

    /**
     * tries to parse a list of expressions
     * @param startPos position in the token list
     * @return A ParseResult
     */
    public ParseResult<ArgumentList> parseArgumentList (final int startPos) {
        ArgumentList argList = new ArgumentList();
        int curPos = startPos;
        while (curPos < tokens.size()) {
            try {
                //case of separation between arguments
                if (readToken(curPos) instanceof CommaToken)
                    curPos++;
                final ParseResult<Exp> curArg = parsePrimary(curPos);   //TODO change to parseExp
                curPos = curArg.nextPos;
                argList.expList.add(curArg.result);
            } catch(final ParseException e) {
                break;
            }
        }
        return new ParseResult<ArgumentList>(argList, curPos);
    }
    /**
     * attempts to parse a literal
     * @param startPos position in the token list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parseLiteral (final int startPos) throws ParseException {
        final Token token = readToken(startPos);
        if (token instanceof IdentifierToken) {
            final IdentifierToken asID = (IdentifierToken)token;
            return new ParseResult<Exp>(new IdentifierLiteral(asID.name), startPos + 1);
        } else if (token instanceof BooleanToken) {
            final BooleanToken asBool = (BooleanToken)token;
            return new ParseResult<Exp>(new BooleanLiteral(asBool.value), startPos + 1);
        } else if(token instanceof IntegerToken) {
            final IntegerToken asInt = (IntegerToken)token;
            return new ParseResult<Exp>(new IntegerLiteral(asInt.value), startPos + 1);
        } else if (token instanceof StringToken){
            final StringToken asString = (StringToken)token;
            return new ParseResult<Exp>(new StringLiteral(asString.name), startPos + 1);
        } else if (token instanceof NullToken) {
            return new ParseResult<Exp>(new NullLiteral(), startPos + 1);
        }
        else
            throw new ParseException("not a valid token: " + tokens.get(startPos));
    }

    //for testing, not the final version
    public Exp parseTest() throws ParseException {
        final ParseResult<Exp> toplevel = parsePrimary(0);
        if (toplevel.nextPos == tokens.size()) {
            return toplevel.result;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }
    //test main
    public static void main(String[] args) {
        final String input = "new Foo(2, true, foo, \"hello\", null)";
        final Tokenizer tokenizer = new Tokenizer(input);

        try {
            final List<Token> tokens = tokenizer.tokenize();
            final Parser parser = new Parser(tokens);
            final Exp parsed = parser.parseTest();
            System.out.println(parsed);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}

