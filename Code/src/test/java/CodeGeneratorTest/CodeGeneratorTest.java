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

    public static void assertGeneratesDeclFromString(final String expected, final String receieved) {
        try {
            final Parser parser = new Parser(new Tokenizer(receieved).tokenize());
            final Decl parsedDecl = (parser.parseTopLevelClass());
            Assertions.assertEquals(expected, new CodeGenerator(null).generateDecl(parsedDecl));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //***EXPR TESTS***//
    @Test
    public void checkGeneratesBoolean() {
        assertGenerateExpFromString("false", "false");
    }

    @Test
    public void checkGeneratesIdentifier() {
        assertGenerateExpFromString("varName", "varName");
    }

    @Test
    public void checkGeneratesInteger() {
        assertGenerateExpFromString("6", "6");
    }

    @Test
    public void checkGeneratesNull() {
        assertGenerateExpFromString("null", "null");
    }

    @Test
    public void checkGeneratesString() {
        assertGenerateExpFromString("\"hello\"", "\"hello\"");
    }

    @Test
    public void checkGeneratesPreIncrement() {
        assertGenerateExpFromString("++var", "++var");
    }

    @Test
    public void checkGeneratesPreDecrement() {
        assertGenerateExpFromString("--foo", "--foo");
    }

    @Test
    public void checkGeneratesPostIncrement() {
        assertGenerateExpFromString("bar++", "bar++");
    }

    @Test
    public void checkGeneratesPostDecrement() {
        assertGenerateExpFromString("apple--", "apple--");
    }

    @Test
    public void checkGeneratesAddition() {
        assertGenerateExpFromString("2+3", "2+3");
    }

    @Test
    public void checkGeneratesThreeAdditions() {
        assertGenerateExpFromString("5+6+7+8", "5+6+7+8");
    }

    @Test
    public void checkGeneratesSubtraction() {
        assertGenerateExpFromString("90-8", "90 - 8");
    }

    @Test
    public void checkGeneratesThreeSubtraction() {
        assertGenerateExpFromString("42-30-2-1", "42 - 30 - 2 - 1");
    }

    @Test
    public void checkGeneratesMultiplication() {
        assertGenerateExpFromString("3*7", "3*7");
    }

    @Test
    public void checkGeneratesFourMultiplications() {
        assertGenerateExpFromString("1*2*3*4*5", "1*2*3*4*5");
    }

    @Test
    public void checkGeneratesDivision() {
        assertGenerateExpFromString("4/2", "4/2");
    }

    @Test
    public void checkGeneratesTwoDivisions() {
        assertGenerateExpFromString("10/5/1", "10/5/1");
    }

    @Test
    public void checkGeneratesLessThan() {
        assertGenerateExpFromString("5<6", "5<6");
    }

    @Test
    public void checkGeneratesThreeLessThan() {
        assertGenerateExpFromString("9<10<14<22", "9<10<14<22");
    }

    @Test
    public void checkGeneratesGreatThan() {
        assertGenerateExpFromString("10>9", "10>9");
    }

    @Test
    public void checkGeneratesTwoGreatThan() {
        assertGenerateExpFromString("33>20>10", "33>20>10");
    }

    @Test
    public void checkGeneratesNotEqual() {
        assertGenerateExpFromString("55!=60", "55!=60");
    }

    @Test
    public void checkGeneratesThreeNotEqual() {
        assertGenerateExpFromString("3!=4!=5!=6", "3!=4!=5!=6");
    }

    @Test
    public void checkGeneratesEqual() {
        assertGenerateExpFromString("5==5", "5==5");
    }

    @Test
    public void checkGeneratesThreeEqual() {
        assertGenerateExpFromString("10==10==10==10", "10==10==10==10");
    }

    @Test
    public void checkGeneratesAssignment() {
        assertGenerateExpFromString("var=10", "var = 10");
    }

    @Test
    public void checkGeneratesTwoAssignments() {
        assertGenerateExpFromString("x=y=5", "x = y = 5");
    }

    @Test
    public void checkGeneratesPlusAssignment() {
        assertGenerateExpFromString("z+=2", "z += 2");
    }

    @Test
    public void checkGeneratesTwoPlusAssignments() {
        assertGenerateExpFromString("a+=b+=7", "a += b += 7");
    }

    @Test
    public void checkGeneratesSubstractAssignment() {
        assertGenerateExpFromString("pop-=5", "pop -= 5");
    }

    @Test
    public void checkGeneratesThreeSubtractAssignment() {
        assertGenerateExpFromString("d-=e-=f-=27", "d -= e -= f -= 27");
    }

    @Test
    public void checkGeneratesNegateUnaryExpression() {
        assertGenerateExpFromString("!x", "!x");
    }

    @Test
    public void checkGeneratesFieldAccessExpression() {
        assertGenerateExpFromString("func.method", "func.method");
    }

    @Test
    public void checkGeneratesFieldAccessWithOneArgument() {
        assertGenerateExpFromString("x.bar(false)", "x.bar(false)");
    }

    @Test
    public void checkGeneratesFieldAccessWithThreeArguments() {
        assertGenerateExpFromString("test.foo(false,2,5)", "test.foo(false,2,5)");
    }

    @Test
    public void checkGeneratesFieldAccessWithTwoDots() {
        assertGenerateExpFromString("func.test.work(10,false)",
                "func.test.work(10,false)");
    }
    @Test
    public void checkGeneratesMethodWithOneArgument() {
        assertGenerateExpFromString("apple(2)", "apple(2)");
    }

    @Test
    public void checkGeneratesMethodWithThreeArguments() {
        assertGenerateExpFromString("beta(2,true,false)", "beta(2,true,false)");
    }

    //***STMT TESTS***//
    @Test
    public void checkGeneratesRegularReturnStatement() {
        assertGenerateStmtFromString("return;", "return;");
    }

    @Test
    public void checkGeneratesReturnStatementWithValue() {
        assertGenerateStmtFromString("return 8;", "return 8;");
    }

    @Test
    public void checkGeneratesRegularBreakStatement() {
        assertGenerateStmtFromString("break;", "break;");
    }

    @Test
    public void checkGeneratesBreakStatementWithIdentifier() {
        assertGenerateStmtFromString("break foo;", "break foo;");
    }

    @Test
    public void checkGeneratesEmptyStatement() {
        assertGenerateStmtFromString(";", ";");
    }

    @Test
    public void checkGeneratesPrintStatementWithValue() {
        assertGenerateStmtFromString("console.log(5);", "println(5);");
    }

    @Test
    public void checkGeneratesPrintStatementWithString() {
        assertGenerateStmtFromString("console.log(\"bar\");", "println(\"bar\");");
    }

    @Test
    public void checkGeneratesStmtExprWithLessThan() {
        assertGenerateStmtFromString("5<7;", "5<7;");
    }

    @Test
    public void checkGeneratesStmtExprWithFieldAccess() {
        assertGenerateStmtFromString("foo.bar();", "foo.bar();");
    }

    @Test
    public void checkGeneratesBlockWithReturnStatement() {
        assertGenerateStmtFromString("{return;}", "{return;}");
    }

    @Test
    public void checkGeneratesBlockWithBreakStatement() {
        assertGenerateStmtFromString("{break;}", "{break;}");
    }

    @Test
    public void checkGeneratesBlockWithFieldAccess() {
        assertGenerateStmtFromString("{foo.box();}", "{foo.box();}");
    }

    @Test
    public void checkGeneratesForStatementWithTwoInitializers() {
        assertGenerateStmtFromString("for(foo=0,bar=1;foo<5;foo++){return foo;}", "for(foo = 0, bar = 1; foo < 5; foo++) {return foo;}");
    }

    @Test
    public void checkGeneratesIfElseStatement() {
        assertGenerateStmtFromString("if(x<0){x=1;}else{x=2;}", "if(x < 0) {x = 1;} else {x = 2;}");
    }

    @Test
    public void checkGeneratesWhileStatement() {
        assertGenerateStmtFromString("while(x==5){x+=1;}", "while (x == 5) { x += 1;}");
    }

    //***DECL TESTS***//
    @Test
    public void checkGeneratesEmptyClass() {
        assertGeneratesDeclFromString("function Bar(){}", "class Bar{}");
    }

    @Test
    public void checkGeneratesClassWithInt() {
        assertGeneratesDeclFromString("function Bar(){var x=0;}", "class Bar{ int x = 0; }");
    }

    @Test
    public void checkGeneratesClassWithString() {
        assertGeneratesDeclFromString("function Bar(){var x=\"test\";}", "class Bar{ string x = \"test\"; }");
    }

    @Test
    public void checkGeneratesClassWithBoolean() {
        assertGeneratesDeclFromString("function Bar(){var x=true;}", "class Bar{ boolean x = true; }");
    }

    @Test
    public void checkGeneratesClassWithIntAddition() {
        assertGeneratesDeclFromString("function Bar(){var x=1+2;}", "class Bar{ int x = 1 + 2;}");
    }

    @Test
    public void checkGeneratesClassWithIntSubtraction() {
        assertGeneratesDeclFromString("function Bar(){var x=2-1;}", "class Bar{ int x = 2 - 1;}");
    }

    @Test
    public void checkGeneratesClassWithIntMultiplication() {
        assertGeneratesDeclFromString("function Bar(){var x=2*2;}", "class Bar{ int x = 2 * 2;}");
    }

    @Test
    public void checkGeneratesClassWithIntDivision() {
        assertGeneratesDeclFromString("function Bar(){var x=2/2;}", "class Bar{ int x = 2 / 2;}");
    }

    @Test
    public void checkGeneratesClassWithTwoInts(){
        assertGeneratesDeclFromString("function Bar(){var x=1;var y=2;}", "class Bar{ int x = 1; int y = 2;}");
    }
    
}
