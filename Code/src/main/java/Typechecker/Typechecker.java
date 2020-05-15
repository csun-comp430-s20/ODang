package Typechecker;

import Parser.Declarations.*;
import Parser.Expressions.*;
import Parser.Declarations.Decl;
import Parser.Parser;
import Parser.Statements.*;
import Parser.Literals.*;
import Parser.Types.*;

import Parser.Types.ParserVoid;
import Tokenizer.Tokenizer;
import Typechecker.Types.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

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
        else if (type instanceof ParserVoid)
            return new VoidType();

        else throw new IllTypedException("Not a valid Parser.Type to convert: " + type);
    }


    /**
     * converts a Parser.FormalParamList to a List<Typechecker.FormalParameter>
     * @param list Parser.FormalParamList
     * @return List<Formalparameter>
     * @throws IllTypedException
     */
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

    /**
     * attempts to get a class from the classes-map
     * @param className the key
     * @return a ClassDecl
     * @throws IllTypedException if key doesnt match
     */
    public ClassDecl getClass(final String className) throws IllTypedException {
        if (!classes.containsKey(className))
            throw new IllTypedException("No such class defined: " + className);
        else
            return classes.get(className);
    }


    /**
     * copies a TypeEnvironment and returns a TypeEnviroment with a new class-name
     * @param env initial typeenviroment
     * @param newName new class-name to give the enviroment
     * @return new TypeEnvironment with the same functions/variables as env, but with a new name
     */
    public static TypeEnvironment sameEnvNewClassName(final TypeEnvironment env, final String newName) {
        return new TypeEnvironment(
                env.getFunctions(),
                env.getVariables(),
                newName);
    }
    /**
     * creates an empty type-environment without any methods, functions or class
     * @return empty TypeEnviroment
     */
    public static TypeEnvironment createEmptyTypeEnvironment() {
        return new TypeEnvironment(null, null, null);
    }


    /**
     * top level function that calls typecheckClass on all classes found in the classes-list
     * @throws IllTypedException
     */
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

    public TypeEnvironment typecheckClass(final TypeEnvironment env, final ClassDecl classDecl) throws IllTypedException {

        final String className = ((IdentifierLiteral)classDecl.identifier).name;
        final TypeEnvironment envWithClass = new TypeEnvironment(
                env.getFunctions(),
                env.getVariables(),
                className);

        if (!(classDecl.extendsClass == null)) {
            final ClassType superClassType = (ClassType)convertParserType(classDecl.extendsClass);
            final ClassDecl superClass = getClass(superClassType.className);
            final TypeEnvironment newEnv = typecheckClass(envWithClass, superClass);

            return typecheckClassBodyDecs(
                    sameEnvNewClassName(newEnv, className),
                    (ClassBodyDecs)classDecl.classBody);

        }
        else {
            return typecheckClassBodyDecs(envWithClass, (ClassBodyDecs)classDecl.classBody);
        }
    }

    /**
     * loops through and typechecks all class body declarations in a ClassBody
     * @param env the type environment
     * @param classBody the classbody
     * @return Typeenviroment with declared variables and methods
     * @throws IllTypedException if not well-typed
     */
    public TypeEnvironment typecheckClassBodyDecs(final TypeEnvironment env, final ClassBodyDecs classBody) throws IllTypedException {

        if (classBody == null)
            return env;

        TypeEnvironment updatedEnv = env.copy();

        for (final Decl decl : classBody.classBodyDecs) {
            updatedEnv = typecheckClassBodyDecl(updatedEnv, decl);
        }
        return updatedEnv;
    }


    /**
     * attempts to typecheck a ClassBodyDecl, ie. <class member dec> | <constructor dec>,
     * and all nodes within a <class member dec> or a <constructor dec>
     * @param env the type environment
     * @param d the declaration to typecheck
     * @return a typeenvironment containing new declared methods or variables
     * @throws IllTypedException if not well-typed
     */
    public TypeEnvironment typecheckClassBodyDecl (final TypeEnvironment env, final Decl d) throws IllTypedException {

        if (d instanceof ConstructorDecl) {
            final ConstructorDecl constructorDecl = (ConstructorDecl)d;
            final ConstructorDeclarator constrDeclarator = (ConstructorDeclarator)constructorDecl.constructorDeclarator;
            final String constructorName = ((IdentifierLiteral)constrDeclarator.identifier).name;

            if (!constructorName.equals(env.getClassName()))
                throw new IllTypedException("Constructor must have the same name as the class");

            final List<FormalParameter> formalParams = convertFormalParamList(constrDeclarator.paramList);

            TypeEnvironment newEnv = env.copy();
            if (!(formalParams.isEmpty())) {
                for (final FormalParameter param : formalParams)
                    newEnv = newEnv.addVariable(param.theVariable, param.theType);
            }
            final ConstructorBody body = (ConstructorBody) constructorDecl.constructorBody;

            if (body.explConstrInvoc != null) {
                return null;
                //TODO implement super/this class constructor
            }
            else if (!body.blockStmts.isEmpty()){
                final List<Stmt> blockStmts = body.blockStmts;
                for (final Stmt bodyStmt : blockStmts) {
                    newEnv = typecheckStmts(newEnv, false, bodyStmt);
                }

                return newEnv;
            }
            else if (body.blockStmts.isEmpty())
                return env;
            else
                throw new IllTypedException("Invalid form of constructor");
        }

        else if (d instanceof FieldDecl) {
            TypeEnvironment updatedEnv = env.copy();

            final FieldDecl fieldDecl = (FieldDecl)d;
            final Type fieldType = convertParserType(fieldDecl.parserType);
            final VarDeclaratorList varList = (VarDeclaratorList)fieldDecl.varDeclarators;

            for (final Decl decl : varList.varDeclList) {
                final VarDeclarator varDec = (VarDeclarator)decl;
                final IdentifierLiteral identifier = (IdentifierLiteral) varDec.identifier;
                if (varDec.exp == null) {

                    updatedEnv = updatedEnv.addVariable(identifier.name, fieldType);
                }
                else {
                    final Type actualType = typeof(updatedEnv, varDec.exp);
                    if (!(fieldType.equals(actualType)))
                        throw new IllTypedException("Field declared " + fieldType + ", cannot assign " + actualType);
                    else {
                        updatedEnv = updatedEnv.addVariable(identifier.name, fieldType);
                    }
                }
            }
            return updatedEnv;
        }

        else if (d instanceof MethodDecl) {
            final MethodDecl methodDecl = (MethodDecl) d;
            return typecheckMethod(env, methodDecl);
        }

        else {
            assert(false);
            throw new IllTypedException("Not a valid class body declaration");
        }

    }

    /**
     * attempts to typecheck a MethodDeclaration
     * @param env type environment to add method to
     * @param methodDecl the method declaration to typecheck
     * @return TypeEnvironment containing the new method
     * @throws IllTypedException if method decl not well-typed
     */
    public TypeEnvironment typecheckMethod(final TypeEnvironment env, final MethodDecl methodDecl) throws IllTypedException {

        final MethodHeader mh = (MethodHeader)methodDecl.header;
        final Type resultType = convertParserType(mh.resultParserType);
        final MethodDeclarator md = (MethodDeclarator)mh.methodDeclarator;
        final String methodName = ((IdentifierLiteral)md.identifier).name;

        final Block body = (Block)methodDecl.body;

        final Stmt hopefullyReturnStmt = body.blockStmts.get(body.blockStmts.size()-1);

        final TypeEnvironment newEnv = typecheckStmts(env, false, body);

        if (!(hopefullyReturnStmt instanceof ReturnStmt))
            throw new IllTypedException("Missing return statement at end of block");

        else {
            final ReturnStmt returnStmt = (ReturnStmt)hopefullyReturnStmt;
            final Type returnType = typeof(newEnv, returnStmt.exp);

            if (!(resultType.equals(returnType)))
                throw new IllTypedException("Type mismatch in method " + methodName +
                        ". Declared: " + resultType + ", Return type: " + returnType);

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
        if (s instanceof Block) {
            final Block asBlock = (Block)s;

            //do not update env here, whats declared inside block stays inside
            for (final Stmt stmt : asBlock.blockStmts)
                typecheckStmts(env, breakOk, stmt);

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

        if (s instanceof LocalVardec) {


            final LocalVardec varDec = (LocalVardec)s;
            final Type declaredType = convertParserType(varDec.parserType);

            TypeEnvironment updatedEnv = env.copy();
            final VarDeclaratorList varList = (VarDeclaratorList)varDec.varDeclarators;

            for (final Decl decl : varList.varDeclList) {
                final VarDeclarator varDeclarator = (VarDeclarator)decl;
                final IdentifierLiteral identifier = (IdentifierLiteral) varDeclarator.identifier;
                if (varDeclarator.exp == null) {

                    updatedEnv = updatedEnv.addVariable(identifier.name, declaredType);
                }
                else {
                    final Type actualType = typeof(updatedEnv, varDeclarator.exp);
                    if (!(declaredType.equals(actualType)))
                        throw new IllTypedException("Field declared " + declaredType + ", cannot assign " + actualType);
                    else {
                        updatedEnv = updatedEnv.addVariable(identifier.name, declaredType);
                    }
                }
            }
            return updatedEnv;


        }
        else if (s instanceof ExprStmt) {
            final ExprStmt exprStmt = (ExprStmt)s;
            return typecheckStmt(env, breakOk, exprStmt.stmt);
        }

        else if (s instanceof StmtExpr) {
            final StmtExpr stmtExpr = (StmtExpr)s;
            typeof(env, stmtExpr.exp);
            return env;
        }

        else if (s instanceof PrintlnStmt) {
            //TODO javascript can handle this, no need to typecheck I think
            return env;
        }

        else if (s instanceof BreakStmt) {
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
            throw new IllTypedException("Unrecognized statement: " + s);
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

        else if (e instanceof CastExp) {
            final CastExp asCast = (CastExp)e;
            final Type castedType = convertParserType(asCast.parserType);
            final Type expressionType = typeof(env, asCast.exp);

            if (castedType.equals(expressionType))
                return castedType;

            else if (castedType instanceof ClassType && expressionType instanceof ClassType) {
                final ClassDecl possibleSubClass = getClass(((ClassType) castedType).className);
                if (possibleSubClass.extendsClass != null) {
                    final Type superType = convertParserType(possibleSubClass.extendsClass);
                    if (superType.equals(expressionType))
                        return castedType;
                    else throw new IllTypedException("Cannot cast " + ((ClassType) expressionType).className
                            + " to " + ((ClassType) castedType).className);
                } else
                    throw new IllTypedException("Cannot cast " + expressionType + " to " + castedType);
            }
            else if (castedType instanceof StringType) {
                if (expressionType instanceof IntType || expressionType instanceof BoolType)
                    return castedType;
                else
                    throw new IllTypedException("Cannot cast " + expressionType + " to " + castedType);
            }
            else
                throw new IllTypedException("Cannot cast " + expressionType + " to " + castedType);
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

                //this throws an exception if function isnt in scope
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
                return typeof(env, asInvoc.exp);

            } else {
                throw new IllTypedException("Not a valid method call: " + e);
            }
        }
        else if (e instanceof FieldAccessExp) {
            final FieldAccessExp asField = (FieldAccessExp)e;
            final IdentifierLiteral id = (IdentifierLiteral)asField.right;
            final Type idType = typeof(env, id);

            if (asField.left instanceof ClassInstanceExp) {
                final ClassInstanceExp asClass = (ClassInstanceExp) asField.left;
                final TypeEnvironment tau = typecheckClass(env, getClass(((IdentifierLiteral)asClass.className).name));
                return tau.lookupVariable(id.name);
            }
            else if (asField.left instanceof MethodInvocation) {
                final MethodInvocation methodInvocation = (MethodInvocation)asField.left;
                final Type leftType = typeof(env, methodInvocation);
                if (leftType.equals(idType))
                    return leftType;
                else
                    throw new IllTypedException("Not a valid field access, " +
                            asField.left + " is of type: "+ leftType +
                            ", " + id + " is of type: " + idType);

            }
            else {
                throw new IllTypedException("Not a valid field access" + e);
            }

        }
/*
        else if (e instanceof ClassInstanceExp) {
            final ClassInstanceExp asClassInstance = (ClassInstanceExp)e;

        }
*/
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
            final IdentifierLiteral asID = (IdentifierLiteral)e;
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

    //TODO remove before final submission
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

            System.out.println(parsed);

            typechecker.typecheckProgram();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
