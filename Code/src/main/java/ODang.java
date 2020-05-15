import Tokenizer.*;
import Parser.*;
import Parser.Declarations.Decl;
import Typechecker.*;
import CodeGenerator.*;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class ODang {

    public static void main(String[] args) {

        System.out.println("Welcome to ODang");
        System.out.println("Created by: Charles Dang, " +
                "Marius Kleppe Larnøy and Giovanni Orozco\n");
        String fileName = "";
        while (true) {
            try {
                System.out.println("Enter name of file to compile (<filename>.odang)\n" +
                        "Enter 'quit' to exit");
                Scanner scanner = new Scanner(System.in);
                final String userInput = scanner.nextLine();
                if(userInput.equalsIgnoreCase("quit"))
                    break;
                else {
                    fileName = userInput.split("\\.")[0];
                    final String extension = userInput.split("\\.")[1];
                    if (!extension.equalsIgnoreCase("odang"))
                        break;
                    final String tokenizerInput = readFile(userInput);
                    compile(tokenizerInput);
                    break;
                }
            } catch (final Exception e) {
                System.out.println(e.getClass().getSimpleName() +": " + e.getMessage());
                continue;
            }

        }
        System.out.printf("Compilation complete!\nFile saved as %s.js", fileName);

    }

    public static String readFile(final String fileName) throws IOException {
        final File file = new File(fileName);
        final BufferedReader br = new BufferedReader(new FileReader(file));
        String tokenizerInput = "";
        String line = "";
        while ((line = br.readLine()) != null) {
            tokenizerInput += line;
        }
        br.close();
        return tokenizerInput;
    }
    public static void compile(final String inputString) throws TokenizerException, ParseException,
                                                        IllTypedException, CodeGeneratorException {
        final List<Decl> parsedProgram = new Parser(new Tokenizer(inputString).tokenize()).parseProgram();
        new Typechecker(parsedProgram).typecheckProgram();
        final CodeGenerator codeGen = new CodeGenerator(parsedProgram);
        //TODO output file

    }
}
