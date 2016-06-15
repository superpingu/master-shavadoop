import java.io.IOException;
import java.util.ArrayList;

public class Distant {
    // hardcoded prefix, not the cleanest way, but after all "simple is beautiful"
    static final String DISTANTPREFIX = "~/shavadoop/";

    static Process startDistant(String machine, String command) throws IOException {
        String[] cmd = {"sh", "-c", "ssh -o ConnectTimeout=1 abonetti@"+machine+" '"+command+"'"};
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
        pb.redirectErrorStream();
        //start process and retrieve exit code to see if connection succeeded after exit
        return pb.start();
    }
    static boolean execDistant(String name, String command) {
        int exitVal = -1;
        try {
            exitVal = startDistant(name, command).waitFor();
        } catch (Exception e) {}
        return exitVal == 0;
    }

    /* methods abtracting slave start over network */
    static Process slaveMap(String slaveName, String inputFile) throws IOException {
        return startDistant(slaveName, "java -jar slave-shavadoop.jar map "+ DISTANTPREFIX + inputFile);
    }
    static Process slaveShuffle(String slaveName, String key, ArrayList<String> inputs, String output) throws IOException {
        String files = "";
        for(String file : inputs)
            files += " "+ DISTANTPREFIX + file;
        return startDistant(slaveName, "java -jar slave-shavadoop.jar shuffle "+ key + files + " " + DISTANTPREFIX + output);
    }
    static Process slaveReduce(String slaveName, String inputFile) throws IOException {
        return startDistant(slaveName, "java -jar slave-shavadoop.jar reduce "+ DISTANTPREFIX + inputFile);
    }

    static String[] getUpMachines(String filename) throws IOException {
        String[] machines = Files.getLines(filename);
        ArrayList<String> machinesUp = new ArrayList<String>();
        for(String machine : machines) {
            //System.out.print("Trying machine " + machine + " ... ");
            if (execDistant(machine, "echo ok")) {
                machinesUp.add(machine);
                //System.out.println("OK");
            }// else
                //System.out.println("NO");
        }
        return  machinesUp.toArray(new String[machinesUp.size()]);
    }
}
