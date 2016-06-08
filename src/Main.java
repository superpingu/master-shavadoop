import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length == 0) {
            System.err.println("usage : java -jar master-shavadoop.jar <input file>");
            return;
        }
        int i= 0;

        // splitting
        String[] slaves = Distant.getUpMachines("/Users/arnaud/shavadoop/stations");
        System.out.print("Start splitting input ...");
        int lineCnt = Files.splitInput(args[0]);
        System.out.println(" OK");

        // map
        System.out.print("Map lines ...");
        Process[] jobs = new Process[lineCnt];
        HashMap<String, String> mapMachines = new HashMap<>();
        for(i=0; i<lineCnt; i++) {
            jobs[i] = Distant.startDistant(slaves[i % slaves.length], "java -jar slave-shavadoop.jar ~/shavadoop/input" + i);
            mapMachines.put(args[0]+i+"map", slaves[i % slaves.length]);
        }
        for(Process job : jobs) {
            job.waitFor();
        }
        System.out.println(" OK");

        // shuffle
        HashMap<String, ArrayList<String>> keytoMachine = new HashMap<>();
        System.out.print("Shuffling ...");
        i=0;
        for(Process job : jobs) {
            Scanner keys = new Scanner(job.getInputStream());
            while(keys.hasNextLine()) {
                String key = keys.nextLine();
                if(!keytoMachine.containsKey(key))
                    keytoMachine.put(key, new ArrayList<String>());
                keytoMachine.get(key).add(mapMachines.get(args[0] + i + "map"));
            }
            i++;
        }
        System.out.println(" OK");
        System.out.println(keytoMachine);
    }
}
