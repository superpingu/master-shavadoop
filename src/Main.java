import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    static String[] slaves;
    static HashMap<String, ArrayList<String>> keyDispatch;

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length == 0) {
            System.err.println("usage : java -jar master-shavadoop.jar <input file>");
            return;
        }
        // search for available machines
        long startTime = System.currentTimeMillis();
        System.out.print("Scanning slaves ...");
        slaves = Distant.getUpMachines("stations");
        System.out.println(" OK (" + slaves.length + " found in " + (System.currentTimeMillis() - startTime) + " ms)");

        // split input (line based split strategy : split into 1 line chunks)
        startTime = System.currentTimeMillis();
        System.out.print("Splitting input ...");
        int lineCnt = Files.splitInput(args[0]);
        System.out.println(" OK (" + (System.currentTimeMillis() - startTime) + " ms)");

        // map
        startTime = System.currentTimeMillis();
        System.out.print("Mapping  ...");
        map(lineCnt);
        System.out.println(" OK (" + (System.currentTimeMillis() - startTime) + " ms)");

        // shuffle
        startTime = System.currentTimeMillis();
        System.out.print("Shuffling ...");
        shuffle();
        System.out.println(" OK (" + (System.currentTimeMillis() - startTime) + " ms)");

        //reduce
        startTime = System.currentTimeMillis();
        System.out.print("Reducing ...");
        reduce();
        System.out.println(" OK (" + (System.currentTimeMillis() - startTime) + " ms)");

        startTime = System.currentTimeMillis();
        System.out.print("Merging keys ...");
        String[] output = new String[keyDispatch.keySet().size()];
        for(int j=0; j<keyDispatch.keySet().size(); j++) {
            String[] reduce = Files.getLines("output" + j + "reduced");
            output[j] = reduce[0];
        }
        Files.saveLines("result", output);
        System.out.println(" OK (" + (System.currentTimeMillis() - startTime) + " ms)");
        System.out.println("Done. Output written in 'result'");
    }

    /* do a distributed mapping. Each slave may receive more than one task, thus allowing to take advantage
     * of the multiple core architecture */
    static void map(int lineCount) throws InterruptedException, IOException {
        ArrayList<Process> jobs = new ArrayList<>();
        keyDispatch = new HashMap<>();
        //start maps
        for(int i=0; i<lineCount; i++)
            jobs.add(Distant.slaveMap(slaves[i % slaves.length], "input" + i));

        // wait for completion and retrieve keys processed in each output file
        for(int i=0; i<lineCount; i++) {
            jobs.get(i).waitFor();
            // retrieve keys after mapping
            Scanner keys = new Scanner(jobs.get(i).getInputStream());
            while(keys.hasNextLine()) {
                String key = keys.nextLine();
                if(key.equals(""))
                    continue;
                if(!keyDispatch.containsKey(key))
                    keyDispatch.put(key, new ArrayList<String>());
                keyDispatch.get(key).add("input" + i + "map");
            }
        }
    }
    /* shuffle the result of the map */
    static void shuffle() throws InterruptedException, IOException {
        ArrayList<Process> jobs = new ArrayList<>();
        int i=0;
        for(String key : keyDispatch.keySet())
            jobs.add(Distant.slaveShuffle(slaves[i % slaves.length], key, keyDispatch.get(key), "output" + i++));
        // wait for completion
        for(Process job : jobs)
            job.waitFor();
    }
    /* reduce the shuffled outputs */
    static void reduce() throws InterruptedException, IOException {
        ArrayList<Process> jobs = new ArrayList<>();
        // reduce each output file (one file per key)
        for(int i=0; i<keyDispatch.keySet().size(); i++)
            jobs.add(Distant.slaveReduce(slaves[i % slaves.length], "output" + i));
        // wait for completion
        for(Process job : jobs)
            job.waitFor();
    }
}