import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import java.util.Random;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import static org.junit.Assert.assertEquals;

class IndividualExperiment implements Runnable
{
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  List<Integer> data;
  List<Integer> operations;
  SplitOrderHashMap map;

  public IndividualExperiment(SplitOrderHashMap map1, List<Integer> data1, List<Integer> operations1)
  {
    data = data1;
    operations = operations1;
    map = map1;
  }

  public void run()
  {
    if(operations.size() != data.size())
    {
      System.out.println("Bad test formatting");
      return;
    }

    for(int i = 0; i < operations.size(); i++)
    {
      Integer currentOperation = operations.get(i);
      Integer currentValue = data.get(i);

      if(currentOperation == 0)
        this.map.find(currentValue);
      else if (currentOperation == 1)
        this.map.insert(currentValue);
      else if (currentOperation == 2)
        this.map.delete(currentValue);
      else
        System.out.println("WOAH!! ERROR! INVALID OPERATION " + currentOperation);
      }
  }
}




public class Experiments {

  public static void varyingOperationDistribution() throws Exception
  {
    int NUM_OPERATIONS = 100000;
    int MAX_THREADS = 100;
    int MAX_NUM = 16777215;
    try (PrintWriter writer = new PrintWriter(new File("varyingOperationDistribution.csv"))) {
      // initialize results CSV
      StringBuilder sb = new StringBuilder();
      sb.append("num_map");
      sb.append(',');
      sb.append("Name");
      sb.append(',');
      sb.append("Threads");
      sb.append(",");
      sb.append("OperationsPerMilliSecond");
      sb.append('\n');


    SplitOrderHashMap map1 = new SplitOrderHashMap(1);
    SplitOrderHashMap map2 = new SplitOrderHashMap(2);
    SplitOrderHashMap map4 = new SplitOrderHashMap(4);
    SplitOrderHashMap map8 = new SplitOrderHashMap(8);
    SplitOrderHashMap map16 = new SplitOrderHashMap(16);


    ArrayList<SplitOrderHashMap> maps = new ArrayList<>(Arrays.asList(map1, map2, map4, map8, map16));
    //ArrayList<SplitOrderHashMap> maps = new ArrayList<>(Arrays.asList(map2, map4, map8, map16));

    ArrayList<Integer> data1 = new ArrayList<>();
    ArrayList<Integer> operations1 = new ArrayList<>();

    Random rng = new Random();

    for (int j = 0; j < NUM_OPERATIONS; j++) {
      data1.add(rng.nextInt(MAX_NUM));
      int randomOp = rng.nextInt(3);
      System.out.println(randomOp);
      operations1.add(randomOp);
    }
    int mapIndex = 0;
    for (SplitOrderHashMap map : maps) {
      for (int numThreads = 1; numThreads < MAX_THREADS; numThreads++) {

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int start = 0;
        int end = start + (NUM_OPERATIONS / numThreads);
        long startTime;
        long elapsedTime;
        // start stopwatch for experiment
        startTime = System.nanoTime();

        for (int threadNum = 0; threadNum < numThreads; threadNum++) {

          executor.execute(new IndividualExperiment(map, data1.subList(start, end), operations1.subList(start, end)));
          start = end;
          end = end + (int) (NUM_OPERATIONS / numThreads);
          // set end to last index of operations list if last thread
          if (threadNum == numThreads - 2) end = NUM_OPERATIONS;
        }
        // get end time and measure ops / ms
        elapsedTime = System.nanoTime() - startTime;
        double elapsedMilliSeconds = (double) elapsedTime / 1_000_000;
        double opsPerMilliSecond = NUM_OPERATIONS / elapsedMilliSeconds;
        executor.shutdown();
        try
        {
          executor.awaitTermination(3, TimeUnit.SECONDS);
        }
        catch(InterruptedException e)
        {
          executor.shutdownNow();
        }

        sb.append(mapIndex);
        sb.append(',');
        sb.append(map.name);
        sb.append(',');
        sb.append(numThreads);
        sb.append(',');
        sb.append(opsPerMilliSecond);
        sb.append('\n');

      }
      mapIndex++;
    }
      writer.write(sb.toString());
      System.out.println("done with this experiment and printed results to csv!");

    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    varyingOperationDistribution();
  }

}
