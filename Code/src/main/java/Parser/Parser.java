package Parser;

import Parser.Nodes.*;
import Tokenizer.Tokens.*;

import java.util.ArrayList;
import java.util.List;

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
    private void checkTokenIs(final int position, final Token... possibleTokens) throws ParseException {
        boolean member = false;
        for (Token token : tokens) {
            if (tokens[position].equals(token))
                member = true;
        }
        if (!member) {
            throw new ParseException("Expected " + possibleTokens.toString() +
                    "Received" + tokens[position].toString());
        }
    }
    /**
     * tries to parse a primary
     * @param startPos where in the list of tokens we are
     * @return ParseResult<Exp>, either an IntegerExp, IdentifierExp or StringExp
     * @throws ParseException
     */
    public ParseResult<Exp> parsePrimary(final int startPos) throws ParseException {
        if (tokens[startPos] instanceof IdentifierToken) {
            final IdentifierToken asID = (IdentifierToken)tokens[startPos];
            return new ParseResult<Exp>(new IdentifierExp(asID.name), startPos + 1);
        } else if (tokens[startPos] instanceof IntegerToken) {
            final IntegerToken asInt = (IntegerToken)tokens[startPos];
            return new ParseResult<Exp>(new IntegerExp(asInt.value), startPos + 1);
        } else {
            final StringToken asString = (StringToken)tokens[startPos];
            return new ParseResult<Exp>(new StringExp(asString.name), startPos + 1);
        }
    }
    public ParseResult<List<Exp>> parseAdditiveHelper(final int startPos) {
        final List<Exp> resultList = new ArrayList<Exp>();
        int curPos = startPos;

        while (curPos < tokens.length) {
            try{
                checkTokenIs(curPos, new OperatorToken("+"), new OperatorToken("-"));
                final ParseResult<Exp> curPrimary = parsePrimary(curPos + 1);
                curPos = curPrimary.nextPos;
                resultList.add(curPrimary.result);

            } catch(ParseException e) {
                break;
            }
        }
        return new ParseResult<List<Exp>>(resultList, curPos);
    }

    public ParseResult<Exp> parseAdditiveExp(final int startPos) throws ParseException {
        final ParseResult<Exp> start = parsePrimary(startPos);
        final ParseResult<List<Exp>> rest = parseAdditiveHelper(startPos + 1);
        Exp resultExp = start.result;

        for (final Exp otherExp : rest.result) {
            resultExp = new PlusExp(resultExp, otherExp);
        }

        return new ParseResult<Exp>(resultExp, rest.nextPos);

    }
    public ParseResult<Exp> parseExp(final int startPos) throws ParseException {
        if (tokens[startPos] instanceof IntegerToken) {

        }
    }
    public ParseResult<Stmt> parseStmt(final int startPos) throws ParseException {

    }
    public ParseResult<ClassDefinition> parseClassDefinition (final int startPos) throws ParseException {

    }
    public ParseResult<Program> parseProgram(final int startPos) throws ParseException {

        int curPos = 0;
        Program program = new Program();

        while (curPos < tokens.length) {

            program.AST.add(new ClassDefinition(curPos));

        }
    }

    /**
     *
     * @return AST
     * @throws ParseException
     */
    public Program parseToplevel() throws ParseException {
        ParseResult<Program> result = parseProgram(0);

        if (result.nextPos == tokens.length)
            return result.result;
        else throw new ParseException("Tokens left to parse");

    }
}

