import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length > 0) {
            System.out.print("Start splitting input ...");
            Files.splitInput(args[0]);
            System.out.println(" OK");
        } else
            System.err.println("usage : java -jar master-shavadoop.jar <input file>");
    }
}
