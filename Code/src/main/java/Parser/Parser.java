package Parser;

import Parser.Expressions.*;
import Parser.Literals.*;
import Tokenizer.Tokens.*;
import Tokenizer.*;
import Parser.Types.*;

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
     * Checks the current position in the list, done a lot in this code
     * so wrote a method for it
     * @return true or false depending on current position on the list
     */
    private boolean validPosition(final int inputPos) {
        return inputPos < tokens.size();
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
        //parseAssignment
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
     * attempts to parse a type
     * @param startPos current position in the list
     * @return ParseResult<Type>
     * @throws ParseException
     */
    public ParseResult<Type> parseType(final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);
        //boolean
        if (currentToken instanceof BooleanTypeToken) {
            return new ParseResult<Type>(new PrimitiveType(new BooleanType()), startPos + 1);
        }
        //int
        else if (currentToken instanceof IntTypeToken) {
            return new ParseResult<Type>(new PrimitiveType(new IntType()), startPos + 1);
        }
        //<primitive type>:str
        else if (currentToken instanceof StringTypeToken) {
            return new ParseResult<Type>(new PrimitiveType(new StringType()), startPos + 1);
        }
        //<class type>: <identifier>
        else {
            return new ParseResult<Type>(new ClassType(), startPos + 1);
        }
    }
    /**
     * attempts to parse a postincrement expression
     * @param startPos position in the token array
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parsePostIncrExpr(final int startPos) throws ParseException {
        final ParseResult<Exp> postfixExpr = parsePrimary(startPos);
        checkTokenIs(postfixExpr.nextPos, new OperatorToken("++"));
        PostIncrDecrExp result = new PostIncrDecrExp(postfixExpr.result, "++");
        return new ParseResult<Exp>(result, postfixExpr.nextPos + 1);
    }

    /**
     * attempts to parse a postdecrement expression
     * @param startPos position in the token array
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parsePostDecrExpr(final int startPos) throws ParseException {
        final ParseResult<Exp> postfixExpr = parsePrimary(startPos);
        checkTokenIs(postfixExpr.nextPos, new OperatorToken("--"));
        PostIncrDecrExp result = new PostIncrDecrExp(postfixExpr.result, "--");
        return new ParseResult<Exp>(result, postfixExpr.nextPos + 1);
    }

    /**
     * attempts to parse a primary
     * @param startPos current position in the list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parsePrimary(final int startPos) throws ParseException {
        final ParseResult<Exp> primary = parsePrimaryHelper(startPos);

        //<primary> ::= <method invocation> : <field access> : <primary> . <identifier> (<arg list>?)
        final ParseResult<List<Exp>> rest = parseFieldAccessExp(primary.nextPos);
        Exp resultExp = primary.result;

        for (final Exp otherExp : rest.result) {
            resultExp = new FieldAccessExp(resultExp, otherExp);
        }
        return new ParseResult<Exp>(resultExp, rest.nextPos);
    }

    /**
     * internal method to avoid left recursion in field access parsing
     * attempts to parse a primary, ie literal/this/(exp)/class creation/method invocation
     * @param startPos position in the token list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    private ParseResult<Exp> parsePrimaryHelper (final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);
        final Token nextToken = validPosition(startPos + 1) ? readToken(startPos + 1) : null;
        //<primary> ::= this
        if (currentToken instanceof ThisToken) {
            return new ParseResult<Exp>(new ThisExp(), startPos + 1);
        }
        //<primary> ::= <class instance creation expr>
        else if (currentToken instanceof NewToken) {
            final ParseResult<Exp> classType = parseLiteral(startPos + 1);
            checkTokenIs(classType.nextPos, new LeftParenToken());
            final ParseResult<ArgumentList> argumentList = parseArgumentList(classType.nextPos + 1);
            checkTokenIs(argumentList.nextPos, new RightParenToken());
            return new ParseResult<Exp>(new ClassInstanceExp(classType.result, argumentList.result), argumentList.nextPos + 1);
        }
        //<primary> ::= <method invocation> : <method name> (<arg list>?)
        else if (currentToken instanceof IdentifierToken &&
                nextToken instanceof LeftParenToken) {
            final ParseResult<Exp> identifier = parseLiteral(startPos);
            final ParseResult<ArgumentList> argumentList = parseArgumentList(identifier.nextPos + 1);
            checkTokenIs(argumentList.nextPos, new RightParenToken());
            return new ParseResult<Exp>(new MethodInvocation(identifier.result, argumentList.result), argumentList.nextPos + 1);
        }
        //<primary> ::= (expr)
        else if (currentToken instanceof LeftParenToken) {
            final ParseResult<Exp> inner = parseExp(startPos + 1);
            checkTokenIs(inner.nextPos, new RightParenToken());
            return new ParseResult<Exp>(inner.result, inner.nextPos + 1);
        } //super
        else if (currentToken instanceof SuperToken) {
            return new ParseResult<Exp>(new SuperExp(), startPos + 1);
        }
        //<primary> ::= <literal>
        else {
            final ParseResult<Exp> literal = parseLiteral(startPos);
            return new ParseResult<Exp>(literal.result, literal.nextPos);
        }
    }

    /**
     * attempts to parse a field access, greedy approach that parses until no more
     * DotTokens appear
     * @param startPos
     * @return List of expressions
     */
     public ParseResult<List<Exp>> parseFieldAccessExp(final int startPos) {
        final List<Exp> resultList = new ArrayList<Exp>();
        int curPos = startPos;

        while (curPos < tokens.size()) {
            try {
                checkTokenIs(curPos, new DotToken());
                final ParseResult<Exp> curPrimary = parsePrimary(curPos + 1);
                curPos = curPrimary.nextPos;
                resultList.add(curPrimary.result);
            } catch (final ParseException e) {
                break;
            }
        }
        return new ParseResult<List<Exp>>(resultList, curPos);
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
        final String input = "this.toString()";
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

