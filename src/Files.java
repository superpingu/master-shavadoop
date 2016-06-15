import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Files {
    // hardcoded prefix, not the cleanest way, but after all "simple is beautiful"
    static final String LOCALPREFIX = "/Users/arnaud/shavadoop/";

    /* read lines in a file */
    static String[] getLines(String filename) throws IOException {
        Scanner in;
        if(filename.startsWith("/")) // if the specified path is absolute, use it 'as is'
            in = new Scanner(new FileReader(filename));
        else // otherwise search in LOCALPREFIX directory
            in = new Scanner(new FileReader(LOCALPREFIX+filename));
        ArrayList<String> lines = new ArrayList<String>();
        while(in.hasNextLine())
            lines.add(in.nextLine());
        return lines.toArray(new String[lines.size()]);
    }

    /* save each array entry as a line in a file */
    static void saveLines(String filename, String[] lines) throws IOException {
        PrintWriter out;
        if(filename.startsWith("/"))  // if the specified path is absolute, use it 'as is'
            out = new PrintWriter(filename);
        else // otherwise search in LOCALPREFIX directory
            out = new PrintWriter(LOCALPREFIX+filename);

        for(String line : lines)
            out.println(line);
        out.close();
    }
    /* line based split strategy (split into 1 line chunks) */
    static int splitInput(String inputFilename) throws IOException {
        String[] lines = getLines(inputFilename);
        int i = 0;
        for (String line : lines){
            PrintWriter out = new PrintWriter(LOCALPREFIX+"input" + i++);
            out.println(line);
            out.close();
        }
        return lines.length;
    }
}
