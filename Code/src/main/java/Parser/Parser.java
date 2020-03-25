package Parser;

import Parser.Statements.*;
import Parser.Expressions.*;
import Parser.Literals.*;
import Tokenizer.Tokens.*;
import Tokenizer.*;
import Parser.Types.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

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
    private class Pair<U, V> {
        public final U first;
        public final V second;
        public Pair(final U first, final V second) {
            this.first = first;
            this.second = second;
        }
        public String toString() {
            return String.format("(" + first + ", " + second + ")");
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

    /**
     * assigns a precedence to a binary operator
     * @param operator
     * @return
     */
    private static int checkPrecedence(String operator) {

        if (operator.equals("==") || operator.equals("!="))
            return 0;
        else if (operator.equals("<") || operator.equals(">"))
            return 1;
        else if (operator.equals("+") || operator.equals("-"))
            return 2;
        else if (operator.equals("*") || operator.equals("/"))
            return 3;
        else return -1;
    }

    //TODO implement later, just need it now to implement other parseStmts
    /**
     * attempts to parse a block, ie {<block statements>?}
     * @param startPos position on the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    public ParseResult<Stmt> parseBlock(final int startPos) throws ParseException {
        return null;
    }

    /**
     * attempts to parse an If Else statement
     * @param startPos position on the token list
     * @return  ParseResult<Stmt>
     * @throws ParseException
     */
    public ParseResult<Stmt> parseIfElseStmt(final int startPos) throws ParseException {
        checkTokenIs(startPos, new IfToken());
        checkTokenIs(startPos + 1, new LeftParenToken());
        final ParseResult<Exp> guard = parseExp(startPos + 2);
        checkTokenIs(guard.nextPos, new RightParenToken());
        final ParseResult<Stmt> trueBranch = parseBlock(guard.nextPos + 1);
        checkTokenIs(trueBranch.nextPos, new ElseToken());
        final ParseResult<Stmt> falseBranch = parseBlock(trueBranch.nextPos + 1);
        return new ParseResult<Stmt>(new IfElseStmt(
                guard.result, trueBranch.result, falseBranch.result),
                falseBranch.nextPos);
    }

    /**
     * attempts to parse a ForStmt
     * @param startPos current position in the list
     * @return ParseResult<Stmt> with a ForStmt
     * @throws ParseException
     */
    public ParseResult<Stmt> parseForStmt(final int startPos) throws ParseException {
        checkTokenIs(startPos, new ForToken());
        checkTokenIs(startPos + 1, new LeftParenToken());
        final ParseResult<Stmt> forInit = parseForInit(startPos + 2);
        checkTokenIs(forInit.nextPos, new SemiColonToken());
        final ParseResult<Exp> conditional = parseExp(forInit.nextPos + 1);
        checkTokenIs(conditional.nextPos, new SemiColonToken());
        final ParseResult<Stmt> forUpdate = parseStmtExpList(conditional.nextPos + 1);
        checkTokenIs(forUpdate.nextPos, new RightParenToken());
        final ParseResult<Stmt> body = parseBlock(forUpdate.nextPos + 1);
        return new ParseResult<Stmt>(new ForStmt(
                forInit.result,
                conditional.result,
                forUpdate.result,
                body.result), body.nextPos);
    }

    public ParseResult<Stmt> parseForInit(final int startPos) throws ParseException {
        return null;
    }

    /**
     * attempts to parse an ExpStmt, ie <assignment> | <preincrement expr> | <predecrement expr> |
     *<postdecrement expr> | <postincrement expr> | <method invocation> | <class instance creation expr>
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    public ParseResult<Stmt> parseExpStmt (final int startPos) throws ParseException {
        final ParseResult<Exp> stmtExp = parseExp(startPos);
        return new ParseResult<Stmt>(new ExpStmt(stmtExp.result), stmtExp.nextPos);
    }

    public ParseResult<Stmt> parseStmtExpList(final int startPos) throws ParseException {
        return null;
    }

    /**
     * attempts to parse a break statement
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    public ParseResult<Stmt> parseBreakStmt(final int startPos) throws ParseException{
        checkTokenIs(startPos, new BreakToken());
        if (readToken(startPos + 1) instanceof IdentifierToken){
            ParseResult<Exp> identifier = parsePrimary(startPos + 1);
            checkTokenIs(identifier.nextPos, new SemiColonToken());
            return new ParseResult<Stmt>(new BreakStmt("break", identifier.result, ";"),
                    identifier.nextPos + 1);
        } else {
            checkTokenIs(startPos + 1, new SemiColonToken());
            return new ParseResult<Stmt>(new BreakStmt("break", null, ";"),
                    startPos + 2);
        }
    }

    /**
     * attempts to parse a return statement
     * @param startPos position in the token array
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    public ParseResult<Stmt> parseReturnStmt(final int startPos) throws ParseException{
        checkTokenIs(startPos, new ReturnToken());
        if (readToken(startPos + 1) instanceof SemiColonToken){
            return new ParseResult<Stmt>(new ReturnStmt("return", null, ";"), startPos + 2);
        }
        else {
            ParseResult<Exp> returnExp = parseExp(startPos + 1);
            checkTokenIs(returnExp.nextPos, new SemiColonToken());
            return new ParseResult<Stmt>(new ReturnStmt("return", returnExp.result, ";"), returnExp.nextPos + 1);
        }
    }

    /**
     * attempts to parse an expression
     * @param startPos position in the token list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parseExp(final int startPos) throws ParseException {
        final ParseResult<Exp> exp = parseAssignmentExpr(startPos);
        return exp;
    }

    /**
     * <assignment expr ::= <equality expr> | <assignment>
     * attempts to parse an assignment expr
     * <assignment> calls <assignment expr> so the parse function goes through as many <assignment> as possible before
     * moving on to the <equality expr>
     * @param startPos current position in the list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parseAssignmentExpr (final int startPos) throws ParseException{
        ParseResult<Exp> equalityExpr = parseBinaryOperatorExp(startPos);
        ParseResult<List<Pair<String,Exp>>> rest = parseAssignmentExpHelper(equalityExpr.nextPos,
                new OperatorToken("="),
                new OperatorToken("+="),
                new OperatorToken("-="));

        Exp finalResult = equalityExpr.result;

        for (final Pair<String, Exp> current : rest.result) {
            finalResult = new BinaryOperatorExp(current.first, finalResult, current.second);
        }

        return new ParseResult<Exp>(finalResult, rest.nextPos);
    }

    public ParseResult<List<Pair<String, Exp>>> parseAssignmentExpHelper(int curPos, final Token... operators) {
        final List<Pair<String, Exp>> resultList = new ArrayList<Pair<String, Exp>>();

        while(curPos < tokens.size()) {
            try {
                checkTokenIs( curPos, operators);
                final ParseResult<Exp> currentLeftSide = parsePrimary(curPos + 1);
                final OperatorToken currentOperator = (OperatorToken)readToken(curPos);
                curPos = currentLeftSide.nextPos;
                resultList.add(new Pair(currentOperator.name, currentLeftSide.result));

            } catch (final ParseException e) {
                break;
            }
        }
        return new ParseResult<List<Pair<String, Exp>>>(resultList, curPos);
    }
    /**
     * attempts to parse a binary operator expression in this sequence
     * (excluded <assignment expr> because of differences)
     * 1. equality exp
     * 2. relational exp
     * 3. additive exp
     * 4. multiplicative exp
     *
     * @param startPos current position in the list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parseBinaryOperatorExp(final int startPos) throws ParseException {

        final ParseResult<Exp> starting = parseUnaryExp(startPos);
        Exp resultExp = starting.result;

        final ParseResult<List<Pair<String, Exp>>> rest =
                parseBinaryOperatorExpHelper(starting.nextPos,
                        new OperatorToken("=="), new OperatorToken("!="),
                        new OperatorToken(">"), new OperatorToken("<"),
                        new OperatorToken("+"), new OperatorToken("-"),
                        new OperatorToken("*"), new OperatorToken("/"));

        //calculating precedence using Dijkstras shunting yard algorithm
        Stack<Exp> operands = new Stack<>();
        Stack<String> operators = new Stack<>();

        //first value
        operands.push(resultExp);

        //going through the parsed list of Pair(Op, Unary exp)
        for (final Pair pair : rest.result) {
            final String currentOperator = (String) pair.first;
            final int currentOpPrecedence = checkPrecedence(currentOperator);
            final Exp currentExp = (Exp) pair.second;

            //no operators to compare the new operator with
            if (operators.isEmpty()) {
                operators.push(currentOperator);
                operands.push(currentExp);
            }
            //pop stack with higher precedence operators
            else {
                while (!operators.isEmpty() && currentOpPrecedence <= checkPrecedence(operators.peek())) {
                    //have to pop right before left to get left-associativity
                    final Exp right = operands.pop();
                    final Exp left = operands.pop();
                    resultExp = new BinaryOperatorExp(operators.pop(), left, right);
                    operands.push(resultExp);   //push the result expression on the operand stack
                }
                //push the new highest precedence operator and the next expression
                operators.push(currentOperator);
                operands.push(currentExp);
            }
        }
        //list of Pair(Op, unary exp) is empty, run through the stacks and generate expressions
        while (!operators.isEmpty()) {
            //have to pop right before left to get left-associativity
            final Exp right = operands.pop();
            final Exp left = operands.pop();
            resultExp = new BinaryOperatorExp(operators.pop(), left, right);
            operands.push(resultExp);
        }
        return new ParseResult<Exp>(resultExp, rest.nextPos);

    }

    /**
     * greedy implementation of binary op exp parsing. using a private class Pair to store both
     * the expressions and the operators in between
     * @param startPos current position in the list
     * @param operators list of valid operators
     * @return ParseResult<List<Pair<String, Exp>>>
     */
    private ParseResult<List<Pair<String, Exp>>> parseBinaryOperatorExpHelper(final int startPos, final Token... operators) {
        final List<Pair<String, Exp>> resultList = new ArrayList<Pair<String, Exp>>();
        int curPos = startPos;

        while (curPos < tokens.size()) {
            try {
                checkTokenIs(curPos, operators);
                final OperatorToken currentOperator = (OperatorToken)readToken(curPos);
                final ParseResult<Exp> currentUnaryExp = parseUnaryExp(curPos + 1);
                curPos = currentUnaryExp.nextPos;
                resultList.add(new Pair(currentOperator.name, currentUnaryExp.result));
            } catch (ParseException e) {
                break;
            }
        }
        return new ParseResult<List<Pair<String, Exp>>>(resultList, curPos);
    }
    /**
     * attempts to parse a UnaryExp, ie NoIncDecUnaryExp, PreIncrDecrExp
     * @param startPos current position in the list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parseUnaryExp(final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);
        if (currentToken instanceof OperatorToken) {
            final OperatorToken OpToken = (OperatorToken) currentToken;
            //++
            if (OpToken.name.equals("++")) {
                final ParseResult<Exp> preIncrExp = parsePreIncrExpr(startPos);
                return new ParseResult<Exp>(preIncrExp.result, preIncrExp.nextPos);
            }
            //--
            else if (OpToken.name.equals("--")) {
                final ParseResult<Exp> preDecrExp = parsePreDecrExpr(startPos);
                return new ParseResult<Exp>(preDecrExp.result, preDecrExp.nextPos);
            }
            else throw new ParseException("invalid unary incr/decr operator" + OpToken.name);
        }
        //<unary expr no incr decr
        else {
            final ParseResult<Exp> noIncrDecrExp = parseNoIncDecUnaryExp(startPos);
            return new ParseResult<Exp>(noIncrDecrExp.result, noIncrDecrExp.nextPos);
        }
    }
    /**
     * attempts to parse a NoIncDecUnaryExp, ie primary, !<unary exp>, <cast exp>
     * @param startPos
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parseNoIncDecUnaryExp(final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);
        //! <unary exp>
        if (currentToken instanceof OperatorToken) {
            checkTokenIs(startPos, new OperatorToken("!"));
            final ParseResult<Exp> unaryExp = parseUnaryExp(startPos + 1);
            return new ParseResult<Exp>(new NegateUnaryExp(unaryExp.result), unaryExp.nextPos);
        }
        //<cast exp> || (expr)
        else if (currentToken instanceof LeftParenToken) {
            final Token nextToken = validPosition(startPos + 1) ?
                    readToken(startPos + 1) : null;
            final Token nextNextToken = validPosition(startPos + 2) ?
                    readToken(startPos + 2) : null;
            //TODO maybe rework at some point?
            // assumes that IdentifierToken have no other use alone between two parens
            if (nextToken instanceof BooleanTypeToken ||
                nextToken instanceof IntTypeToken ||
                nextToken instanceof StringTypeToken ||
               (nextToken instanceof IdentifierToken &&
                nextNextToken instanceof RightParenToken)) {

                final ParseResult<Exp> castExp = parseCastExp(startPos);
                return new ParseResult<Exp>(castExp.result, castExp.nextPos);
            }
            else {
                final ParseResult<Exp> primary = parsePrimary(startPos);
                return new ParseResult<Exp>(primary.result, primary.nextPos);
            }
        }
        //postfix
        else {
            final ParseResult<Exp> postfix = parsePostfixExp(startPos);
            return new ParseResult<Exp>(postfix.result, postfix.nextPos);
        }
    }

    /**
     * attempts to parse a type cast
     * @param startPos current position in the list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parseCastExp(final int startPos) throws ParseException {
        checkTokenIs(startPos, new LeftParenToken());
        ParseResult<Type> castType = parseType(startPos + 1);
        checkTokenIs(castType.nextPos, new RightParenToken());
        ParseResult<Exp> unaryExp = parseUnaryExp(castType.nextPos + 1);
        return new ParseResult<Exp>(new CastExp(castType.result, unaryExp.result), unaryExp.nextPos);
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
        else if (currentToken instanceof IdentifierToken){
            IdentifierToken identifierToken = (IdentifierToken) currentToken;
            return new ParseResult<Type>(new ClassType(new IdentifierLiteral(identifierToken.name)), startPos + 1);
        }
        else
            throw new ParseException("Not a valid type to cast: " + currentToken);
    }


    /**
     * attempts to parse a postfix exp, ie <primary>, <postincr exp>, <postdecr exp>
     * @param startPos current position in the list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parsePostfixExp(final int startPos) throws ParseException {
        final ParseResult<Exp> primary = parsePrimary(startPos);
        Token nextToken = validPosition(primary.nextPos) ? readToken(primary.nextPos) : null;

        if (nextToken instanceof OperatorToken) {
            OperatorToken postfixOp = (OperatorToken) nextToken;
            if (postfixOp.name.equals("++")) {
                final ParseResult<Exp> postIncrExp = parsePostIncrExpr(startPos);
                return new ParseResult<Exp>(postIncrExp.result, postIncrExp.nextPos);
            }
            else if (postfixOp.name.equals("--")) {
                final ParseResult<Exp> postDecrExp = parsePostDecrExpr(startPos);
                return new ParseResult<Exp>(postDecrExp.result, postDecrExp.nextPos);
            }
            else return new ParseResult<Exp>(primary.result, primary.nextPos);
        }
        //primary
        else {
            return new ParseResult<Exp>(primary.result, primary.nextPos);
        }
    }
    /**
     * attempts to parse a preincrement expression
     * @param startPos position in the token array
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parsePreIncrExpr(final int startPos) throws ParseException {
        checkTokenIs(startPos, new OperatorToken("++"));
        final ParseResult<Exp> preIncrExpr = parseUnaryExp(startPos + 1);
        return new ParseResult<Exp>(new PreIncrDecrExp(preIncrExpr.result, "++"), preIncrExpr.nextPos);
    }
    /**
     * attempts to parse a preincrement expression
     * @param startPos position in the token array
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    public ParseResult<Exp> parsePreDecrExpr(final int startPos) throws ParseException {
        checkTokenIs(startPos, new OperatorToken("--"));
        final ParseResult<Exp> preDecrExpr = parseUnaryExp(startPos + 1);
        return new ParseResult<Exp>(new PreIncrDecrExp(preDecrExpr.result, "--"), preDecrExpr.nextPos);
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
        } //super, TODO might move?
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
                final ParseResult<Exp> curArg = parseExp(curPos);
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

    public Exp parseTopLevel() throws ParseException {
        final ParseResult<Exp> toplevel = parseExp(0);
        if (toplevel.nextPos == tokens.size()) {
            return toplevel.result;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }

    //Testing out some statements, remove later
    public Stmt parseTest() throws ParseException {
        final ParseResult<Stmt> toplevel = parseReturnStmt(0);
        if (toplevel.nextPos == tokens.size()) {
            return toplevel.result;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }

    public Stmt parseTest2() throws ParseException {
        final ParseResult<Stmt> toplevel = parseBreakStmt(0);
        if (toplevel.nextPos == tokens.size()) {
            return toplevel.result;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }

    //test main
    public static void main(String[] args) {
        final String input = "(foo) bar";
        final Tokenizer tokenizer = new Tokenizer(input);

        try {
            final List<Token> tokens = tokenizer.tokenize();
            final Parser parser = new Parser(tokens);
            final Exp parsed = parser.parseTopLevel();
            System.out.println(parsed);
        } catch(Exception e) {
            e.printStackTrace();
        }

        final String input2 = "return 5;";
        final Tokenizer tokenizer2 = new Tokenizer(input2);

        try {
            final List<Token> tokens2 = tokenizer2.tokenize();
            final Parser parser = new Parser(tokens2);
            final Stmt parsed = parser.parseTest();
            System.out.println(parsed);
        } catch(Exception e) {
            e.printStackTrace();
        }

        final String input3 = "break;";
        final Tokenizer tokenizer3 = new Tokenizer(input3);

        try {
            final List<Token> tokens3 = tokenizer3.tokenize();
            final Parser parser = new Parser(tokens3);
            final Stmt parsed = parser.parseTest2();
            System.out.println(parsed);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}

