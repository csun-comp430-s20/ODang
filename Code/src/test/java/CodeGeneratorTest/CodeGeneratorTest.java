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
            Assertions.assertEquals(expected, new CodeGenerator(null).generateExp(received, parsedExp));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void checkGeneratesInteger() throws CodeGeneratorException{
        assertGenerateExpFromString("6", "6");
    }
}
