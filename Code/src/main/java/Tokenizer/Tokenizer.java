package Tokenizer;

import Tokenizer.Tokens.*;
import java.util.List;
import java.util.ArrayList;

public class Tokenizer {

    private final char[] input;
    private int inputPos;

    public Tokenizer(final char[] input) {
        this.input = input;
        inputPos = 0;
    }

    public Tokenizer(String tokens) {
        this(tokens.toCharArray());
    }

    /**
     * tries to tokenize a non-letter/non-digit symbol
     * @return Token or null
     */
    private Token tryTokenizeNonLetterOrDigitSymbol() {
        String symbols = "";

        //regex containing the valid operators in the language
        if(validPosition() &&
                String.valueOf(input[inputPos]).matches("[;.(){}+=\\-*/!%><&|]")) {

            symbols += input[inputPos];
            inputPos++;

            switch (symbols) {
                case "(":
                    return new LeftParenToken();
                case ")":
                    return new RightParenToken();
                case "{":
                    return new LeftCurlyToken();
                case "}":
                    return new RightCurlyToken();
                case ";":
                    return new SemiColonToken();
                case ".":
                    return new DotToken();
                case "&":
                    if (validPosition() && input[inputPos] == '&') {
                        inputPos++;
                        return new OperatorToken("&&");
                    } else return null;
                case "|":
                    if (validPosition() && input[inputPos] == '|') {
                        inputPos++;
                        return new OperatorToken("||");
                    } else return null;
                case "!":
                    if (validPosition() && input[inputPos] == '=') {
                        inputPos++;
                        return new OperatorToken("!=");
                    }
                case "<":
                    if (validPosition() && input[inputPos] == '=') {
                        inputPos++;
                        return new OperatorToken("<=");
                    }
                case ">":
                    if (validPosition() && input[inputPos] == '=') {
                        inputPos++;
                        return new OperatorToken(">=");
                    }
                case "=":
                    if (validPosition() && input[inputPos] == '=') {
                        inputPos++;
                        return new OperatorToken("==");
                    }
                    //special case for possible negative numbers
                case "-":
                    if (validPosition() && Character.isDigit(input[inputPos])) {
                        inputPos--;
                        return tryTokenizeInt();
                    } else if (validPosition() && input[inputPos] == '-') {
                        inputPos++;
                        return new OperatorToken("--");
                    }
                case "+":
                    if (validPosition() && input[inputPos] == '+') {
                        inputPos++;
                        return new OperatorToken("++");
                    }
                default:
                    return new OperatorToken(symbols);
            }
        } else {
            return null;
        }
    }

    /**
     * tries to tokenize a String, starting and ending with a double quote
     * @return StringToken or null
     */
    private StringToken tryTokenizeString() {
        String string = "";

        if (validPosition() && input[inputPos] == '"') {
            inputPos++;

            while (validPosition() && input[inputPos] != '"') {
                string += input[inputPos];
                inputPos++;
            }
            inputPos++;
            return new StringToken(string);
        } else
            return null;
    }
    /**
     * @return Token or null
     */
    private Token tryTokenizeReservedWordOrIdentifier() {
        String letters = "";

        if(validPosition() && Character.isLetter(input[inputPos])) {
            letters += input[inputPos];
            inputPos++;
            while (validPosition() && Character.isLetterOrDigit(input[inputPos])) {
                letters += input[inputPos];
                inputPos++;
            }
            switch (letters) {
                case "if":
                    return new IfToken();
                case "for":
                    return new ForToken();
                case "new":
                    return new NewToken();
                case "else":
                    return new ElseToken();
                case "while":
                    return new WhileToken();
                case "return":
                    return new ReturnToken();
                case "class":
                    return new ClassToken();
                case "break":
                    return new BreakToken();
                case "extends":
                    return new ExtendsToken();
                case "String":
                    return new StringTypeToken();
                case "this":
                    return new ThisToken();
                case "char":
                    return new CharTypeToken();
                case "int":
                    return new IntTypeToken();
                case "boolean":
                    return new BooleanTypeToken();
                case "println":
                    return new PrintlnToken();
                case "void":
                    return new VoidToken();
                case "true":
                    return new BooleanToken(true);
                case "false":
                    return new BooleanToken(false);
                default:
                    return new IdentifierToken(letters);
            }

        } else {
            return null;
        }
    }

    /**
     * Tries to tokenize a char sequence as an integer
     * @return IntegerToken or null
     */
    private IntegerToken tryTokenizeInt() {
        String digits = "";

        //special case for negative numbers
        if (validPosition() && input[inputPos] == '-') {
            digits += input[inputPos];
            inputPos++;
        }
        while (validPosition() && Character.isDigit(input[inputPos])) {
            digits += input[inputPos];
            inputPos++;
        }

        if (digits.length() > 0) {
            return new IntegerToken(Integer.parseInt(digits));
        } else {
            return null;
        }

    }

    /**
     * tries to create a token
     * @return valid token
     * @throws TokenizerException if no valid token is found
     */
    private Token createToken() throws TokenizerException {
        Token token = tryTokenizeNonLetterOrDigitSymbol();

        if (token == null) {
            token = tryTokenizeReservedWordOrIdentifier();
        }
        if (token == null) {
            token = tryTokenizeString();
        }
        if (token == null) {
            token = tryTokenizeInt();
        }
        if (token == null) {
            throw new TokenizerException("Not a valid token");
        }
        return token;
    }

    /**
     * skips whitespace in the input
     */
    private void skipWhitespace() {
        while (validPosition() &&
                Character.isWhitespace(input[inputPos])) {
            inputPos++;
        }
    }

    /**
     * Checks the current position in the array, done a lot in this code
     * so wrote a method for it
     * @return true or false depending on current position on the list
     */
    private boolean validPosition() {
        return inputPos < input.length;
    }

    /**
     * tokenizes a string input
     * @return List<Token> if successful
     * @throws TokenizerException if invalid input
     */
    public List<Token> tokenize() throws TokenizerException {
        List<Token> tokens = new ArrayList<Token>();
        while(validPosition()) {
            skipWhitespace();
            if(validPosition()) {
                tokens.add(createToken());
            }
        }
        if (tokens.isEmpty())
            throw new TokenizerException("Empty Input");
        else return tokens;
    }

    //main for testing purposes
    public static void main(String[] args) {
        String testString = " ";
        Tokenizer testTokenizer = new Tokenizer(testString.toCharArray());
        try {
            List<Token> result = testTokenizer.tokenize();
            System.out.println(result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
