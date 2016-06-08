import java.io.*;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length == 0) {
            System.err.println("usage : java -jar master-shavadoop.jar <input file>");
            return;
        }

        // splitting
        String[] slaves = Distant.getUpMachines("/Users/arnaud/shavadoop/stations");
        System.out.print("Start splitting input ...");
        int lineCnt = Files.splitInput(args[0]);
        System.out.println(" OK");

        // map
        System.out.print("Map lines ...");
        Process[] jobs = new Process[lineCnt];
        HashMap<String, String> mapMachines = new HashMap<>();
        for(int i=0; i<lineCnt; i++) {
            jobs[i] = Distant.startDistant(slaves[i % slaves.length], "java -jar slave-shavadoop.jar ~/shavadoop/input" + i);
            mapMachines.put(args[0]+i+"map", slaves[i % slaves.length]);
        }
        for(Process job : jobs)
            job.waitFor();

        System.out.println(" OK");
        System.out.println(mapMachines);
    }
}
