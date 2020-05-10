package CodeGeneratorTest;
import Parser.*;
import Parser.Types.*;
import Parser.Literals.*;
import Parser.Expressions.*;
import Parser.Declarations.*;
import Parser.Statements.*;
import Tokenizer.*;
import Tokenizer.Tokens.*;
import CodeGenerator.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


public class CodeGeneratorTest {

    public static void assertGenerateExpFromString(final String expected, final String received) {
        try {
            final Parser parser = new Parser(new Tokenizer(received).tokenize());
            final Exp parsedExp = (parser.parseExp(0)).getResult();
            Assertions.assertEquals(expected, new CodeGenerator(null).generateExp(parsedExp));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void assertGenerateStmtFromString(final String expected, final String receieved){
        try {
            final Parser parser = new Parser(new Tokenizer(receieved).tokenize());
            final Stmt parsedStmt = (parser.parseStmt(0)).getResult();
            Assertions.assertEquals(expected, new CodeGenerator(null).generateStmt(parsedStmt));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //***EXPR TESTS***//
    @Test
    public void checkGeneratesBoolean() throws CodeGeneratorException{
        assertGenerateExpFromString("false", "false");
    }

    @Test
    public void checkGeneratesIdentifier() throws CodeGeneratorException{
        assertGenerateExpFromString("varName", "varName");
    }

    @Test
    public void checkGeneratesInteger() throws CodeGeneratorException{
        assertGenerateExpFromString("6", "6");
    }

    @Test
    public void checkGeneratesNull() throws CodeGeneratorException{
        assertGenerateExpFromString("null", "null");
    }

    @Test
    public void checkGeneratesString() throws CodeGeneratorException{
        assertGenerateExpFromString("hello", "\"hello\"");
    }

    @Test
    public void checkGeneratesPreIncrement() throws CodeGeneratorException{
        assertGenerateExpFromString("++var", "++var");
    }

    @Test
    public void checkGeneratesPreDecrement() throws CodeGeneratorException{
        assertGenerateExpFromString("--foo", "--foo");
    }

    @Test
    public void checkGeneratesPostIncrement() throws CodeGeneratorException{
        assertGenerateExpFromString("bar++", "bar++");
    }

    @Test
    public void checkGeneratesPostDecrement() throws CodeGeneratorException{
        assertGenerateExpFromString("apple--", "apple--");
    }

    @Test
    public void checkGeneratesAddition() throws CodeGeneratorException{
        assertGenerateExpFromString("2+3", "2+3");
    }

    @Test
    public void checkGeneratesMultipleAdditions() throws CodeGeneratorException{
        assertGenerateExpFromString("5+6+7+8", "5+6+7+8");
    }

    @Test
    public void checkGeneratesSubtraction() throws CodeGeneratorException{
        assertGenerateExpFromString("90-8", "90 - 8");
    }

    @Test
    public void checkGeneratesMultipleSubtraction() throws CodeGeneratorException{
        assertGenerateExpFromString("42-30-2-1", "42 - 30 - 2 - 1");
    }

    @Test
    public void checkGeneratesMultiplication() throws CodeGeneratorException{
        assertGenerateExpFromString("3*7", "3*7");
    }

    @Test
    public void checkGeneratesMultipleMultiplications() throws CodeGeneratorException{
        assertGenerateExpFromString("1*2*3*4*5", "1*2*3*4*5");
    }

    @Test
    public void checkGeneratesDivision() throws CodeGeneratorException{
        assertGenerateExpFromString("4/2", "4/2");
    }

    @Test
    public void checkGeneratesMultipleDivisions() throws CodeGeneratorException{
        assertGenerateExpFromString("10/5/1", "10/5/1");
    }

    @Test
    public void checkGeneratesLessThan() throws CodeGeneratorException{
        assertGenerateExpFromString("5<6", "5<6");
    }

    @Test
    public void checkGeneratesMultipleLessThan() throws CodeGeneratorException{
        assertGenerateExpFromString("9<10<14<22", "9<10<14<22");
    }

    @Test
    public void checkGeneratesGreatThan() throws CodeGeneratorException{
        assertGenerateExpFromString("10>9", "10>9");
    }

    @Test
    public void checkGeneratesMultipleGreatThan() throws CodeGeneratorException{
        assertGenerateExpFromString("33>20>10", "33>20>10");
    }

    @Test
    public void checkGeneratesNotEqual() throws CodeGeneratorException{
        assertGenerateExpFromString("55!=60", "55!=60");
    }

    @Test
    public void checkGeneratesMultipleNotEqual() throws CodeGeneratorException{
        assertGenerateExpFromString("3!=4!=5!=6", "3!=4!=5!=6");
    }

    @Test
    public void checkGeneratesEqual() throws CodeGeneratorException{
        assertGenerateExpFromString("5==5", "5==5");
    }

    @Test
    public void checkGeneratesMultipleEqual() throws CodeGeneratorException{
        assertGenerateExpFromString("10==10==10==10", "10==10==10==10");
    }

    @Test
    public void checkGeneratesAssignment() throws CodeGeneratorException{
        assertGenerateExpFromString("var=10", "var = 10");
    }

    @Test
    public void checkGeneratesMultipleAssignment() throws CodeGeneratorException{
        assertGenerateExpFromString("x=y=5", "x = y = 5");
    }

    @Test
    public void checkGeneratesPlusAssignment() throws CodeGeneratorException{
        assertGenerateExpFromString("z+=2", "z += 2");
    }

    @Test
    public void checkGeneratesMultiplePlusAssignments() throws CodeGeneratorException{
        assertGenerateExpFromString("a+=b+=7", "a += b += 7");
    }

    @Test
    public void checkGeneratesSubstractAssignment() throws CodeGeneratorException{
        assertGenerateExpFromString("pop-=5", "pop -= 5");
    }

    @Test
    public void checkGeneratesMultipleSubtractAssignment() throws CodeGeneratorException{
        assertGenerateExpFromString("d-=e-=f-=27", "d -= e -= f -= 27");
    }

    @Test
    public void checkGeneratesNegateUnaryExpression() throws CodeGeneratorException{
        assertGenerateExpFromString("!x", "!x");
    }

    @Test
    public void checkGeneratesFieldAccessExpression() throws CodeGeneratorException{
        assertGenerateExpFromString("func.method", "func.method");
    }

    @Test
    public void checkGeneratesFieldAccessWithOneArgument() throws CodeGeneratorException{
        assertGenerateExpFromString("x.bar(false)", "x.bar(false)");
    }

    @Test
    public void checkGeneratesFieldAccessWithMultipleArguments() throws CodeGeneratorException{
        assertGenerateExpFromString("test.foo(false,2,5)", "test.foo(false,2,5)");
    }

    @Test
    public void checkGeneratesFieldAccessWithMultipleDots() throws CodeGeneratorException{
        assertGenerateExpFromString("func.test.work(10,false)",
                "func.test.work(10,false)");
    }
    @Test
    public void checkGeneratesMethodWithOneArgument() throws CodeGeneratorException{
        assertGenerateExpFromString("apple(2)", "apple(2)");
    }

    @Test
    public void checkGeneratesMethodWithMultipleArguments() throws CodeGeneratorException{
        assertGenerateExpFromString("beta(2,true,false)", "beta(2,true,false)");
    }

    //***STMT TESTS***//
    @Test
    public void checkGeneratesRegularReturnStatement() throws CodeGeneratorException{
        assertGenerateStmtFromString("return;", "return;");
    }

    @Test
    public void checkGeneratesReturnStatementWithValue() throws CodeGeneratorException{
        assertGenerateStmtFromString("return 8;", "return 8;");
    }
}
