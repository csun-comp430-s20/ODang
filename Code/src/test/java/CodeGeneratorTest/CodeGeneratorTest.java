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

import com.sun.tools.javac.jvm.Code;
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
}
