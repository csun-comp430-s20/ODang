package Typechecker;

import Parser.Declarations.*;
import Parser.Expressions.*;
import Parser.Declarations.Decl;
import Parser.Parser;
import Parser.Statements.*;
import Parser.Literals.*;
import Parser.Types.*;

import Parser.Types.Void;
import Tokenizer.Tokenizer;
import Typechecker.Types.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import Typechecker.Types.IntType;

public class Typechecker {

    private final Map<FunctionName, FunctionDefinition> functionDefinitions;
    public final List<FunctionDefinition> programFunctions;
    public final List<Decl> program;
    public final Map<String, ClassDecl> classes;
    public final TypeEnvironment env;


    /**
     * constructor to initialize a typechecker
     * initializes a Map: ClassName -> ClassDecl to keep track of all classes
     * @param program a list of class declarations
     * @throws IllTypedException
     */
    public Typechecker(final List<Decl> program, final List<FunctionDefinition> programFunctions) throws IllTypedException {
        this.program = program;
        this.env = new TypeEnvironment(null, null, null);
        classes = new HashMap<String, ClassDecl>();

        for (final Decl curClass : program) {
            final ClassDecl curClassDecl = (ClassDecl) curClass;
            final IdentifierLiteral className = (IdentifierLiteral)curClassDecl.identifier;

            if (classes.containsKey(className.name)) {
                throw new IllTypedException("Duplicate class name: " + className.name);
            }
            classes.put(className.name, curClassDecl);
        }

        functionDefinitions = new HashMap<FunctionName, FunctionDefinition>();
        this.programFunctions = programFunctions;
        for (final FunctionDefinition function: programFunctions) {
            if (!functionDefinitions.containsKey(function.name)) {
                functionDefinitions.put(function.name, function);
            } else {
                throw new IllTypedException("Duplicate function name: " + function.name);
            }
        }

    }

    /**
     * private empty constructor, used for unit testing purposes
     */
    private Typechecker() {
        this.env = null;
        this.program = null;
        this.classes = new HashMap<String, ClassDecl>();
        this.programFunctions = null;
        this.functionDefinitions = null;
    }

    public static class Pair<U, V> {
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
     * converts a Parser.Types.Type to a Typechecker.Type
     * @param type a Parser.Types.Type
     * @return a Typechecker.Type
     * @throws IllTypedException if invalid type to convert
     */
    public static Type convertParserType(ParserType type) throws IllTypedException {
        if (type instanceof ClassParserType) {
            final ClassParserType classType = (ClassParserType) type;
            final IdentifierLiteral className = (IdentifierLiteral)classType.className ;
            return new ClassType(className.name);
        }
        else if (type instanceof PrimitiveParserType) {
            final PrimitiveParserType prType = (PrimitiveParserType)type;

            if (prType.parserType instanceof BooleanParserType)
                return new BoolType();
            else if (prType.parserType instanceof IntParserType)
                return new IntType();
            else if (prType.parserType instanceof StringParserType)
                return new StringType();
            else
                throw new IllTypedException("Not a valid primitive type to convert: " + type);
        }
        else if (type instanceof Void)
            return new VoidType();

        else throw new IllTypedException("Not a valid Parser.Type to convert: " + type);
    }

    public ClassDecl getClass(final String className) throws IllTypedException {
        final ClassDecl result = classes.get(className);
        if (result == null)
            throw new IllTypedException("No such class defined: " + className);
        else return result;
    }

    public static TypeEnvironment createEmptyTypeEnvironment() {
        return new TypeEnvironment(null, null, null);
    }

    public void typecheckProgram() throws IllTypedException {
        for (final Decl classDecl : program) {
            typecheckClass(createEmptyTypeEnvironment(), (ClassDecl)classDecl);
        }

        for (final FunctionDefinition function: programFunctions) {
            typecheckFunction(createEmptyTypeEnvironment(), function);
        }
    }

    /**
     * typechecks a class. will return a env with all defined variables and declarations in the class
     * for typecheckProgram() this will not be used, but is neccessary for typechecking subclasses
     * @param env TypeEnvironment keeping track of variables and functions in the class
     * @param classDecl class that is being typechecked
     * @return TypeEnvironment containing all mappings of functions and variables
     * @throws IllTypedException
     */
    public TypeEnvironment typecheckClass(final TypeEnvironment env, final ClassDecl classDecl) throws IllTypedException {

        final IdentifierLiteral classIdentifier = (IdentifierLiteral) classDecl.identifier;
        TypeEnvironment newEnv = new TypeEnvironment(env.getFunctions(), env.getVariables(), classIdentifier.name);
        if (classDecl.extendsClass == null)
            newEnv = typecheckDecl(newEnv, classDecl.classBody);

        //if classDecl has a superclass, add methods and variables from superclass to env
        else {
            final ClassParserType type = (ClassParserType) classDecl.extendsClass;
            final ClassDecl superClass = getClass(((IdentifierLiteral)type.className).name);
            final TypeEnvironment superClassEnv = typecheckClass(new TypeEnvironment(
                    null, null, ((IdentifierLiteral)superClass.identifier).name), superClass);

            newEnv = typecheckClass(superClassEnv, classDecl);
        }

        return newEnv;
    }

    public TypeEnvironment typecheckDecl(TypeEnvironment env, final Decl d) throws IllTypedException {

         if (d instanceof ClassBodyDecs) {
            final ClassBodyDecs classBodyDecs = (ClassBodyDecs)d;
            for (final Decl classBody: classBodyDecs.classBodyDecs) {
                env = typecheckDecl(env, classBody);
            }
            return env;
        }

        else if (d instanceof ConstructorDecl) {
            final ConstructorDecl constructorDecl = (ConstructorDecl)d;
            final ConstructorDeclarator constructorDeclarator = (ConstructorDeclarator) constructorDecl.constructorDeclarator;
            final IdentifierLiteral identifier = (IdentifierLiteral)constructorDeclarator.identifier;

            if (classes.containsKey(identifier.name)) {
                return typecheckDecl(env, constructorDecl.constructorBody);
            }

            else {
                throw new IllTypedException("Constructor naming mismatch for class: " + identifier.name);
            }
        }

        else if (d instanceof FieldDecl) {
            TypeEnvironment newEnv = env;
            final FieldDecl fieldDecl = (FieldDecl)d;
            final Type declaredType = convertParserType(fieldDecl.parserType);
            final VarDeclaratorList varList = (VarDeclaratorList) fieldDecl.varDeclarators;
            for (final Decl decl : varList.varDeclList) {
                final VarDeclarator varDec = (VarDeclarator)decl;
                final String identifierName = ((IdentifierLiteral)varDec.identifier).name;
                if (!(varDec.exp == null)){
                    newEnv = env.addVariable(identifierName, declaredType);
                } else {
                    final Type actualType = typeof(env, varDec.exp);
                    if (actualType.equals(declaredType))
                        newEnv = env.addVariable(identifierName, declaredType);
                    else
                        throw new IllTypedException("Expression of type: " + actualType + 
                                "cannot be assigned to a variable with type: " + declaredType);
                }
            }
            return newEnv;
         }

        else if (d instanceof MethodDecl) {
            final MethodDecl methodDecl = (MethodDecl)d;
            final MethodHeader methodHeader = (MethodHeader)methodDecl.header;
            final Type resultType = convertParserType(methodHeader.resultParserType);


            return null;
         }

        else {
            assert(false);
            throw new IllTypedException("Unrecognized declaration: " + d.toString());
        }
    }
    

    /**
     * attempts to typecheck a function
     * @param function the function definition to typecheck
     * @return void
     * @throws IllTypedException unrecognized expression
     */
    public void typecheckFunction(TypeEnvironment env, final FunctionDefinition function) throws IllTypedException {
        for (final FormalParameter formalParam: function.formalParams) {
            if (!env.containsVariable(formalParam.theVariable)) {
                env = env.addVariable(formalParam.theVariable, formalParam.theType);
            } else {
                throw new IllTypedException("Duplicate formal parameter name");
            }
        }
        final TypeEnvironment finalEnv = typecheckStmts(env, false, function.body);
        final Type actualReturnType = typeof(finalEnv, function.returnExp);
        if (!actualReturnType.equals(function.returnType)) {
            throw new IllTypedException("return type mismatch");
        }
    }

    /**
     * attempts to typecheck multiple statements
     * @param env map of bound variables
     * @param breakOk bool to allow break stmt
     * @param s current statement that holds multiple statements
     * @return new gamma map of bound variables
     * @throws IllTypedException unrecognized expression
     */
    public TypeEnvironment typecheckStmts(TypeEnvironment env,  final boolean breakOk,
                                                     final Stmt s) throws IllTypedException {
        if (s instanceof BlockStmt) {
            final BlockStmt asBlock = (BlockStmt)s;

            for (final Stmt stmt : asBlock.block)
                env = typecheckStmts(env, breakOk, stmt);

        }
        else
            env = typecheckStmt(env, breakOk, s);
        return env;
    }

    /**
     * attempts to typecheck a statement
     * @param env TypeEnvironment of bound variables
     * @param breakOk bool to allow break stmt
     * @param s current statement
     * @return new gamma map of bound variables
     * @throws IllTypedException unrecognized expression
     */
    public TypeEnvironment typecheckStmt(final TypeEnvironment env, final boolean breakOk,
                                                     final Stmt s) throws IllTypedException {
        if (s instanceof BreakStmt) {
            if (breakOk) {
                return env;
            } else {
                throw new IllTypedException("break outside of a loop");
            }
        }
        else if (s instanceof ForStmt) {
            final ForStmt asFor = (ForStmt)s;
            final TypeEnvironment newEnv = typecheckStmt(env, breakOk,asFor.forInit);
            final Type guardType = typeof(newEnv, asFor.conditional);
            if (guardType instanceof BoolType) {
                typecheckStmt(newEnv, breakOk, asFor.forUpdate);
                // have to deal with body being a Stmt and not a List<Stmt>
                typecheckStmts(newEnv, true, asFor.body);
            } else {
                throw new IllTypedException("Guard in for stmt must be boolean");
            }
            return env;
        }
        else if (s instanceof WhileStmt) {
            final WhileStmt asWhile = (WhileStmt)s;
            final Type guardType = typeof(env, asWhile.guard);
            if (guardType instanceof BoolType) {
                typecheckStmts(env, breakOk, asWhile.body);
            } else {
                throw new IllTypedException("Guard in while stmt must be boolean");
            }
            return env;
        }
        else if (s instanceof IfElseStmt) {
            final IfElseStmt asIf = (IfElseStmt)s;
            final Type guardType = typeof(env, asIf.guard);
            if (guardType instanceof BoolType) {
                typecheckStmts(env, breakOk, asIf.trueBranch);
                typecheckStmts(env, breakOk, asIf.falseBranch);
            } else {
                throw new IllTypedException("Guard in ifelse stmt must be boolean");
            }
            return env;
        }
        else if (s instanceof ReturnStmt || s instanceof EmptyStmt || s instanceof PrintlnStmt) {
            return env;
        }
        else {
            assert(false);
            throw new IllTypedException("Unrecognized statement");
        }
    }

    /**
     * attempts to typecheck an expression
     * @param env TypeEnvironment with bound variables
     * @param e current expression
     * @return type of e
     * @throws IllTypedException unrecognized expression
     */
    public Type typeof(final TypeEnvironment env, final Exp e) throws IllTypedException{

        if (e instanceof BinaryOperatorExp) {
            final BinaryOperatorExp asBOP = (BinaryOperatorExp)e;
            final Type left = typeof(env, asBOP.left);
            final Type right = typeof(env, asBOP.right);

            //assignment
            switch(asBOP.op)
            {
                case "=":
                case "-=":
                case "+=":
                    final IdentifierLiteral asID = (IdentifierLiteral) asBOP.left;
            
                    if(!env.containsVariable(asID.name))
                        throw new IllTypedException("Variable not in scope: " + asID.name);
            
                    if(left.equals(right))
                    {
                        return left;
                    }
                    else
                        throw new IllTypedException("Type mismatch: type " + right + "cannot be assigned to type " + left);
                case "+":
                case "-":
                case "*":
                case "/":
                    if(left instanceof IntType && right instanceof IntType)
                    {
                        return new IntType();
                    }
                    else
                    {
                        throw new IllTypedException("Operator " + asBOP.op + " cannot be applied to " + left + ", " + right);
                    }
                case "<":
                case ">":
                    if(left instanceof IntType && right instanceof IntType)
                    {
                        return new BoolType();
                    }
                    else
                    {
                        throw new IllTypedException("Operator " + asBOP.op + " cannot be applied to " + left + ", " + right);
                    }
                case "!=":
                case "==":
                    if(left instanceof IntType && right instanceof IntType)
                    {
                        return new BoolType();
                    }
                    if(left instanceof BoolType && right instanceof BoolType)
                    {
                        return new BoolType();
                    }
                    else
                    {
                        throw new IllTypedException("Operator " + asBOP.op + " cannot be applied to " + left + ", " + right);
                    }
                default:
                    assert (false);
                    throw new IllTypedException("Illegal binary operation");
            }
        }

        else if (e instanceof PreIncrDecrExp) {
            final PreIncrDecrExp asPre = (PreIncrDecrExp)e;
            final Type expType = typeof(env, asPre.prefixExp);
            if (expType instanceof IntType)
                return expType;
            else
                throw new IllTypedException("Cannot apply ++/-- on type " + expType);
        }
        else if (e instanceof PostIncrDecrExp) {
            final PostIncrDecrExp asPost = (PostIncrDecrExp) e;
            final Type expType = typeof(env, asPost.postfixExp);
            if (expType instanceof IntType)
                return expType;
            else
                throw new IllTypedException("Cannot apply ++/-- on type " + expType);
        }
        else if (e instanceof NegateUnaryExp) {
            final NegateUnaryExp asNeg = (NegateUnaryExp) e;
            final Type expType = typeof(env, asNeg.exp);
            if (expType instanceof BoolType)
                return expType;
            else
                throw new IllTypedException("Cannot negate a non-boolean type " + expType);
        }
        else if (e instanceof IntegerLiteral) {
            return new IntType();
        }
        else if (e instanceof BooleanLiteral) {
            return new BoolType();
        }
        else if (e instanceof StringLiteral) {
            return new StringType();
        }
        else if (e instanceof NullLiteral) {
            return new NullType();
        }
        //have to check if the variable is in scope
        else if (e instanceof IdentifierLiteral) {
            IdentifierLiteral asID = (IdentifierLiteral)e;
            if (env.containsVariable(asID.name)) {
                return env.lookupVariable(asID.name);
            } else {
                throw new IllTypedException("Variable not in scope: " + asID.name);
            }
        }
        else {
            assert(false);
            throw new IllTypedException("unrecognized expression: " + e.toString());
        }
    }
    public static void main(String[] args) {

        Map<String, Type> test = new HashMap<>();
        test.put("foo", new BoolType());

        try {
            File file = new File("testProgram.odang");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String tokenizerInput = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                tokenizerInput += line;
            }
            br.close();
            final Tokenizer tokenizer = new Tokenizer(tokenizerInput);
            final Parser parser = new Parser(tokenizer.tokenize());
            final List<Decl> parsed = parser.parseProgram();
            final Typechecker typechecker = new Typechecker(parsed, null);
            typechecker.typecheckProgram();

            System.out.println(parsed);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
