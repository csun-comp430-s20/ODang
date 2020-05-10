package CodeGenerator;

import Parser.Declarations.*;
import Parser.Expressions.*;
import Parser.Literals.*;

import java.util.List;

public class CodeGenerator  {
    private final String codeOutput ="";
    private final List<Decl> AST;

    public CodeGenerator(final List<Decl> AST) {
        this.AST = AST;
    }



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
            String result = "(";
            for (final Exp curArg : asArg.expList){
                if (asArg.expList.indexOf(curArg) == asArg.expList.size() - 1){
                    result = result + generateExp(curArg);
                } else {
                    result = result + generateExp(curArg) + ",";
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
            return asString.name;
        }
        else {
            assert (false);
            throw new CodeGeneratorException("Unrecognizable expression.");
        }
    }//remember to add all the results to the overall string output in the end

    public static void main(String[] args) {

    }
}
