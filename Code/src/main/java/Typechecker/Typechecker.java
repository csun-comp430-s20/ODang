package Typechecker;

import Parser.Declarations.*;
import Parser.Expressions.*;
import Parser.Declarations.Decl;
import Parser.Parser;
import Parser.Statements.*;
import Parser.Literals.*;
import Tokenizer.Tokenizer;
import Typechecker.Types.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

public class Typechecker {

    public final ClassDecs program;
    public final Map<String, ClassDecl> classes;

    /**
     * constructor to initialize a typechecker
     * initializes a Map: ClassName -> ClassDecl to keep track of all classes
     * @param program a list of class declarations
     * @throws IllTypedException
     */
    public Typechecker(final ClassDecs program) throws IllTypedException {
        this.program = program;
        classes = new HashMap<String, ClassDecl>();
        for (final Decl curClass : program.classDecs) {
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
        this.program = null;
        this.classes = new HashMap<String, ClassDecl>();

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

    public ImmutableMap<String, Type> typecheckDecl(final ImmutableMap<String, Type> gamma,
                                                    final Decl d) throws IllTypedException {

        if (d instanceof ClassDecl) {
            if (d instanceof SubClassDecl) {
                final SubClassDecl subClass = (SubClassDecl)d;
                typecheckDecl(gamma, subClass.classBody);
            }
            //not a subclass, no need to keep track of super
            else {
                final ClassDecl classDecl = (ClassDecl)d;
                typecheckDecl(gamma, classDecl.classBody);
            }
            return gamma;
        }

        else if (d instanceof ClassBodyDecs) {
            final ClassBodyDecs classBodyDecs = (ClassBodyDecs)d;
            for (final Decl classBody: classBodyDecs.classBodyDecs) {
                typecheckDecl(gamma, classBody);
            }
            return gamma;
        }

        else if (d instanceof ConstructorDecl) {
            final ConstructorDecl constructorDecl = (ConstructorDecl)d;
            final ConstructorDeclarator constructorDeclarator = (ConstructorDeclarator) constructorDecl.constructorDeclarator;
            final String identifier = constructorDeclarator.identifier.getClass().getSimpleName();

            if (classes.containsKey(identifier)) {
                typecheckDecl(gamma, constructorDecl.constructorBody);
                return gamma;
            }
            else {
                throw new IllTypedException("Constructor naming mismatch for class: " + identifier);
            }
        }

        else {
            assert(false);
            throw new IllTypedException("Unrecognized declaration: " + d.toString());
        }
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
                typecheckStmt(newGamma, true, asFor.body);
            } else {
                throw new IllTypedException("Guard in for stmt must be boolean");
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

            System.out.println(parsed);

        } catch (Exception e) {
            System.out.println(e.getClass().getSimpleName() +
                    ": " + e.getMessage());
        }

    }

}
