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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import Typechecker.Types.IntType;

public class Typechecker {

    public final List<Decl> program;
    public final Map<String, ClassDecl> classes;
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

    final static List<FormalParameter> convertFormalParamList(final FormalParamList list) throws IllTypedException {
        if (list.declList.isEmpty())
            return new ArrayList<>();
        else {
            final List<FormalParameter> formalParameterList = new ArrayList<>();
            for (final Decl decl : list.declList) {
                final FormalParam parserParam = (FormalParam)decl;
                final Type paramType = convertParserType(parserParam.paramParserType);
                final String paramName = ((IdentifierLiteral)parserParam.paramIdentifier).name;
                formalParameterList.add(new FormalParameter(paramType, paramName));
            }
            return formalParameterList;
        }
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
    }

    /**
     * typechecks a class. will return a env with all defined variables and declarations in the class
     * for typecheckProgram() this will not be used, but is neccessary for typechecking subclasses
     * @param env TypeEnvironment keeping track of variables and functions in the class
     * @param classDecl class that is being typechecked
     * @return TypeEnvironment containing all mappings of functions and variables
     * @throws IllTypedException
     */

    public TypeEnvironment typecheckClass(final TypeEnvironment env, final ClassDecl classDecl) {
        return null;
    }


    public TypeEnvironment typecheckMethod(final TypeEnvironment env, final MethodDecl methodDecl) throws IllTypedException {

        final MethodHeader mh = (MethodHeader)methodDecl.header;
        final Type resultType = convertParserType(mh.resultParserType);
        final MethodDeclarator md = (MethodDeclarator)mh.methodDeclarator;
        final String methodName = ((IdentifierLiteral)md.identifier).name;

        final BlockStmt body = (BlockStmt)methodDecl.body;

        final Stmt hopefullyReturnStmt = body.block.get(body.block.size());

        final TypeEnvironment newEnv = typecheckStmts(env, false, body);

        if (!(hopefullyReturnStmt instanceof ReturnStmt))
            throw new IllTypedException("Missing return statement at end of block");

        else {
            final ReturnStmt returnStmt = (ReturnStmt)hopefullyReturnStmt;
            final Type returnType = typeof(newEnv, returnStmt.exp);

            if (!(resultType.equals(returnType)))
                throw new IllTypedException("Type mismatch. Declared: " + resultType + " ,Actual type: " + returnType);

            final FunctionDefinition functionDefinition = new FunctionDefinition(
                    returnType,
                    methodName,
                    convertFormalParamList((FormalParamList)md.paramList),
                    methodDecl.body,
                    returnStmt);

            return env.addFunction(methodName, functionDefinition);

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
            switch(asBOP.op) {
                case "=":
                case "-=":
                case "+=":
                    final IdentifierLiteral asID = (IdentifierLiteral) asBOP.left;
            
                    if(!env.containsVariable(asID.name))
                        throw new IllTypedException("Variable not in scope: " + asID.name);
            
                    if(left.equals(right)) {
                        return left;
                    } else
                        throw new IllTypedException("Type mismatch: type " + right + "cannot be assigned to type " + left);
                case "+":
                case "-":
                case "*":
                case "/":
                    if(left instanceof IntType && right instanceof IntType) {
                        return new IntType();
                    }
                    else {
                        throw new IllTypedException("Operator " + asBOP.op + " cannot be applied to " + left + ", " + right);
                    }
                case "<":
                case ">":
                    if(left instanceof IntType && right instanceof IntType) {
                        return new BoolType();
                    }
                    else {
                        throw new IllTypedException("Operator " + asBOP.op + " cannot be applied to " + left + ", " + right);
                    }
                case "!=":
                case "==":
                    if(left instanceof IntType && right instanceof IntType) {
                        return new BoolType();
                    }
                    if(left instanceof BoolType && right instanceof BoolType) {
                        return new BoolType();
                    }
                    else {
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

        else if (e instanceof MethodInvocation) {
            final MethodInvocation asInvoc = (MethodInvocation) e;
            final List<Exp> argList = ((ArgumentList)asInvoc.argList).expList;

            if (asInvoc.exp instanceof IdentifierLiteral) {
                final IdentifierLiteral methodIdentifier = (IdentifierLiteral)asInvoc.exp;

                //this throws an exception of function isnt in scope
                final FunctionDefinition methodDef = env.lookupFunction(methodIdentifier.name);
                final Type returnType = methodDef.returnType;

                if (methodDef.formalParams.size() != argList.size())
                    throw new IllTypedException("Wrong number of params in method call");

                //compare called method params to definition
                for (int i = 0; i < methodDef.formalParams.size(); i++) {
                    final Type paramType = typeof(env, argList.get(i));
                    final Type definedParamType = methodDef.formalParams.get(i).theType;

                    if (paramType != definedParamType)
                        throw new IllTypedException("Type mismatch in params of methodcall: " +
                                argList.get(i) +  "is not of type " + definedParamType);
                }

                return returnType;

            } else if (asInvoc.exp instanceof FieldAccessExp) {
                return null;

            } else {
                throw new IllTypedException("Not a valid method call: " + e);
            }
        }
        else if (e instanceof FieldAccessExp) {
            final FieldAccessExp asField = (FieldAccessExp)e;
            final IdentifierLiteral id = (IdentifierLiteral)asField.right;

            if (asField.left instanceof ClassInstanceExp) {
                final ClassInstanceExp asClass = (ClassInstanceExp) asField.left;
                final TypeEnvironment tau = typecheckClass(env, getClass(((IdentifierLiteral)asClass.className).name));
                return tau.lookupVariable(id.name);
            }
            else if (asField.left instanceof MethodInvocation) {
                //TODO finish
                return null;
            }
            else {
                throw new IllTypedException("Not a valid field access" + e);
            }

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
            final Typechecker typechecker = new Typechecker(parsed);
            typechecker.typecheckProgram();

            System.out.println(parsed);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
