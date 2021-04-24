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

class IndividualExperiment implements Runnable {
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  List<Integer> data;
  List<Integer> operations;
  SplitOrderHashMap<Integer> map;

  public IndividualExperiment(SplitOrderHashMap<Integer> map1, List<Integer> data1, List<Integer> operations1) {
    data = data1;
    operations = operations1;
    map = map1;
  }

  public void run() {
    if (operations.size() != data.size()) {
      System.out.println("Bad test formatting");
      return;
    }

    for (int i = 0; i < operations.size(); i++) {
      Integer currentOperation = operations.get(i);
      Integer currentValue = data.get(i);

      if (currentOperation == FIND)
        this.map.find(currentValue);
      else if (currentOperation == INSERT) {
        this.map.insert(currentValue);
      } else if (currentOperation == DELETE)
        this.map.delete(currentValue);
      else
        System.out.println("WOAH!! ERROR! INVALID OPERATION " + currentOperation);
    }
  }
}

class IndividualExperimentRefine implements Runnable {
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  List<Integer> data;
  List<Integer> operations;
  RefinableHashSet<Integer> rMap;

  public IndividualExperimentRefine(RefinableHashSet<Integer> rMap1, List<Integer> data1, List<Integer> operations1) {
    data = data1;
    operations = operations1;
    rMap = rMap1;
  }

  public void run() {
    if (operations.size() != data.size()) {
      System.out.println("Bad test formatting");
      return;
    }

    for (int i = 0; i < operations.size(); i++) {
      Integer currentOperation = operations.get(i);
      Integer currentValue = data.get(i);

      if (currentOperation == FIND)
        this.rMap.contains(currentValue);
      else if (currentOperation == INSERT)
        this.rMap.add(currentValue);
      else if (currentOperation == DELETE)
        this.rMap.remove(currentValue);
      else
        System.out.println("WOAH!! ERROR! INVALID OPERATION " + currentOperation);
    }
  }
}

public class Experiments {

  /*
   * Duplicating Load Factor Experiment. Appears We only see good results up to
   * small number of threads due to virtualization at larger thread counts.
   * 
   * 
   * Tried 50 on a 14-16 thread capable machine. Lets try up to 12.
   * 
   * 
   */
  public static void uniformOperationDistribution() throws Exception {
    int NUM_OPERATIONS = 100000;
    int MAX_THREADS = 12;
    int MAX_NUM = 16777215;
    try (PrintWriter writer = new PrintWriter(new File("LoadFactorTest.csv"))) {
      // initialize results CSV
      StringBuilder sb = new StringBuilder();
      sb.append("num_map");
      sb.append(',');
      sb.append("Name");
      sb.append(',');
      sb.append("Threads");
      sb.append(",");
      sb.append("OperationsPerMilliSecond");
      sb.append(",");
      sb.append("LogOperationsPerMilliSecond");
      sb.append('\n');

      SplitOrderHashMap<Integer> map1 = new SplitOrderHashMap<Integer>(1);
      SplitOrderHashMap<Integer> map2 = new SplitOrderHashMap<Integer>(2);
      SplitOrderHashMap<Integer> map4 = new SplitOrderHashMap<Integer>(4);
      SplitOrderHashMap<Integer> map8 = new SplitOrderHashMap<Integer>(8);
      SplitOrderHashMap<Integer> map16 = new SplitOrderHashMap<Integer>(16);

      RefinableHashSet<Integer> rMap1 = new RefinableHashSet<>(1);
      RefinableHashSet<Integer> rMap2 = new RefinableHashSet<>(2);
      RefinableHashSet<Integer> rMap4 = new RefinableHashSet<>(4);
      RefinableHashSet<Integer> rMap8 = new RefinableHashSet<>(8);
      RefinableHashSet<Integer> rMap16 = new RefinableHashSet<>(16);

      ArrayList<SplitOrderHashMap<Integer>> maps = new ArrayList<>(Arrays.asList(map1, map2, map4, map8, map16));
      ArrayList<RefinableHashSet<Integer>> rMaps = new ArrayList<>(Arrays.asList(rMap1, rMap2, rMap4, rMap8, rMap16));

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
      for (SplitOrderHashMap<Integer> map : maps) {
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
            if (threadNum == numThreads - 2)
              end = NUM_OPERATIONS;
          }
          // get end time and measure ops / ms
          elapsedTime = System.nanoTime() - startTime;
          double elapsedMilliSeconds = (double) elapsedTime / 1_000_000;
          double opsPerMilliSecond = NUM_OPERATIONS / elapsedMilliSeconds;
          executor.shutdown();
          try {
            executor.awaitTermination(3, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            executor.shutdownNow();
          }

          sb.append(mapIndex);
          sb.append(',');
          sb.append(map.name);
          sb.append(',');
          sb.append(numThreads);
          sb.append(',');
          sb.append(opsPerMilliSecond);
          sb.append(',');
          sb.append(Math.log(opsPerMilliSecond));
          sb.append('\n');

        }
        mapIndex++;
      }

      for (RefinableHashSet<Integer> rMap : rMaps) {
        for (int numThreads = 1; numThreads < MAX_THREADS; numThreads++) {
          ExecutorService executor = Executors.newFixedThreadPool(numThreads);
          int start = 0;
          int end = start + (NUM_OPERATIONS / numThreads);
          long startTime;
          long elapsedTime;
          // start stopwatch for experiment
          startTime = System.nanoTime();

          for (int threadNum = 0; threadNum < numThreads; threadNum++) {

            executor.execute(
                new IndividualExperimentRefine(rMap, data1.subList(start, end), operations1.subList(start, end)));
            start = end;
            end = end + (int) (NUM_OPERATIONS / numThreads);
            // set end to last index of operations list if last thread
            if (threadNum == numThreads - 2)
              end = NUM_OPERATIONS;
          }
          // get end time and measure ops / ms
          elapsedTime = System.nanoTime() - startTime;
          double elapsedMilliSeconds = (double) elapsedTime / 1_000_000;
          double opsPerMilliSecond = NUM_OPERATIONS / elapsedMilliSeconds;
          executor.shutdown();
          try {
            executor.awaitTermination(3, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            executor.shutdownNow();
          }

          sb.append(mapIndex);
          sb.append(',');
          sb.append(rMap.name);
          sb.append(',');
          sb.append(numThreads);
          sb.append(',');
          sb.append(opsPerMilliSecond);
          sb.append(',');
          sb.append(Math.log(opsPerMilliSecond));
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

  public static void varyingOperationDistribution() throws Exception {
    int NUM_OPERATIONS = 100002;

    double percentGet = (1. / 3);
    double percentInsert = (1. / 3);
    double percentRemove = (1. / 3);
    System.out.println("We are doing " + percentGet + "GET OPERATIONS");
    System.out.println("We are doing " + percentInsert + "INSERT OPERATIONS");
    System.out.println("We are doing " + percentRemove + "REMOVE OPERATIONS");

    int MAX_THREADS = 50;
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

      SplitOrderHashMap<Integer> map8 = new SplitOrderHashMap<Integer>(8);
      SplitOrderHashMap<Integer> map16 = new SplitOrderHashMap<Integer>(16);

      RefinableHashSet<Integer> rMap8 = new RefinableHashSet<>(8);
      RefinableHashSet<Integer> rMap16 = new RefinableHashSet<>(16);

      ArrayList<SplitOrderHashMap<Integer>> maps = new ArrayList<>(Arrays.asList(map8, map16));
      ArrayList<RefinableHashSet<Integer>> rMaps = new ArrayList<>(Arrays.asList(rMap8, rMap16));

      // ArrayList<SplitOrderHashMap<Integer>> maps = new
      // ArrayList<>(Arrays.asList(map2, map4, map8, map16));

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
      for (SplitOrderHashMap<Integer> map : maps) {
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
            if (threadNum == numThreads - 2)
              end = NUM_OPERATIONS;
          }
          // get end time and measure ops / ms
          elapsedTime = System.nanoTime() - startTime;
          double elapsedMilliSeconds = (double) elapsedTime / 1_000_000;
          double opsPerMilliSecond = NUM_OPERATIONS / elapsedMilliSeconds;
          executor.shutdown();
          try {
            executor.awaitTermination(3, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
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
      System.out.println("done with this rMap experiment and printed results to csv!");

    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    // use random libraries for uniform distribution of operations
    uniformOperationDistribution();
  }

}