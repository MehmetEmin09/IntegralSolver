package integral;

import integral.ast.Node;
import integral.ast.Printer;
import integral.parser.ParseException;
import integral.parser.Parser;

import java.util.Scanner;

public final class Main {

    private Main() {}

    public static void main(String[] args) {
        System.out.println("Integral Solver (type 'exit' to quit)");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.equals("exit")) {
                break;
            }
            if (line.isEmpty()) {
                continue;
            }
            try {
                Node tree = Parser.parse(line);
                System.out.println("tree:  " + tree);
                System.out.println("print: " + Printer.print(tree));
            } catch (ParseException e) {
                System.out.println("error: " + e.getMessage());
            }
        }
    }
}