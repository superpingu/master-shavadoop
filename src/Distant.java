import java.io.IOException;
import java.util.ArrayList;

public class Distant {
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
    static String[] getUpMachines(String filename) throws IOException {
        String[] machines = Files.getLines("/Users/arnaud/Programmation/IdeaProjects/master-shavadoop/stations");
        ArrayList<String> machinesUp = new ArrayList<String>();
        for(String machine : machines) {
            System.out.print("Trying machine " + machine + " ... ");
            if (execDistant(machine, "echo ok")) {
                machinesUp.add(machine);
                System.out.println("OK");
            } else
                System.out.println("NO");
        }
        return  machinesUp.toArray(new String[machinesUp.size()]);
    }
}
