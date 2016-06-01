import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Process startDistant(String machine, String command) throws IOException {
        String[] cmd = {"sh", "-c", "ssh -o ConnectTimeout=1 abonetti@"+machine+" '"+command+"'"};
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
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
    static String[] getUpMachines(String filename) throws IOException {
        String[] machines = getLines("/Users/arnaud/Programmation/IdeaProjects/master-shavadoop/stations");
        ArrayList<String> machinesUp = new ArrayList<String>();
        for(String machine : machines) {
            System.out.print("Trying machine " + machine + " ... ");
            if (execDistant(machine, "echo ok"))
                machinesUp.add(machine);
            else
                System.out.println("NO");
        }
        return  machinesUp.toArray(new String[machinesUp.size()]);
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        String[] machinesUp = getUpMachines("/Users/arnaud/Programmation/IdeaProjects/master-shavadoop/stations");
        Process[] jobs = new Process[machinesUp.length];
        int i=0;
        System.out.println("Starting jobs ...");
        for(String machine : machinesUp) {
            jobs[i++] = startDistant(machine, "java -jar slave-shavadoop.jar");
        }
        System.out.println("Jobs started");
        System.out.println("Waiting for jobs to finish ...");
        for(Process job : jobs) {
            job.waitFor();
        }
        System.out.println("Jobs finished");
    }
}
