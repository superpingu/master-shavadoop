import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Files {
    // read machine names stored in a file, one name per line
    static String[] getLines(String filename) throws IOException {
        Scanner in = new Scanner(new FileReader(filename));
        ArrayList<String> machinesList = new ArrayList<String>();
        while(in.hasNextLine())
            machinesList.add(in.nextLine());
        return machinesList.toArray(new String[machinesList.size()]);
    }
    // save machines names in a file (one name per line)
    static void saveMachines(String filename, String[] machines) throws IOException {
        PrintWriter out = new PrintWriter(filename);
        for(String machine : machines)
            out.println(machine);
        out.close();
    }
    static void splitInput(String inputFilename) throws IOException {
        String[] lines = getLines(inputFilename);
        int i = 0;
        for (String line : lines){
            PrintWriter out = new PrintWriter(inputFilename + (i++));
            out.println(line);
            out.close();
        }
    }
}
