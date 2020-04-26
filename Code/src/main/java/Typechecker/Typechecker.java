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
import com.google.common.collect.ImmutableMap;

public class Typechecker {

    public final List<Decl> program;
    public final Map<String, ClassDecl> classes;
    public final Map<String, ClassDecl> subClasses;
    public final TypeEnvironment env;

    /**
     * constructor to initialize a typechecker
     * initializes a Map: ClassName -> ClassDecl to keep track of all classes
     * @param program a list of class declarations
     * @throws IllTypedException
     */
    public Typechecker(final List<Decl> program) throws IllTypedException {
        this.program = program;
        this.env = new TypeEnvironment(null, null, null);
        classes = new HashMap<String, ClassDecl>();
        subClasses = new HashMap<String, ClassDecl>();

        for (final Decl curClass : program) {
            final ClassDecl curClassDecl = (ClassDecl) curClass;
            final IdentifierLiteral className = (IdentifierLiteral)curClassDecl.identifier;

            if (classes.containsKey(className.name)) {
                throw new IllTypedException("Duplicate class name: " + className.name);
            }
            classes.put(className.name, curClassDecl);
        }
    }

    /**
     * private empty constructor, used for unit testing purposes
     */
    private Typechecker() {
        this.env = null;
        this.subClasses = null;
        this.program = null;
        this.classes = new HashMap<String, ClassDecl>();

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

    /**
     * creates an empty gamma
     * @return empty immutable map
     */
    private static ImmutableMap<String, Type> createEmptyGamma() {
        final Map<String, Type> mutableGamma = new HashMap<>();
        final ImmutableMap<String, Type> gamma = ImmutableMap.copyOf(mutableGamma);
        return gamma;
    }
    /**
     * creates a copy of an ImmutableMap and adds new mappings
     * @param gamma current mapping
     * @param pairs new key-value pairs to add to map
     * @return copy of gamma with new mappings
     */
    private static ImmutableMap<String, Type> addToGamma(
            final ImmutableMap<String, Type> gamma, final Pair<String, Type>... pairs) {

        Map<String, Type> newMappings = new HashMap<>();
        for (final Pair curPair : pairs) {
            newMappings.put((String)curPair.first, (Type)curPair.second);
        }

        final ImmutableMap<String, Type> newGamma = ImmutableMap.<String, Type>builder()
                .putAll(gamma)
                .putAll(newMappings)
                .build();

        return newGamma;
    }

    public void typecheckProgram() throws IllTypedException {
        for (final Decl classDecl : program) {
            typecheckClass(createEmptyGamma(), (ClassDecl)classDecl);
        }
    }
    public void typecheckClass(final ImmutableMap<String, Type> gamma, final ClassDecl classDecl) throws IllTypedException {

        if (classDecl.extendsClass == null)
            typecheckDecl(createEmptyGamma(), classDecl.classBody);

        else {
            final ClassParserType type = (ClassParserType) classDecl.extendsClass;
            final ClassDecl superClass = getClass(((IdentifierLiteral)type.className).name);
            final Map<String, Type> superClassMapping = new HashMap<String, Type>(typecheckDecl(gamma, superClass.classBody));
            final ImmutableMap<String, Type> newGamma =
                    ImmutableMap.<String, Type>builder()
                    .putAll(gamma)
                    .putAll(superClassMapping)
                    .build();
            typecheckDecl(newGamma, classDecl.classBody);
        }
    }

    public ImmutableMap<String, Type> typecheckDecl(final ImmutableMap<String, Type> gamma, final Decl d) throws IllTypedException {

         if (d instanceof ClassBodyDecs) {
            final ClassBodyDecs classBodyDecs = (ClassBodyDecs)d;
            for (final Decl classBody: classBodyDecs.classBodyDecs) {
                typecheckDecl(gamma, classBody);
            }
            return gamma;
        }

        else if (d instanceof ConstructorDecl) {
            final ConstructorDecl constructorDecl = (ConstructorDecl)d;
            final ConstructorDeclarator constructorDeclarator = (ConstructorDeclarator) constructorDecl.constructorDeclarator;
            final IdentifierLiteral identifier = (IdentifierLiteral)constructorDeclarator.identifier;

            if (classes.containsKey(identifier.name)) {
                typecheckDecl(gamma, constructorDecl.constructorBody);
                return gamma;
            }

            else {
                throw new IllTypedException("Constructor naming mismatch for class: " + identifier.name);
            }
        }

        else if (d instanceof FieldDecl) {
            final FieldDecl fieldDecl = (FieldDecl)d;
            final Type declaredType = convertParserType(fieldDecl.parserType);

            //need a mutable map here to ensure that all new variables are added to the same gamma
            Map<String, Type> newGamma = new HashMap<>(gamma);

            final VarDeclaratorList varList = (VarDeclaratorList) fieldDecl.varDeclarators;
            for (final Decl decl : varList.varDeclList) {
                final VarDeclarator varDec = (VarDeclarator)decl;
                newGamma.put(((IdentifierLiteral)varDec.identifier).name, declaredType);

                if (!(varDec.exp == null)){
                    typeof(gamma, varDec.exp);
                }
            }
            return ImmutableMap.copyOf(newGamma);
         }

        //TODO MOVE
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

    public Type typeOfField(final String onClass, final String fieldName) throws IllTypedException {
        if (onClass == null) {
            throw new IllTypedException("No instance variable defined: " + fieldName);
        }
        else {
            final ClassDecl classDecl = getClass(onClass);
            final ClassBodyDecs bodyDecs = (ClassBodyDecs)classDecl.classBody;

            for (final Decl bodyDecl : bodyDecs.classBodyDecs) {
                
            }
        }

        return null;
    }

    public ImmutableMap<String, Type> typecheckStmts(ImmutableMap<String, Type> gamma,  final boolean breakOk,
                                                     final Stmt s) throws IllTypedException {
        if (s instanceof BlockStmt) {
            final BlockStmt asBlock = (BlockStmt)s;
            gamma = typecheckStmts(gamma, breakOk, asBlock.left);
            gamma = typecheckStmts(gamma, breakOk, asBlock.right);
        }
        else
            gamma = typecheckStmt(gamma, breakOk, s);
        return gamma;
    }

    /**
     * attempts to typecheck a statement
     * @param gamma map of bound variables
     * @param breakOk bool to allow break stmt
     * @param s current statement
     * @return new gamma map of bound variables
     * @throws IllTypedException unrecognized expression
     */
    public ImmutableMap<String, Type> typecheckStmt(final ImmutableMap<String, Type> gamma, final boolean breakOk,
                                                     final Stmt s) throws IllTypedException {
        if (s instanceof BreakStmt) {
            if (breakOk) {
                return gamma;
            } else {
                throw new IllTypedException("break outside of a loop");
            }
        }
        else if (s instanceof ForStmt) {
            final ForStmt asFor = (ForStmt)s;
            final ImmutableMap<String, Type> newGamma = typecheckStmt(gamma, breakOk,asFor.forInit);
            final Type guardType = typeof(newGamma, asFor.conditional);
            if (guardType instanceof BoolType) {
                typecheckStmt(newGamma, breakOk, asFor.forUpdate);
                // have to deal with body being a Stmt and not a List<Stmt>
                typecheckStmts(newGamma, true, asFor.body);
            } else {
                throw new IllTypedException("Guard in for stmt must be boolean");
            }
            return gamma;
        }
        else if (s instanceof WhileStmt) {
            final WhileStmt asWhile = (WhileStmt)s;
            final Type guardType = typeof(gamma, asWhile.guard);
            if (guardType instanceof BoolType) {
                typecheckStmts(gamma, breakOk, asWhile.body);
            } else {
                throw new IllTypedException("Guard in while stmt must be boolean");
            }
            return gamma;
        }
        else if (s instanceof IfElseStmt) {
            final IfElseStmt asIf = (IfElseStmt)s;
            final Type guardType = typeof(gamma, asIf.guard);
            if (guardType instanceof BoolType) {
                typecheckStmts(gamma, breakOk, asIf.trueBranch);
                typecheckStmts(gamma, breakOk, asIf.falseBranch);
            } else {
                throw new IllTypedException("Guard in ifelse stmt must be boolean");
            }
            return gamma;
        }
        else if (s instanceof ReturnStmt || s instanceof EmptyStmt || s instanceof PrintlnStmt) {
            return gamma;
        }
        else {
            assert(false);
            throw new IllTypedException("Unrecognized statement");
        }
    }

    /**
     * attempts to typecheck an expression
     * @param gamma map of bound variables
     * @param e current expression
     * @return type of e
     * @throws IllTypedException unrecognized expression
     */
    public Type typeof(final ImmutableMap<String, Type> gamma, final Exp e) throws IllTypedException{

        if (e instanceof BinaryOperatorExp) {
            final BinaryOperatorExp asBOP = (BinaryOperatorExp)e;
            final Type left = typeof(gamma, asBOP.left);
            final Type right = typeof(gamma, asBOP.right);

            //assignment
            if (asBOP.op.equals("=") ||
                    asBOP.op.equals("-=") ||
                    asBOP.op.equals("+=")) {

                final IdentifierLiteral asID = (IdentifierLiteral)asBOP.left;

                if (!gamma.containsKey(asID.name))
                    throw new IllTypedException("Variable not in scope: " + asID.name);

                if (left.equals(right)) {
                    return left;
                }
                else throw new IllTypedException("Type mismatch: type " + right +
                        "cannot be assigned to type " + left);
            }
            else if (asBOP.op.equals("+") || asBOP.op.equals("-") ||
                    asBOP.op.equals("*") || asBOP.op.equals("/")) {
                if (left instanceof IntType && right instanceof IntType) {
                    return new IntType();
                }
                else {
                    throw new IllTypedException("Operator " + asBOP.op +
                            " cannot be applied to " + left +", " + right);
                }
            }
            else if (asBOP.op.equals("<") || asBOP.op.equals(">")){
                if (left instanceof IntType && right instanceof IntType) {
                    return new BoolType();
                } else {
                    throw new IllTypedException("Operator " + asBOP.op +
                            " cannot be applied to " + left +", " + right);
                }
            }
            else if (asBOP.op.equals("!=") || asBOP.op.equals("==")) {
                if (left instanceof IntType && right instanceof IntType) {
                    return new BoolType();
                }
                if (left instanceof BoolType && right instanceof BoolType) {
                    return new BoolType();
                } else {
                    throw new IllTypedException("Operator " + asBOP.op +
                            " cannot be applied to " + left +", " + right);
                }
            }
            else {
                assert(false);
                throw new IllTypedException("Illegal binary operation");
            }
        }

        else if (e instanceof PreIncrDecrExp) {
            final PreIncrDecrExp asPre = (PreIncrDecrExp)e;
            final Type expType = typeof(gamma, asPre.prefixExp);
            if (expType instanceof IntType)
                return expType;
            else
                throw new IllTypedException("Cannot apply ++/-- on type " + expType);
        }

        else if (e instanceof PostIncrDecrExp) {
            final PostIncrDecrExp asPost = (PostIncrDecrExp) e;
            final Type expType = typeof(gamma, asPost.postfixExp);
            if (expType instanceof IntType)
                return expType;
            else
                throw new IllTypedException("Cannot apply ++/-- on type " + expType);
        }
        else if (e instanceof NegateUnaryExp) {
            final NegateUnaryExp asNeg = (NegateUnaryExp) e;
            final Type expType = typeof(gamma, asNeg.exp);
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
            if (gamma.containsKey(asID.name)) {
                return gamma.get(asID.name);
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
            final Typechecker typechecker = new Typechecker(parsed);
            typechecker.typecheckProgram();

            System.out.println(parsed);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
