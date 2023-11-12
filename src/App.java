import java.io.*;

import java_cup.runtime.ComplexSymbolFactory;
public class App {

  /**
   * Runs the parser on an input file.
   *
   *
   * @param argv the command line, argv[0] is the filename to run
   */
  public static void main(String argv[]) {
      try {
        ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
        Lexer lexer = new Lexer(new FileReader(argv[0]), symbolFactory);
        Parser p = new Parser(lexer, symbolFactory);
        System.out.println(p.parse().value);
        System.out.println("El análisis sintáctico ha finalizado correctamente.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
