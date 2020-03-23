package TokenizerTest;

import Tokenizer.*;
import Tokenizer.Tokens.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.*;

public class TokenTest {

    public static void checkTokenizes(final String input,
                                      Token... expected) throws TokenizerException {
        final List<Token> expectedTokens = new ArrayList<>();
        for (final Token token : expected)
            expectedTokens.add(token);
        final Tokenizer testTokenizer = new Tokenizer(input);
        final List<Token> receivedTokens = testTokenizer.tokenize();
        Assertions.assertEquals(expectedTokens, receivedTokens);
    }
    @Test
    public void checkCorrectTokenizedInteger() throws TokenizerException {
        checkTokenizes("1234", new IntegerToken(1234));
    }
    @Test
    public void checkCorrectTokenizedIntegerWithSpaceBefore() throws TokenizerException {
        checkTokenizes(" 1234", new IntegerToken(1234));
    }
    @Test
    public void checkCorrectTokenizedIntegerWithSpaceAfter() throws TokenizerException {
        checkTokenizes("1234 ", new IntegerToken(1234));
    }
    @Test
    public void checkCorrectTokenizedTwoIntegers() throws TokenizerException {
        checkTokenizes("12 34", new IntegerToken(12), new IntegerToken(34));
    }
    @Test
    public void checkCorrectTokenizedNegativeInteger() throws TokenizerException {
        checkTokenizes("-12", new IntegerToken(-12));
    }
    @Test
    public void checkCorrectTokenizedTwoNegativeIntegers() throws TokenizerException {
        checkTokenizes("-12 -34", new IntegerToken(-12), new IntegerToken(-34));
    }
    @Test
    public void checkCorrectTokenizedString() throws TokenizerException {
        checkTokenizes("\"string\"", new StringToken("string"));
    }
    @Test
    public void checkTokenizeStringWithReservedWord() throws TokenizerException {
        checkTokenizes("\"for while &&\"", new StringToken("for while &&"));
    }
    @Test
    public void checkTokenizeStringWithIntegers() throws TokenizerException {
        checkTokenizes("\"10 is an integer\"", new StringToken("10 is an integer"));
    }
    @Test
    public void checkCorrectTokenizedTwoStrings() throws TokenizerException {
        checkTokenizes("\"string\" \"string2\"",
                new StringToken("string"),
                new StringToken("string2"));
    }
    @Test
    public void checkCorrectTokenizedLParen() throws TokenizerException {
        checkTokenizes("(", new LeftParenToken());
    }
    @Test
    public void checkCorrectTokenizedLParenWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" (", new LeftParenToken());
    }
    @Test
    public void checkCorrectTokenizedLParenWhitespaceAfter() throws TokenizerException {
        checkTokenizes("( ", new LeftParenToken());
    }
    @Test
    public void checkCorrectTokenizedRParen() throws TokenizerException {
        checkTokenizes(")", new RightParenToken());
    }
    @Test
    public void checkCorrectTokenizedRParenWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" )", new RightParenToken());
    }
    @Test
    public void checkCorrectTokenizedRParenWhitespaceAfter() throws TokenizerException {
        checkTokenizes(") ", new RightParenToken());
    }
    @Test
    public void checkCorrectTokenizedLCurly() throws TokenizerException {
        checkTokenizes("{", new LeftCurlyToken());
    }
    @Test
    public void checkCorrectTokenizedLCurlyWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" {", new LeftCurlyToken());
    }
    @Test
    public void checkCorrectTokenizedLCurlyWhitespaceAfter() throws TokenizerException {
        checkTokenizes("{ ", new LeftCurlyToken());
    }
    @Test
    public void checkCorrectTokenizedRCurly() throws TokenizerException {
        checkTokenizes("}", new RightCurlyToken());
    }
    @Test
    public void checkCorrectTokenizedRCurlyWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" }", new RightCurlyToken());
    }
    @Test
    public void checkCorrectTokenizedRCurlyWhitespaceAfter() throws TokenizerException {
        checkTokenizes("} ", new RightCurlyToken());
    }
    @Test
    public void checkCorrectTokenizedIf() throws TokenizerException {
        checkTokenizes("if", new IfToken());
    }
    @Test
    public void checkCorrectTokenizedIfWithWhiteSpaceBefore() throws TokenizerException {
        checkTokenizes(" if", new IfToken());
    }
    @Test
    public void checkCorrectTokenizedIfWithWhiteSpaceAfter() throws TokenizerException {
        checkTokenizes("if ", new IfToken());
    }
    @Test
    public void checkCorrectTokenizedElse() throws TokenizerException {
        checkTokenizes("else", new ElseToken());
    }
    @Test
    public void checkCorrectTokenizedElseWithWhiteSpaceBefore() throws TokenizerException {
        checkTokenizes(" else", new ElseToken());
    }
    @Test
    public void checkCorrectTokenizedElseWithWhiteSpaceAfter() throws TokenizerException {
        checkTokenizes("else ", new ElseToken());
    }
    @Test
    public void checkCorrectTokenizedBreak() throws TokenizerException {
        checkTokenizes("break", new BreakToken());
    }
    @Test
    public void checkCorrectTokenizedBreakWithWhiteSpaceBefore() throws TokenizerException {
        checkTokenizes(" break", new BreakToken());
    }
    @Test
    public void checkCorrectTokenizedBreakWithWhiteSpaceAfter() throws TokenizerException {
        checkTokenizes("break ", new BreakToken());
    }
    @Test
    public void checkCorrectTokenizedReturn() throws TokenizerException {
        checkTokenizes("return", new ReturnToken());
    }@Test
    public void checkCorrectTokenizedReturnWithWhiteSpaceBefore() throws TokenizerException {
        checkTokenizes(" return", new ReturnToken());
    }
    @Test
    public void checkCorrectTokenizedReturnWithWhiteSpaceAfter() throws TokenizerException {
        checkTokenizes("return ", new ReturnToken());
    }
    @Test
    public void checkCorrectTokenizedFor() throws TokenizerException {
        checkTokenizes("for", new ForToken());
    }
    @Test
    public void checkCorrectTokenizedForWithWhiteSpaceBefore() throws TokenizerException {
        checkTokenizes(" for", new ForToken());
    }
    @Test
    public void checkCorrectTokenizedForWithWhiteSpaceAfter() throws TokenizerException {
        checkTokenizes("for ", new ForToken());
    }
    @Test
    public void checkCorrectTokenizedWhile() throws TokenizerException {
        checkTokenizes("while", new WhileToken());
    }
    @Test
    public void checkCorrectTokenizedWhileWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" while", new WhileToken());
    }
    @Test
    public void checkCorrectTokenizedWhileWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("while ", new WhileToken());
    }
    @Test
    public void checkCorrectTokenizedVoid() throws TokenizerException {
        checkTokenizes("void", new VoidToken());
    }
    @Test
    public void checkCorrectTokenizedVoidWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" void", new VoidToken());
    }
    @Test
    public void checkCorrectTokenizedVoidWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("void ", new VoidToken());
    }
    @Test
    public void checkCorrectTokenizedPrintln() throws TokenizerException {
        checkTokenizes("println", new PrintlnToken());
    }
    @Test
    public void checkCorrectTokenizedPrintlnWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" println", new PrintlnToken());
    }
    @Test
    public void checkCorrectTokenizedPrintlnWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("println ", new PrintlnToken());
    }
    @Test
    public void checkCorrectTokenizedThis() throws TokenizerException {
        checkTokenizes("this", new ThisToken());
    }
    @Test
    public void checkCorrectTokenizedThisWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" this", new ThisToken());
    }
    @Test
    public void checkCorrectTokenizedThisWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("this ", new ThisToken());
    }
    @Test
    public void checkCorrectTokenizedSuper() throws TokenizerException {
        checkTokenizes("super", new SuperToken());
    }
    @Test
    public void checkCorrectTokenizedSuperWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" super", new SuperToken());
    }
    @Test
    public void checkCorrectTokenizedSuperWhitespaceAfter() throws TokenizerException {
        checkTokenizes("super ", new SuperToken());
    }
    @Test
    public void checkCorrectTokenizedNewToken() throws TokenizerException {
        checkTokenizes("new", new NewToken());
    }
    @Test
    public void checkCorrectTokenizedNewTokenWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" new", new NewToken());
    }
    @Test
    public void checkCorrectTokenizedNewTokenWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("new ", new NewToken());
    }
    @Test
    public void checkCorrectTokenizedExtendsToken() throws TokenizerException {
        checkTokenizes("extends", new ExtendsToken());
    }
    @Test
    public void checkCorrectTokenizedExtendsTokenWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" extends", new ExtendsToken());
    }
    @Test
    public void checkCorrectTokenizedExtendsTokenWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("extends ", new ExtendsToken());
    }
    @Test
    public void checkCorrectTokenizedStringType() throws TokenizerException {
        checkTokenizes("String", new StringTypeToken());
    }
    @Test
    public void checkCorrectTokenizedStringTypeWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" String", new StringTypeToken());
    }
    @Test
    public void checkCorrectTokenizedStringTypeWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("String ", new StringTypeToken());
    }
    @Test
    public void checkCorrectTokenizedIntType() throws TokenizerException {
        checkTokenizes("int", new IntTypeToken());
    }
    @Test
    public void checkCorrectTokenizedIntTypeWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" int", new IntTypeToken());
    }
    @Test
    public void checkCorrectTokenizedIntTypeWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("int ", new IntTypeToken());
    }
    @Test
    public void checkCorrectTokenizedCharType() throws TokenizerException {
        checkTokenizes("char", new CharTypeToken());
    }
    @Test
    public void checkCorrectTokenizedCharTypeWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" char", new CharTypeToken());
    }
    @Test
    public void checkCorrectTokenizedCharTypeWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("char ", new CharTypeToken());
    }
    @Test
    public void checkCorrectTokenizedBooleanType() throws TokenizerException {
        checkTokenizes("boolean", new BooleanTypeToken());
    }
    @Test
    public void checkCorrectTokenizedBooleanTypeWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" boolean", new BooleanTypeToken());
    }
    @Test
    public void checkCorrectTokenizedBooleanTypeWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("boolean ", new BooleanTypeToken());
    }
    @Test
    public void checkCorrectTokenizedNull() throws TokenizerException {
        checkTokenizes("null", new NullToken());
    }
    @Test
    public void checkCorrectTokenizedNullWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" null", new NullToken());
    }
    @Test
    public void checkCorrectTokenizedNullWhitespaceAfter() throws TokenizerException {
        checkTokenizes("null ", new NullToken());
    }
    @Test public void checkCorrectTokenizedTrue() throws TokenizerException {
        checkTokenizes("true", new BooleanToken(true));
    }
    @Test public void checkCorrectTokenizedTrueWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" true", new BooleanToken(true));
    }
    @Test public void checkCorrectTokenizedTrueWhitespaceAfter() throws TokenizerException {
        checkTokenizes("true ", new BooleanToken(true));
    }
    @Test public void checkCorrectTokenizedFalse() throws TokenizerException {
        checkTokenizes("false", new BooleanToken(false));
    }
    @Test public void checkCorrectTokenizedFalseWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" false", new BooleanToken(false));
    }
    @Test public void checkCorrectTokenizedFalseWhitespaceAfter() throws TokenizerException {
        checkTokenizes("false ", new BooleanToken(false));
    }
    @Test
    public void checkCorrectTokenizedIdentifier() throws TokenizerException {
        checkTokenizes("ifelsewhile", new IdentifierToken("ifelsewhile"));
    }
    @Test
    public void checkCorrectTokenizedIdentifierWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" ifelsewhile", new IdentifierToken("ifelsewhile"));
    }
    @Test
    public void checkCorrectTokenizedIdentifierWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("ifelsewhile ", new IdentifierToken("ifelsewhile"));
    }
    @Test
    public void checkCorrectTokenizedPlus() throws TokenizerException {
        checkTokenizes("+", new OperatorToken("+"));
    }
    @Test
    public void checkCorrectTokenizedPlusWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" +", new OperatorToken("+"));
    }
    @Test
    public void checkCorrectTokenizedPlusWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("+ ", new OperatorToken("+"));
    }
    @Test
    public void checkCorrectTokenizedMinus() throws TokenizerException {
        checkTokenizes("-", new OperatorToken("-"));
    }
    @Test
    public void checkCorrectTokenizedMinusWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" -", new OperatorToken("-"));
    }
    @Test
    public void checkCorrectTokenizedMinusWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("- ", new OperatorToken("-"));
    }
    @Test
    public void checkCorrectTokenizedEquals() throws TokenizerException {
        checkTokenizes("=", new OperatorToken("="));
    }
    @Test
    public void checkCorrectTokenizedEqualsWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" =", new OperatorToken("="));
    }
    @Test
    public void checkCorrectTokenizedEqualsWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("= ", new OperatorToken("="));
    }
    @Test
    public void checkCorrectTokenizedMultiply() throws TokenizerException {
        checkTokenizes("*", new OperatorToken("*"));
    }
    @Test
    public void checkCorrectTokenizedMultiplyWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" *", new OperatorToken("*"));
    }
    @Test
    public void checkCorrectTokenizedMultiplyWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("* ", new OperatorToken("*"));
    }
    @Test
    public void checkCorrectTokenizedDivision() throws TokenizerException {
        checkTokenizes("/", new OperatorToken("/"));
    }
    @Test
    public void checkCorrectTokenizedDivisionWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" /", new OperatorToken("/"));
    }
    @Test
    public void checkCorrectTokenizedDivisionWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("/ ", new OperatorToken("/"));
    }
    @Test
    public void checkCorrectTokenizedNegation() throws TokenizerException {
        checkTokenizes("!", new OperatorToken("!"));
    }
    @Test
    public void checkCorrectTokenizedNegationWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" !", new OperatorToken("!"));
    }
    @Test
    public void checkCorrectTokenizedNegationWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("! ", new OperatorToken("!"));
    }
    @Test
    public void checkCorrectTokenizedGreaterThan() throws TokenizerException {
        checkTokenizes(">", new OperatorToken(">"));
    }
    @Test
    public void checkCorrectTokenizedGreaterThanWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" >", new OperatorToken(">"));
    }
    @Test
    public void checkCorrectTokenizedGreaterThanWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("> ", new OperatorToken(">"));
    }
    @Test
    public void checkCorrectTokenizedLessThan() throws TokenizerException {
        checkTokenizes("<", new OperatorToken("<"));
    }
    @Test
    public void checkCorrectTokenizedLessThanWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" <", new OperatorToken("<"));
    }
    @Test
    public void checkCorrectTokenizedLessThanWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("< ", new OperatorToken("<"));
    }
    @Test
    public void checkCorrectTokenizedReferenceEquals() throws TokenizerException {
        checkTokenizes("==", new OperatorToken("=="));
    }
    @Test
    public void checkCorrectTokenizedReferenceEqualsWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" ==", new OperatorToken("=="));
    }
    @Test
    public void checkCorrectTokenizedReferenceEqualsWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("== ", new OperatorToken("=="));
    }
    @Test
    public void checkCorrectTokenizedNotEquals() throws TokenizerException {
        checkTokenizes("!=", new OperatorToken("!="));
    }
    @Test
    public void checkCorrectTokenizedNotEqualsWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" !=", new OperatorToken("!="));
    }
    @Test
    public void checkCorrectTokenizedNotqualsWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("!= ", new OperatorToken("!="));
    }
    @Test
    public void checkCorrectTokenizedPlusEquals() throws TokenizerException  {
        checkTokenizes("+=", new OperatorToken("+="));
    }
    @Test
    public void checkCorrectTokenizedPlusEqualsWhitespaceBefore() throws TokenizerException  {
        checkTokenizes(" +=", new OperatorToken("+="));
    }
    @Test
    public void checkCorrectTokenizedPlusEqualsWhitespaceAfter() throws TokenizerException  {
        checkTokenizes("+= ", new OperatorToken("+="));
    }
    @Test
    public void checkCorrectTokenizedMinusEquals() throws TokenizerException  {
        checkTokenizes("-=", new OperatorToken("-="));
    }
    @Test
    public void checkCorrectTokenizedMinusEqualsWhitespaceBefore() throws TokenizerException  {
        checkTokenizes(" -=", new OperatorToken("-="));
    }
    @Test
    public void checkCorrectTokenizedMinusEqualsWhitespaceAfter() throws TokenizerException  {
        checkTokenizes("-= ", new OperatorToken("-="));
    }
    @Test
    public void checkCorrectTokenizedIncrement() throws TokenizerException {
        checkTokenizes("++", new OperatorToken("++"));
    }
    @Test
    public void checkCorrectTokenizedIncrementWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" ++", new OperatorToken("++"));
    }
    @Test
    public void checkCorrectTokenizedIncrementWhitespaceAfter() throws TokenizerException {
        checkTokenizes("++ ", new OperatorToken("++"));
    }
    @Test
    public void checkCorrectTokenizedDecrement() throws TokenizerException {
        checkTokenizes("--", new OperatorToken("--"));
    }
    @Test
    public void checkCorrectTokenizedDecrementWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" --", new OperatorToken("--"));
    }
    @Test
    public void checkCorrectTokenizedDecrementWhitespaceAfter() throws TokenizerException {
        checkTokenizes("-- ", new OperatorToken("--"));
    }
    @Test
    public void checkCorrectTokenizedDot() throws TokenizerException {
        checkTokenizes(".", new DotToken());
    }
    @Test
    public void checkCorrectTokenizedDotWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" .", new DotToken());
    }
    @Test
    public void checkCorrectTokenizedDotWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes(". ", new DotToken());
    }
    @Test
    public void checkCorrectTokenizedComma() throws TokenizerException {
        checkTokenizes(",", new CommaToken());
    }
    @Test
    public void checkCorrectTokenizedCommaWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" ,", new CommaToken());
    }
    public void checkCorrectTokenizedCommaWhitespaceAfter() throws TokenizerException {
        checkTokenizes(", ", new CommaToken());
    }
    @Test
    public void checkCorrectTokenizedSemiColon() throws TokenizerException {
        checkTokenizes(";", new SemiColonToken());
    }
    @Test
    public void checkCorrectTokenizedSemiColonWithWhitespaceBefore() throws TokenizerException {
        checkTokenizes(" ;", new SemiColonToken());
    }
    @Test
    public void checkCorrectTokenizedSemiColonWithWhitespaceAfter() throws TokenizerException {
        checkTokenizes("; ", new SemiColonToken());
    }
    @Test
    public void checkThrowsExceptionWhitespaceInput() {
        final String testString = " ";
        final Tokenizer testTokenizer = new Tokenizer(testString);
        Assertions.assertThrows(TokenizerException.class,
                testTokenizer::tokenize);
    }
    @Test
    public void checkThrowsExceptionEmptyInput() {
        final String testString = "";
        final Tokenizer testTokenizer = new Tokenizer(testString);
        Assertions.assertThrows(TokenizerException.class,
                testTokenizer::tokenize);
    }
    @Test
    public void checkThrowsExceptionInvalidInput() {
        final String testString = "& |";
        final Tokenizer testTokenizer = new Tokenizer(testString);
        Assertions.assertThrows(TokenizerException.class,
                testTokenizer::tokenize);
    }
}
