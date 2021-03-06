package Parser;

import Parser.Declarations.*;
import Parser.Statements.*;
import Parser.Expressions.*;
import Parser.Literals.*;
import Tokenizer.Tokens.*;
import Parser.Types.*;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class Parser {

    private final List<Token> tokens;

    public Parser(final List<Token> tokens) {
        this.tokens = tokens;
    }

    public class ParseResult<A> {
        public final A result;
        public final int nextPos;
        public ParseResult(final A result, final int nextPos) {
            this.result = result;
            this.nextPos = nextPos;
        }
        public A getResult() {
            return result;
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
            throw new ParseException("Expected " + Arrays.toString(tokens) +
                    ", received " + currentToken.toString());
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
     * @param operator input operator
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

    public List<Decl> parseProgram() throws ParseException {
        final ParseResult<Decl> topLevel = parseClassDecs(0);
        final ClassDecs program = (ClassDecs)topLevel.result;
        if (topLevel.nextPos == tokens.size()) {
            return program.classDecs;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }
    /**
     * attempts to parse a <class decs>
     *     ie <class dec> | <class decs> <class dec>
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseClassDecs(final int startPos) throws ParseException {
        final ParseResult<Decl> firstClassDecl = parseClassDecl(startPos);
        final ClassDecs resultClassDecs = new ClassDecs(firstClassDecl.result);

        final ParseResult<List<Decl>> rest = parseClassDecsHelper(firstClassDecl.nextPos);
        for (final Decl otherDecl : rest.result) {
            resultClassDecs.classDecs.add(otherDecl);
        }
        return new ParseResult<Decl>(resultClassDecs, rest.nextPos);
    }

    /**
     * greedy method for parsing class declarations
     * @param startPos position in the list
     * @return ParseResult<List<Stmt>> containing all but the first Decl if any
     * @throws ParseException
     */
    private ParseResult<List<Decl>> parseClassDecsHelper (final int startPos) {
        final List<Decl> resultList = new ArrayList<Decl>();
        int curPos = startPos;

        while (curPos < tokens.size()) {

            try{
                final ParseResult<Decl> curClassDec = parseClassDecl(curPos);
                curPos = curClassDec.nextPos;
                resultList.add(curClassDec.result);
                if (readToken(curPos) instanceof RightCurlyToken)
                    break;
            } catch (ParseException e) {
                break;
            }
        }
        return new ParseResult<List<Decl>>(resultList, curPos);
    }
    /**
     * attempts to parse a class declaration, ie class <identifier> <super>? <class body>
     * @param startPos position in the list
     * @return ParseResult<Decl> containing a parsed class
     * @throws ParseException
     */
    private ParseResult<Decl> parseClassDecl(final int startPos) throws ParseException {
        checkTokenIs(startPos, new ClassToken());
        final ParseResult<Exp> identifier = parseExp(startPos + 1);

        //no <super>
        if (readToken(identifier.nextPos) instanceof LeftCurlyToken) {
            final ParseResult<Decl> classBody = parseClassBody(identifier.nextPos);
            return new ParseResult<Decl>(new ClassDecl(
                    identifier.result,
                    classBody.result),
                    classBody.nextPos);
        }
        //<super>
        else if (readToken(identifier.nextPos) instanceof ExtendsToken){
            final ParseResult<ParserType> classType = parseType(identifier.nextPos + 1);
            if (!(classType.result instanceof ClassParserType))
                throw new ParseException("Invalid class type: " + classType.result);
            final ParseResult<Decl> classBody = parseClassBody(classType.nextPos);
            return new ParseResult<Decl>(new ClassDecl(
                    identifier.result,
                    classType.result,
                    classBody.result),
                    classBody.nextPos);

        }
        else {
            throw new ParseException("Invalid form of class definition");
        }
    }

    /**
     * attempts to parse a <class body>
     *     ie { <class body decs>? }
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseClassBody(final int startPos) throws ParseException {
        checkTokenIs(startPos, new LeftCurlyToken());
        //empty class
        if (readToken(startPos + 1) instanceof RightCurlyToken) {
            return new ParseResult<Decl>(null, startPos + 2);

        }
        else {
            final ParseResult<Decl> classBodyDecs = parseClassBodyDecs(startPos + 1);
            checkTokenIs(classBodyDecs.nextPos, new RightCurlyToken());
            return new ParseResult<Decl>(classBodyDecs.result, classBodyDecs.nextPos + 1);
        }
    }

    /**
     * attempts to parse a <class body decs>
     *     ie <class body dec> | <class body decs> <class body dec>
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseClassBodyDecs(final int startPos) throws ParseException {

        final ParseResult<List<Decl>> classBodyDecs = parseClassBodyDeclHelper(startPos);
        final ClassBodyDecs resultClassBodyDecs = new ClassBodyDecs();
        for (Decl decl : classBodyDecs.result)
            resultClassBodyDecs.classBodyDecs.add(decl);
        return new ParseResult<Decl>(resultClassBodyDecs, classBodyDecs.nextPos);
    }

    /**
     * greedy method for parsing class body declarations
     * @param startPos position in the list
     * @return ParseResult<List<Stmt>> containing all but the first Decl if any
     * @throws ParseException
     */
    private ParseResult<List<Decl>> parseClassBodyDeclHelper(final int startPos) {
        final List<Decl> resultList = new ArrayList<Decl>();
        int curPos = startPos;
        while (curPos < tokens.size()) {

            try{
                final ParseResult<Decl> curClassBodyDecl = parseClassBodyDecl(curPos);
                curPos = curClassBodyDecl.nextPos;
                resultList.add(curClassBodyDecl.result);
                if (readToken(curPos) instanceof RightCurlyToken)
                    break;
            } catch (ParseException e) {
                break;
            }
        }
        return new ParseResult<List<Decl>>(resultList, curPos);
    }

    /**
     * attempts to parse a <class body dec>, i.e <class member dec> | <constructor dec>
     * @param startPos position in the token list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseClassBodyDecl(final int startPos) throws ParseException {
        try {
            final ParseResult<Decl> classMemberDecl = parseClassMemberDecl(startPos);
            return new ParseResult<Decl>(classMemberDecl.result, classMemberDecl.nextPos);
        } catch (ParseException e) {
            final ParseResult<Decl> constructorDecl = parseConstructorDecl(startPos);
            return new ParseResult<Decl>(constructorDecl.result, constructorDecl.nextPos);
        }
    }

    /**
     * attempts to parse a <class member dec>, i.e <field dec> | <method dec>
     * @param startPos position in the token list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseClassMemberDecl(final int startPos) throws ParseException {
        //tries to parse as fieldDecl, if it fails tries to parse as methodDecl
        try {
            final ParseResult<Decl> fieldDecl = parseFieldDecl(startPos);
            return new ParseResult<Decl>(fieldDecl.result, fieldDecl.nextPos);
        } catch (ParseException d) {
            final ParseResult<Decl> methodDecl = parseMethodDecl(startPos);
            return new ParseResult<Decl>(methodDecl.result, methodDecl.nextPos);
        }
    }

    /**
     * attempts to parse a <constructor dec>
     *     ie <constructor declarator> <constructor body>
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseConstructorDecl(final int startPos) throws ParseException {
        final ParseResult<Decl> constructorDeclarator = parseConstructorDeclarator(startPos);
        final ParseResult<Decl> constructorBody = parseConstructorBody(constructorDeclarator.nextPos);
        return new ParseResult<Decl>(new ConstructorDecl(
                constructorDeclarator.result,
                constructorBody.result),
                constructorBody.nextPos);
    }

    /**
     * attempts to parse a <constructor declarator>
     *     ie <identifier> ( <formal param list>? )
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseConstructorDeclarator(final int startPos) throws ParseException {
        final ParseResult<Exp> identifier = parseLiteral(startPos);
        checkTokenIs(identifier.nextPos, new LeftParenToken());
        if (readToken(identifier.nextPos + 1) instanceof RightParenToken) {
            return new ParseResult<Decl>(new ConstructorDeclarator(
                    identifier.result, new FormalParamList()), identifier.nextPos + 2);
        }

        final ParseResult<FormalParamList> paramList = parseFormalParamList(identifier.nextPos + 1);
        checkTokenIs(paramList.nextPos, new RightParenToken());
        return new ParseResult<Decl>(new ConstructorDeclarator(
                identifier.result, paramList.result), paramList.nextPos + 1);
    }

    /**
     * attempts to parse a <constructor body>
     *     ie { <explicit constructor invocation>? <block stmts> }
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseConstructorBody(final int startPos) throws ParseException {
        checkTokenIs(startPos, new LeftCurlyToken());

        //non-empty <explicit constructor invocation>
        if (readToken(startPos + 1) instanceof ThisToken ||
                readToken(startPos + 1) instanceof SuperToken) {
            final ParseResult<Decl> explicitConstructorInvocation = parseExplicitConstructorInvocation(startPos + 1);
            final ParseResult<List<Stmt>> blockStmts = parseBlockStmtHelper(explicitConstructorInvocation.nextPos);
            checkTokenIs(blockStmts.nextPos, new RightCurlyToken());
            return new ParseResult<Decl>(new ConstructorBody(
                    explicitConstructorInvocation.result,
                    blockStmts.result), blockStmts.nextPos + 1);
        }
        else {
            final ParseResult<List<Stmt>> blockStmts = parseBlockStmtHelper(startPos + 1);
            checkTokenIs(blockStmts.nextPos, new RightCurlyToken());
            return new ParseResult<Decl>(new ConstructorBody(
                    null,
                    blockStmts.result), blockStmts.nextPos + 1);
        }
    }

    /**
     * attempts to parse an <explicit constructor invocation>
     *     ie super ( <argument list>? )
     * @param startPos position in the token list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseExplicitConstructorInvocation(final int startPos) throws ParseException {

        final Token token = readToken(startPos);

        if (!(token instanceof SuperToken))
            throw new ParseException("Not a valid explicit construction invocation");

        checkTokenIs(startPos + 1, new LeftParenToken());
        final ParseResult<ArgumentList> argList = parseArgumentList(startPos + 2);
        checkTokenIs(argList.nextPos, new RightParenToken());
        return new ParseResult<Decl>(
                new ExplicitConstructorInvocation(argList.result),
                argList.nextPos + 1);
    }
    /**
     * attempts to parse a field declaration, ie <type> <var declarators> ;
     * @param startPos position in the token list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseFieldDecl(final int startPos) throws ParseException{
        final ParseResult<ParserType> type = parseType(startPos);
        final ParseResult<Decl> varDeclarators = parseVarDeclarators(type.nextPos);
        checkTokenIs(varDeclarators.nextPos, new SemiColonToken());
        return new ParseResult<Decl>(new FieldDecl(type.result, varDeclarators.result), varDeclarators.nextPos + 1);
    }

    /**
     * attempts to parse a list of <var declarator>, ie <var declarator> | <var declarators> , <var declarator>
     * @param startPos position in the token list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseVarDeclarators(final int startPos) throws ParseException {
        final ParseResult<Decl> firstVarDecl = parseVarDeclarator(startPos);
        final ParseResult<List<Decl>> rest = parseVarDeclaratorsHelper(firstVarDecl.nextPos);

        VarDeclaratorList resultDecl = new VarDeclaratorList(firstVarDecl.result);
        for (final Decl otherDecl : rest.result) {
            resultDecl.varDeclList.add(otherDecl);
        }
        return new ParseResult<Decl>(resultDecl, rest.nextPos);
    }
    /**
     * helper to parse multiple <var declarator>
     * @param startPos position in the token list
     * @return ParseResult<List<Decl>>
     * @throws ParseException
     */
    private ParseResult<List<Decl>> parseVarDeclaratorsHelper(final int startPos) {
        final List<Decl> resultList = new ArrayList<>();
        int curPos = startPos;

        while (curPos < tokens.size()) {
            try {
                if (readToken(curPos) instanceof CommaToken)
                    curPos++;
                final ParseResult<Decl> curVarDec = parseVarDeclarator(curPos);
                curPos = curVarDec.nextPos;
                resultList.add(curVarDec.result);
            }
            catch (ParseException e) {
                break;
            }
        }
        return new ParseResult<List<Decl>>(resultList, curPos);
    }

    /**
     * attempts to parse a <var declarator>, ie <identifier> | <identifier> = <expr>
     * @param startPos position in the token list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseVarDeclarator(final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);
        if (currentToken instanceof IdentifierToken) {
            final ParseResult<Exp> identifier = parsePrimary(startPos);
            //<identifier> = <expr>
            if (readToken(identifier.nextPos) instanceof OperatorToken) {
                checkTokenIs(identifier.nextPos, new OperatorToken("="));
                final ParseResult<Exp> exp = parseExp(identifier.nextPos + 1);
                return new ParseResult<Decl>(new VarDeclarator(identifier.result, exp.result), exp.nextPos);
            }
            //<identifier>
            else return new ParseResult<Decl>(new VarDeclarator(identifier.result, null), identifier.nextPos);
        }
        else throw new ParseException("No VarDeclarator to parse");
    }
    /**
     * attempts to parse a formal parameter list
     * @param startPos position in the list
     * @return ParseResult<Decl> containing a list of formal parameters aka FormalParam
     * @throws ParseException
     */
    private ParseResult<FormalParamList> parseFormalParamList (final int startPos) throws ParseException{
        FormalParamList formList = new FormalParamList();
        ParseResult<ParserType> initialType = parseType(startPos);
        if(readToken(initialType.nextPos) instanceof IdentifierToken) {
            ParseResult<Exp> initialID = parsePrimary(initialType.nextPos);
            formList.declList.add(new FormalParam(initialType.result, initialID.result));
            int curPos = initialID.nextPos;
            while (curPos < tokens.size()) {
                try {
                    //case of separation between arguments
                    if (readToken(curPos) instanceof CommaToken)
                        curPos++;
                    final ParseResult<ParserType> curType = parseType(curPos);
                    if (readToken(curType.nextPos) instanceof IdentifierToken){
                        final ParseResult<Exp> curID = parsePrimary(curType.nextPos);
                        curPos = curID.nextPos;
                        formList.declList.add(new FormalParam(curType.result, curID.result));
                    } else throw new ParseException("Not an identifier.");
                } catch (final ParseException e) {
                    break;
                }
            }
            return new ParseResult<FormalParamList>(formList, curPos);
        } else {
            throw new ParseException("Not an identifier.");
        }
    }

    /**
     * attempts to parse a <method dec> , ie <method header> <method body>
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseMethodDecl(final int startPos) throws ParseException {
        final ParseResult<Decl> methodHeader = parseMethodHeader(startPos);
        final ParseResult<Stmt> methodBody = parseMethodBody(methodHeader.nextPos);
        return new ParseResult<Decl>(new MethodDecl(
                methodHeader.result,
                methodBody.result),
                methodBody.nextPos);
    }
    /**
     * attempts to parse a <method header> , ie <result typy> <method declarator>
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private ParseResult<Decl> parseMethodHeader(final int startPos) throws ParseException {
        ParseResult<ParserType> resultType;
        try{
            resultType = parseType(startPos);
        } catch (ParseException e) {
            checkTokenIs(startPos, new VoidToken());
            resultType = new ParseResult<ParserType>(new ParserVoid(), startPos + 1);
        }
        final ParseResult<Decl> methodDeclarator = parseMethodDeclarator(resultType.nextPos);
        return new ParseResult<Decl>(new MethodHeader(
                resultType.result,
                methodDeclarator.result),
                methodDeclarator.nextPos);
    }
    /**
     * attempts to parse a <method declarator> , ie <identifier> (<formal param list>?)
     * @param startPos position in the list
     * @return ParseResult<Decl>
     * @throws ParseException
     */
    private final ParseResult<Decl> parseMethodDeclarator(final int startPos) throws ParseException {
        final ParseResult<Exp> identifier = parseLiteral(startPos);
        checkTokenIs(identifier.nextPos, new LeftParenToken());
        final ParseResult<FormalParamList> paramList = parseFormalParamList(identifier.nextPos + 1);
        checkTokenIs(paramList.nextPos, new RightParenToken());
        return new ParseResult<Decl>(new MethodDeclarator(identifier.result, paramList.result), paramList.nextPos + 1);
    }

    /**
     * attempts to parse a method body, either <block> or ;
     * @param startPos position in the list
     * @return ParseResult<Stmt> containing the method body
     * @throws ParseException
     */
    private final ParseResult<Stmt> parseMethodBody(final int startPos) throws ParseException {
        final ParseResult<Stmt> body = parseStmt(startPos);
        return new ParseResult<Stmt>(body.result, body.nextPos);
    }

    /**
     * attempts to parse a block
     * @param startPos position in the list
     * @return ParseResult<Stmt> containing a Block-object with all statements parsed
     * @throws ParseException
     */
    private ParseResult<Stmt> parseBlock(final int startPos) throws ParseException {
        checkTokenIs(startPos, new LeftCurlyToken());
        final ParseResult<List<Stmt>> blockStmts = parseBlockStmtHelper(startPos + 1);
        checkTokenIs(blockStmts.nextPos, new RightCurlyToken());
        return new ParseResult<Stmt>(new Block(blockStmts.result), blockStmts.nextPos + 1);
    }

    /**
     * greedy method for parsing statements inside a block
     * @param startPos position in the list
     * @return ParseResult<List<Stmt>> containing all but the first Stmt in the block if any
     * @throws ParseException
     */
    private ParseResult<List<Stmt>> parseBlockStmtHelper(final int startPos) {
        final List<Stmt> resultList = new ArrayList<Stmt>();
        int curPos = startPos;

        while (curPos < tokens.size()) {
            try {
                final ParseResult<Stmt> curBlockStmt = parseBlockStmt(curPos);
                curPos = curBlockStmt.nextPos;
                resultList.add(curBlockStmt.result);
                if (readToken(curPos) instanceof RightCurlyToken)
                    break;
            } catch (ParseException e) {
                break;
            }
        }
        return new ParseResult<List<Stmt>>(resultList, curPos);
    }

    /**
     * attempts to parse a <block stmt>, ie <local vardec stmt> | <stmt>
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseBlockStmt(final int startPos) throws ParseException {

        //try to parse as a <local vardec stmt>, if it fails parse as <stmt>
        try {
            final ParseResult<Stmt> localVarDecStmt = parseLocalVardecStmt(startPos);
            return new ParseResult<Stmt>(localVarDecStmt.result, localVarDecStmt.nextPos);
        } catch (ParseException e) {
            final ParseResult<Stmt> stmt = parseStmt(startPos);
            return new ParseResult<Stmt>(stmt.result, stmt.nextPos);
        }

    }
    /**
     * attempts to parse a <local vardec stmt>, ie <local vardec> ;
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseLocalVardecStmt(final int startPos) throws ParseException {

        final ParseResult<Stmt> localVardec = parseLocalVardec(startPos);
        checkTokenIs(localVardec.nextPos, new SemiColonToken());
        return new ParseResult<Stmt>(localVardec.result, localVardec.nextPos + 1);
    }

    /**
     * attempts to parse a <local vardec>, ie <type> <vardeclarators>
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseLocalVardec(final int startPos) throws ParseException {
        final ParseResult<ParserType> type = parseType(startPos);
        final ParseResult<Decl> varDeclarators = parseVarDeclarators(type.nextPos);
        return new ParseResult<Stmt>(new LocalVardec(type.result, varDeclarators.result), varDeclarators.nextPos);
    }
    /**
     * attempts to parse a stmt, ie <stmt without trailing substmt> | <if then else stmt> |
     * <while stmt> | <for stmt>
     * @param startPos position on the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    public ParseResult<Stmt> parseStmt(final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);

        if (currentToken instanceof IfToken) {
            final ParseResult<Stmt> stmt = parseIfElseStmt(startPos);
            return new ParseResult<Stmt>(stmt.result, stmt.nextPos);
        }
        else if (currentToken instanceof WhileToken) {
            final ParseResult<Stmt> stmt = parseWhileStmt(startPos);
            return new ParseResult<Stmt>(stmt.result, stmt.nextPos);
        }
        else if (currentToken instanceof ForToken) {
            final ParseResult<Stmt> stmt = parseForStmt(startPos);
            return new ParseResult<Stmt>(stmt.result, stmt.nextPos);
        }
        else if (currentToken instanceof PrintlnToken) {
            final ParseResult<Stmt> printStmt = parsePrintStmt(startPos);
            return new ParseResult<Stmt>(printStmt.result, printStmt.nextPos);
        }
        //<stmt without trailing substmt>
        else {
            final ParseResult<Stmt> stmt = parseNoTrailingSubstmtStmt(startPos);
            return new ParseResult<Stmt>(stmt.result, stmt.nextPos);
        }
    }

    /**
     * attempts to parse a <stmt withut trailing substmt>
     * ie: <block> | <empty stmt> | <expr stmt> |
     *  <break stmt> | <return stmt>
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseNoTrailingSubstmtStmt(final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);

        //<block>
        if (currentToken instanceof LeftCurlyToken) {
            final ParseResult<Stmt> block = parseBlock(startPos);
            return new ParseResult<Stmt>(block.result, block.nextPos);
        }
        //<empty>
        else if (currentToken instanceof SemiColonToken) {
            return new ParseResult<Stmt>(new EmptyStmt(), startPos + 1);
        }
        // <return>
        else if (currentToken instanceof ReturnToken) {
            final ParseResult<Stmt> returnStmt = parseReturnStmt(startPos);
            return new ParseResult<Stmt>(returnStmt.result, returnStmt.nextPos);
        }
        //<break>
        else if (currentToken instanceof BreakToken) {
            final ParseResult<Stmt> breakStmt = parseBreakStmt(startPos);
            return new ParseResult<Stmt>(breakStmt.result, breakStmt.nextPos);
        }
        //<expr stmt>
        else {
            final ParseResult<Stmt> exprStmt = parseExprStmt(startPos);
            return new ParseResult<Stmt>(exprStmt.result, exprStmt.nextPos);
        }
    }

    /**
     * attempts to parse a printStmt, TODO currently only supports 1 exp to be printed
     * @param startPos position in the list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parsePrintStmt(final int startPos) throws ParseException {
        checkTokenIs(startPos, new PrintlnToken());
        checkTokenIs(startPos + 1, new LeftParenToken());
        final ParseResult<Exp> exp = parseExp(startPos + 2);
        checkTokenIs(exp.nextPos, new RightParenToken());
        return new ParseResult<Stmt>(new PrintlnStmt(exp.result), exp.nextPos + 1);
    }
    /**
     * attempts to parse an If Else statement
     * @param startPos position in the token list
     * @return  ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseIfElseStmt(final int startPos) throws ParseException {
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
     * attempts to parse a while-statement
     * @param startPos position in the list
     * @return ParseResult<Stmt> containing a WhileStmt
     * @throws ParseException
     */
    private ParseResult<Stmt> parseWhileStmt(final int startPos) throws ParseException {
        checkTokenIs(startPos, new WhileToken());
        checkTokenIs(startPos + 1, new LeftParenToken());
        final ParseResult<Exp> guard = parseExp(startPos + 2);
        checkTokenIs(guard.nextPos, new RightParenToken());
        final ParseResult<Stmt> body = parseBlock(guard.nextPos + 1);
        return new ParseResult<Stmt>(new WhileStmt(guard.result, body.result), body.nextPos);
    }
    /**
     * attempts to parse a ForStmt
     * @param startPos current position in the list
     * @return ParseResult<Stmt> with a ForStmt
     * @throws ParseException
     */

    private ParseResult<Stmt> parseForStmt(final int startPos) throws ParseException {
        checkTokenIs(startPos, new ForToken());
        checkTokenIs(startPos + 1, new LeftParenToken());
        final ParseResult<Stmt> forInit = parseForInit(startPos + 2);
        checkTokenIs(forInit.nextPos, new SemiColonToken());
        final ParseResult<Exp> conditional = parseExp(forInit.nextPos + 1);
        checkTokenIs(conditional.nextPos, new SemiColonToken());
        final ParseResult<Stmt> forUpdate = parseStmtExprList(conditional.nextPos + 1);
        checkTokenIs(forUpdate.nextPos, new RightParenToken());
        final ParseResult<Stmt> body = parseBlock(forUpdate.nextPos + 1);
        return new ParseResult<Stmt>(new ForStmt(
                forInit.result,
                conditional.result,
                forUpdate.result,
                body.result), body.nextPos);
    }

    /**
     * attempts to parse a <for init>, ie <stmt expr list> | <local vardec>
     * @param startPos current position in the list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseForInit(final int startPos) throws ParseException {

        //try to parse as <local vardec>, if fails parse as <stmt expr list>
        try {
            final ParseResult<Stmt> localVarDec = parseLocalVardec(startPos);
            return new ParseResult<Stmt>(localVarDec.result, localVarDec.nextPos);

        } catch (ParseException e) {
            final ParseResult<Stmt> expStmtList = parseStmtExprList(startPos);
            return new ParseResult<Stmt>(expStmtList.result, expStmtList.nextPos);
        }
    }

    /**
     * attempts to parse an <expr stmt>, ie <stmt expr> ;
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseExprStmt(final int startPos) throws ParseException {
        final ParseResult<Stmt> stmtExpr = parseStmtExpr(startPos);
        checkTokenIs(stmtExpr.nextPos, new SemiColonToken());
        return new ParseResult<Stmt>(new ExprStmt(stmtExpr.result), stmtExpr.nextPos + 1);
    }
    /**
     * attempts to parse a <stmt expr>, ie <assignment> | <preincrement expr> | <predecrement expr> |
     *<postdecrement expr> | <postincrement expr> | <method invocation> | <class instance creation expr>
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseStmtExpr (final int startPos) throws ParseException {
        final ParseResult<Exp> stmtExp = parseExp(startPos);
        return new ParseResult<Stmt>(new StmtExpr(stmtExp.result), stmtExp.nextPos);
    }

    /**
     * attempts to parse a list of ExpStmt
     * @param startPos position in the token list
     * @return ParseResult<Stmt> containing an ExpStmt
     * @throws ParseException
     */
    private ParseResult<Stmt> parseStmtExprList(final int startPos) throws ParseException {

        StmtExprList stmtExprList = new StmtExprList();
        int curPos = startPos;

        while (curPos < tokens.size()) {
            try {
                if (readToken(curPos) instanceof CommaToken)
                    curPos++;
                final ParseResult<Stmt> curExpStmt = parseStmtExpr(curPos);
                curPos = curExpStmt.nextPos;
                stmtExprList.list.add(curExpStmt.result);
            } catch (final ParseException e) {
                break;
            }
        }
        return new ParseResult<Stmt>(stmtExprList,curPos);
    }

    /**
     * attempts to parse a break statement
     * @param startPos position in the token list
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseBreakStmt(final int startPos) throws ParseException{
        checkTokenIs(startPos, new BreakToken());
        if (readToken(startPos + 1) instanceof IdentifierToken){
            ParseResult<Exp> identifier = parsePrimary(startPos + 1);
            checkTokenIs(identifier.nextPos, new SemiColonToken());
            return new ParseResult<Stmt>(new BreakStmt(identifier.result),
                    identifier.nextPos + 1);
        } else {
            checkTokenIs(startPos + 1, new SemiColonToken());
            return new ParseResult<Stmt>(new BreakStmt(null),
                    startPos + 2);
        }
    }

    /**
     * attempts to parse a return statement
     * @param startPos position in the token array
     * @return ParseResult<Stmt>
     * @throws ParseException
     */
    private ParseResult<Stmt> parseReturnStmt(final int startPos) throws ParseException{
        checkTokenIs(startPos, new ReturnToken());
        if (readToken(startPos + 1) instanceof SemiColonToken){
            return new ParseResult<Stmt>(new ReturnStmt(null), startPos + 2);
        }
        else {
            ParseResult<Exp> returnExp = parseExp(startPos + 1);
            checkTokenIs(returnExp.nextPos, new SemiColonToken());
            return new ParseResult<Stmt>(new ReturnStmt(returnExp.result), returnExp.nextPos + 1);
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
    private ParseResult<Exp> parseAssignmentExpr (final int startPos) throws ParseException{
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

    private ParseResult<List<Pair<String, Exp>>> parseAssignmentExpHelper(int curPos, final Token... operators) {
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
    private ParseResult<Exp> parseBinaryOperatorExp(final int startPos) throws ParseException {

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
    private ParseResult<Exp> parseUnaryExp(final int startPos) throws ParseException {
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
            else if (OpToken.name.equals("!")) {
                final ParseResult<Exp> negateExp = parseNoIncDecUnaryExp(startPos);
                return new ParseResult<Exp>(negateExp.result, negateExp.nextPos);
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
    private ParseResult<Exp> parseNoIncDecUnaryExp(final int startPos) throws ParseException {
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
    private ParseResult<Exp> parseCastExp(final int startPos) throws ParseException {
        checkTokenIs(startPos, new LeftParenToken());
        ParseResult<ParserType> castType = parseType(startPos + 1);
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
    public ParseResult<ParserType> parseType(final int startPos) throws ParseException {
        final Token currentToken = readToken(startPos);
        //boolean
        if (currentToken instanceof BooleanTypeToken) {
            return new ParseResult<ParserType>(new PrimitiveParserType(new BooleanParserType()), startPos + 1);
        }
        //int
        else if (currentToken instanceof IntTypeToken) {
            return new ParseResult<ParserType>(new PrimitiveParserType(new IntParserType()), startPos + 1);
        }
        //<primitive type>:str
        else if (currentToken instanceof StringTypeToken) {
            return new ParseResult<ParserType>(new PrimitiveParserType(new StringParserType()), startPos + 1);
        }
        //<class type>: <identifier>
        else if (currentToken instanceof IdentifierToken){
            IdentifierToken identifierToken = (IdentifierToken) currentToken;
            return new ParseResult<ParserType>(new ClassParserType(new IdentifierLiteral(identifierToken.name)), startPos + 1);
        }
        else
            throw new ParseException("Not a valid type to parse: " + currentToken);
    }


    /**
     * attempts to parse a postfix exp, ie <primary>, <postincr exp>, <postdecr exp>
     * @param startPos current position in the list
     * @return ParseResult<Exp>
     * @throws ParseException
     */
    private ParseResult<Exp> parsePostfixExp(final int startPos) throws ParseException {
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
    private ParseResult<Exp> parsePreIncrExpr(final int startPos) throws ParseException {
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
    private ParseResult<Exp> parsePreDecrExpr(final int startPos) throws ParseException {
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
    private ParseResult<Exp> parsePostIncrExpr(final int startPos) throws ParseException {
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
    private ParseResult<Exp> parsePostDecrExpr(final int startPos) throws ParseException {
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
    private ParseResult<Exp> parsePrimary(final int startPos) throws ParseException {
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
    private ParseResult<List<Exp>> parseFieldAccessExp(final int startPos) {
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
    private ParseResult<ArgumentList> parseArgumentList (final int startPos) {
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

    public Exp parseTopLevelExp() throws ParseException {
        final ParseResult<Exp> toplevel = parseExp(0);
        if (toplevel.nextPos == tokens.size()) {
            return toplevel.result;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }
    public Stmt parseTopLevelStmt() throws ParseException {
        final ParseResult<Stmt> toplevel = parseStmt(0);
        if (toplevel.nextPos == tokens.size()) {
            return toplevel.result;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }
    public Decl parseTopLevelClass() throws ParseException {
        final ParseResult<Decl> toplevel = parseClassDecl(0);
        if (toplevel.nextPos == tokens.size()) {
            return toplevel.result;
        } else {
            throw new ParseException("tokens remaining at end");
        }
    }

}

