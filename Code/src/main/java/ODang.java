import Tokenizer.Tokenizer;
import Parser.Parser;
import Parser.Declarations.Decl;
import Typechecker.Typechecker;
import CodeGenerator.CodeGenerator;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class ODang {
    public static void main(String[] args) {

        System.out.println("Welcome to ODang");
        System.out.println("Created by: \nCharles Dang, " +
                "Marius Kleppe Larnøy and Giovanni Orozco\n");

        while(true) {
            try {
                final String programString = readFile(new Scanner(System.in));
                if (programString.equalsIgnoreCase("q"))
                    break;

                compile(programString);
                System.out.println("Compilation complete!\nFile saved as <filename>.js");
                break;
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
        }

    }

    public static String readFile(final Scanner scanner) throws IOException, InvalidFileNameException {

        System.out.println("Specify file to compile (<filename>.odang) \nEnter 'q' to exit\n >");
        String inputFileName = scanner.nextLine();

        if (inputFileName.equalsIgnoreCase("q"))
            return inputFileName;
        else {

            final String extension = inputFileName.split("\\.")[1];
            if (!extension.equalsIgnoreCase("odang"))
                throw new InvalidFileNameException("Invalid file extension: " + extension);

            final File file = new File(inputFileName);
            final BufferedReader br = new BufferedReader(new FileReader(file));
            String tokenizerInput = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                tokenizerInput += line;
            }
            br.close();
            scanner.close();
            return tokenizerInput;
        }
    }
    public static void compile(final String input) {
        try {
            final List<Decl> AST = (
                    new Parser(new Tokenizer(input).tokenize()).parseProgram()
            );
            new Typechecker(AST).typecheckProgram();
            final CodeGenerator codeGen = new CodeGenerator(AST);

            //TODO finish generation
        } catch(final Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
