import Tokenizer.Tokenizer;
import Parser.Parser;
import Parser.Declarations.Decl;
import Typechecker.Typechecker;
import CodeGenerator.CodeGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

public class ODang {
    public static void main(String[] args) {

        System.out.println("Welcome to ODang");
        System.out.println("Created by: \nCharles Dang, " +
                "Marius Kleppe Larnøy and Giovanni Orozco\n");

        try {
            final Scanner scanner = new Scanner(System.in);
            String inputFileName;

            while (true) {
                System.out.println("Specify file to compile (.odang) > ");
                final String userInput = scanner.nextLine();
                final String[] split = userInput.split("\\.");
                final String extension = split[1];
                if (extension.equalsIgnoreCase("odang")) {
                    inputFileName = userInput;
                    break;
                }
                System.out.println("." + extension + " is not a valid extension, try again\n");
            }
            scanner.close();

            final File file = new File(inputFileName);
            final BufferedReader br = new BufferedReader(new FileReader(file));
            String tokenizerInput = "";
            String line = "";
            while ((line = br.readLine()) != null) {
                tokenizerInput += line;
            }
            br.close();
            final List<Decl> AST = (
                    new Parser(new Tokenizer(tokenizerInput).tokenize()).parseProgram()
                    );
            new Typechecker(AST).typecheckProgram();
            System.out.println(AST);
            final CodeGenerator codeGen = new CodeGenerator(AST);

            //TODO create output file
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
