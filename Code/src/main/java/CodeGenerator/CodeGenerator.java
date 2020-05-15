package CodeGenerator;

import Parser.Declarations.*;
import Parser.Statements.*;
import Parser.Expressions.*;
import Parser.Literals.*;
import Typechecker.IllTypedException;

import java.util.List;

public class CodeGenerator  {
    private final String codeOutput ="";
    private final List<Decl> AST;

    public CodeGenerator(final List<Decl> AST) {
        this.AST = AST;
    }

    public String generateDecl(final Decl d) throws CodeGeneratorException{
        if (d instanceof ClassDecl){//TODO make this look nice if time permits
            final ClassDecl asClassDecl = (ClassDecl)d;
            StringBuilder result = new StringBuilder();
            result.append("function ");
            StringBuilder body = new StringBuilder();
            result.append(generateExp(asClassDecl.identifier));
            result.append("(");
            if (asClassDecl.extendsClass == null){//TODO this and super
                if (asClassDecl.classBody == null) {
                    return result.append("){}").toString();
                }else{
                    final ClassBodyDecs asClassBodyDecs = (ClassBodyDecs)asClassDecl.classBody;
                    final StringBuilder parameters = new StringBuilder();

                    for (Decl decl : asClassBodyDecs.classBodyDecs){
                        if (decl instanceof ConstructorDecl){
                            final ConstructorDecl asConstructorDecl = (ConstructorDecl)decl;
                            final ConstructorDeclarator asConstructorDeclarator = (ConstructorDeclarator)asConstructorDecl.constructorDeclarator;
                            final String curParams = generateDecl(asConstructorDeclarator.paramList);

                            //TODO check multiple constructors
                            //if (parameters.toString().contains())
                        }
                        else if (decl instanceof FieldDecl){
                            final FieldDecl asFieldDecl = (FieldDecl)decl;
                            StringBuilder declString = new StringBuilder();
                            declString.append("var ");
                            final VarDeclaratorList asVarDeclaratorList = (VarDeclaratorList)asFieldDecl.varDeclarators;

                            for (Decl vardecl : asVarDeclaratorList.varDeclList){
                                final VarDeclarator asVarDeclarator = (VarDeclarator)vardecl;

                                if (asVarDeclarator.exp == null){
                                    declString.append(generateExp(asVarDeclarator.identifier));
                                } else {
                                    declString.append(generateExp(asVarDeclarator.identifier));
                                    declString.append("=");
                                    declString.append(generateExp(asVarDeclarator.exp));
                                }
                                if (asVarDeclaratorList.varDeclList.indexOf(vardecl) == asVarDeclaratorList.varDeclList.size() - 1){
                                   //do nothing
                                } else {
                                    declString.append(",");
                                }
                            }
                            body.append(declString);
                            body.append(";");
                        }
                        else if (decl instanceof MethodDecl){
                            final MethodDecl asMethodDecl = (MethodDecl)decl;

                            final MethodHeader asMethodHeader = (MethodHeader)asMethodDecl.header;
                            final MethodDeclarator asMethodDeclarator = (MethodDeclarator)asMethodHeader.methodDeclarator;
                            final String methodName = generateExp(asMethodDeclarator.identifier);
                            final String argList = generateDecl(asMethodDeclarator.paramList);
                            final String methodBody = generateStmt(asMethodDecl.body);
                            final String methodString = "this." + methodName + "=function(" + argList + ")" + methodBody;

                            body.append(methodString);
                        }
                    }

                    result.append(")");
                }
            }
            if (asClassDecl.classBody == null){
                return result.append("{}").toString();
            } else {
                result.append("{");
                result.append(body);
                return result.append("}").toString();
            }
        }
        else if (d instanceof ConstructorDeclarator){
            final ConstructorDeclarator asConstructorDeclarator = (ConstructorDeclarator)d;

            if (asConstructorDeclarator.paramList == null){
                return "";
            }
            else {
                return generateDecl(asConstructorDeclarator.paramList);
            }
        }
        else if (d instanceof FormalParamList){
            final FormalParamList asFormalParamList = (FormalParamList)d;
            StringBuilder result = new StringBuilder();

            for (final Decl decl : asFormalParamList.declList){
                final FormalParam asFormalParam = (FormalParam)decl;
                if (asFormalParamList.declList.indexOf(decl) == asFormalParamList.declList.size() - 1){
                    result.append(generateExp(asFormalParam.paramIdentifier));
                } else {
                    result.append(generateExp(asFormalParam.paramIdentifier));
                    result.append(",");
                }
            }

            return result.toString();
        }
        else if (d instanceof ConstructorBody){
            final ConstructorBody asConstructorBody = (ConstructorBody)d;
            if (asConstructorBody.explConstrInvoc == null){
                //TODO check if this is working correctly
                StringBuilder body = new StringBuilder();
                for (final Stmt bodyStmt : asConstructorBody.blockStmts) {
                    body.append(generateStmt(bodyStmt));
                }
                return body.substring(0, body.length() - 1);
            }
            else {
                return "";//TODO this and super
            }
        }
        else if (d instanceof VarDeclarator){
            final VarDeclarator asVar = (VarDeclarator)d;
            if (asVar.exp == null){
                return generateExp(asVar.identifier);
            }
            else
                return generateExp(asVar.identifier) + "=" + generateExp(asVar.exp);
        }
        else{
            assert(false);
            throw new CodeGeneratorException("Unrecognizable declaration.");
        }
    }

    /**
     * attempts to code generate a statement
     * @param s current statement
     * @return string that represents the code output of s
     * @throws CodeGeneratorException unrecognized statement
     */
    public String generateStmt(final Stmt s) throws CodeGeneratorException{
        if (s instanceof ExprStmt){
            final ExprStmt asExprStmt = (ExprStmt)s;
            return generateStmt(asExprStmt.stmt) + ";";
        }
        else if (s instanceof StmtExpr){
            final StmtExpr asStmtExpr = (StmtExpr)s;
            return generateExp(asStmtExpr.exp) /*+ ";"*/;
        }
        else if (s instanceof StmtExprList){
            final StmtExprList asStmtExprList = (StmtExprList)s;
            StringBuilder result = new StringBuilder();

            for (final Stmt stmt : asStmtExprList.list){
                if (asStmtExprList.list.indexOf(stmt) == asStmtExprList.list.size() - 1){
                    result.append(generateStmt(stmt));
                } else {
                    result.append(generateStmt(stmt));
                    result.append(",");
                }
            }

            return result.toString();
        }
        else if (s instanceof Block){
            final Block asBlock = (Block)s;
            StringBuilder result = new StringBuilder();
            result.append("{");

            for (final Stmt stmt : asBlock.blockStmts){
                result.append(generateStmt(stmt));
            }
            return result + "}";
        }
        else if (s instanceof ForStmt){
            final ForStmt asFor = (ForStmt)s;//local var decl
            String result = "for(";
            final String init = generateStmt(asFor.forInit);
            result += init + ";";
            final String conditional = generateExp(asFor.conditional);
            result += conditional + ";";
            final String update = generateStmt(asFor.forUpdate);
            result  += update + ")";
            final String block = generateStmt(asFor.body);
            return result + block;
        }
        else if (s instanceof IfElseStmt){
            final IfElseStmt asIf = (IfElseStmt)s;
            String result = "if(";
            final String guard = generateExp(asIf.guard);
            result += guard + ")";
            final String trueBranch = generateStmt(asIf.trueBranch);
            result += trueBranch + "else";
            final String falseBranch = generateStmt(asIf.falseBranch);
            return result + falseBranch;
        }
        else if (s instanceof WhileStmt){
            final WhileStmt asWhile = (WhileStmt)s;
            String result = "while(";
            final String guard = generateExp(asWhile.guard);
            result+= guard + ")";
            final String body = generateStmt(asWhile.body);

            return result + body;
        }
        else if (s instanceof ReturnStmt){
            final ReturnStmt asReturn = (ReturnStmt)s;
            if (asReturn.exp == null) {
                return "return;";
            }
            else{
                return "return " + generateExp(asReturn.exp) + ";";
            }
        }
        else if (s instanceof  BreakStmt){
            final BreakStmt asBreak = (BreakStmt)s;
            if (asBreak.identifier == null){
                return "break;";
            }
            else {
                return "break " + generateExp(asBreak.identifier) + ";";
            }
        }
        else if (s instanceof EmptyStmt){
            return ";";
        }
        else if (s instanceof PrintlnStmt){
            final PrintlnStmt asPrint = (PrintlnStmt)s;
            return "console.log(" + generateExp(asPrint.exp) + ");";
        }
        else {
            assert(false);
            throw new CodeGeneratorException("Unrecognizable statement.");
        }
    }

    /**
     * attempts to code generate an expression
     * @param e current expression
     * @return string that represents the code output of e
     * @throws CodeGeneratorException unrecognized expression
     */
    public String generateExp(final Exp e) throws CodeGeneratorException{
        if (e instanceof BinaryOperatorExp){
            final BinaryOperatorExp asBinop = (BinaryOperatorExp)e;
            final String leftExp = generateExp(asBinop.left);
            final String rightExp = generateExp(asBinop.right);
            return leftExp + asBinop.op + rightExp;
        }
        else if (e instanceof PreIncrDecrExp){
            final PreIncrDecrExp asPre = (PreIncrDecrExp)e;
            return asPre.preOp + generateExp(asPre.prefixExp);
        }
        else if (e instanceof PostIncrDecrExp){
            final PostIncrDecrExp asPost = (PostIncrDecrExp)e;
            return generateExp(asPost.postfixExp) + asPost.postOp;
        }
        else if (e instanceof NegateUnaryExp) {
            final NegateUnaryExp asNeg = (NegateUnaryExp)e;
            return "!" + generateExp(asNeg.exp);
        }
        else if (e instanceof FieldAccessExp){
            final FieldAccessExp asField = (FieldAccessExp)e;
            return generateExp(asField.left) + "." + generateExp(asField.right);
        }
        else if (e instanceof ArgumentList){
            final ArgumentList asArg = (ArgumentList)e;
            StringBuilder result = new StringBuilder();
            result.append("(");

            for (final Exp curArg : asArg.expList){
                if (asArg.expList.indexOf(curArg) == asArg.expList.size() - 1){
                    result.append(generateExp(curArg));
                } else {
                    result.append(generateExp(curArg));
                    result.append(",");
                }
            }
            return result + ")";
        }
        else if (e instanceof MethodInvocation){
            final MethodInvocation asMethod = (MethodInvocation)e;
            final String invoker = generateExp(asMethod.exp);
            final String argList = generateExp(asMethod.argList);
            return invoker + argList;
        }
        else if (e instanceof BooleanLiteral) {
            final BooleanLiteral asBool = (BooleanLiteral) e;
            return Boolean.toString(asBool.value);
        }
        else if (e instanceof IdentifierLiteral) {
            final IdentifierLiteral asId = (IdentifierLiteral) e;
            return asId.name;
        }
        else if (e instanceof IntegerLiteral) {
            final IntegerLiteral asInt = (IntegerLiteral) e;
            return Integer.toString(asInt.value);
        }
        else if (e instanceof NullLiteral) {
            final NullLiteral asNull = (NullLiteral) e;
            return "null";
        }
        else if (e instanceof StringLiteral) {
            final StringLiteral asString = (StringLiteral) e;
            return "\"" + asString.name + "\"";
        }
        else {
            assert (false);
            throw new CodeGeneratorException("Unrecognizable expression.");
        }
    }// TODO remember to add all the results to the overall string output in the end and to deal with whitespaces

    public static void main(String[] args) {

    }
}
