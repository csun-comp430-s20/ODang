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
        String userInput = "";

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("Enter name of file to compile (<filename>.odang)\n" +
                        "Enter 'quit' to exit");

                userInput = scanner.nextLine();
                if(userInput.equalsIgnoreCase("quit"))
                    return;
                else {
                    final String tokenizerInput = readFile(userInput);
                    createFile(userInput, compile(tokenizerInput));

                    break;
                }
            } catch (final Exception e) {
                System.out.println(e.getClass().getSimpleName() +": " + e.getMessage());
                continue;
            }

        }
        System.out.printf("Compilation complete!\nFile saved as %s.js", userInput.split("\\.")[0]);
        scanner.close();
    }

    public static String readFile(final String fileName) throws IOException, InvalidFileNameException {

        final String[] fileNameArray = fileName.split("\\.");
        if (fileNameArray[1] == null || !fileNameArray[1].equalsIgnoreCase("odang"))
            throw new InvalidFileNameException(fileName + " is not a valid filename");

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

    public static void createFile(final String filename, final String compiledCode) throws IOException {
        final File outputFile = new File(filename.split("\\.")[0] + ".js");
        if (outputFile.createNewFile()) {
            final FileWriter fileWriter = new FileWriter(outputFile.getName());
            fileWriter.write(compiledCode);
            fileWriter.close();
        }

    }

    public static String compile(final String inputString) throws TokenizerException, ParseException,
                                                        IllTypedException, CodeGeneratorException {
        final List<Decl> parsedProgram = new Parser(new Tokenizer(inputString).tokenize()).parseProgram();
        new Typechecker(parsedProgram).typecheckProgram();
        final CodeGenerator codeGen = new CodeGenerator(parsedProgram);
        return codeGen.generateProgram();

    }
}
