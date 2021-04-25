import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;

class IndividualExperimentHashSet implements Runnable
{
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  List<Integer> data;
  List<Integer> operations;
  Set<Integer> map;

  public IndividualExperimentHashSet(Set<Integer> map1, List<Integer> data1, List<Integer> operations1)
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

      if(currentOperation == FIND)
        this.map.contains(currentValue);
      else if (currentOperation == INSERT)
        this.map.add(currentValue);
      else if (currentOperation == DELETE)
        this.map.remove(currentValue);
      else
        System.out.println("WOAH!! ERROR! INVALID OPERATION " + currentOperation);
      }
  }
}


class IndividualExperiment implements Runnable
{
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  List<Integer> data;
  List<Integer> operations;
  SplitOrderHashMap<Integer> map;

  public IndividualExperiment(SplitOrderHashMap<Integer> map1, List<Integer> data1, List<Integer> operations1)
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

      if(currentOperation == FIND)
        this.map.contains(currentValue);
      else if (currentOperation == INSERT)
        this.map.insert(currentValue);
      else if (currentOperation == DELETE)
        this.map.delete(currentValue);
      else
        System.out.println("WOAH!! ERROR! INVALID OPERATION " + currentOperation);
    }
  }
}

class IndividualExperimentConcurrentHashMap implements Runnable
{
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  List<Integer> data;
  List<Integer> operations;
  ConcurrentHashMap<Integer, Integer> map;

  public IndividualExperimentConcurrentHashMap(ConcurrentHashMap<Integer, Integer> map1, List<Integer> data1, List<Integer> operations1)
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

      if(currentOperation == FIND)
        this.map.containsKey(currentValue);
      else if (currentOperation == INSERT)
        this.map.put(currentValue, currentValue);
      else if (currentOperation == DELETE)
        this.map.remove(currentValue);
      else
        System.out.println("WOAH!! ERROR! INVALID OPERATION " + currentOperation);
    }
  }
}

class IndividualExperimentConcurrentSkipListSet implements Runnable
{
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  List<Integer> data;
  List<Integer> operations;
  ConcurrentSkipListSet<Integer> map;

  public IndividualExperimentConcurrentSkipListSet(ConcurrentSkipListSet<Integer> map1, List<Integer> data1, List<Integer> operations1)
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

      if(currentOperation == FIND)
        this.map.contains(currentValue);
      else if (currentOperation == INSERT)
        this.map.add(currentValue);
      else if (currentOperation == DELETE)
        this.map.remove(currentValue);
      else
        System.out.println("WOAH!! ERROR! INVALID OPERATION " + currentOperation);
    }
  }
}


class IndividualExperimentRefine implements Runnable
{
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  List<Integer> data;
  List<Integer> operations;
  RefinableHashSet<Integer> rMap;

  public IndividualExperimentRefine(RefinableHashSet<Integer> rMap1, List<Integer> data1, List<Integer> operations1)
  {
    data = data1;
    operations = operations1;
    rMap = rMap1;
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

      if(currentOperation == FIND)
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
    Duplicating Load Factor Experiment. Appears We only see good results up to small number of threads due to
    virtualization at larger thread counts.


    Tried 50 on a 14-16 thread capable machine. Lets try up to 12.


   */
  public static void uniformOperationDistribution(int NUM_OPERATIONS, int MAX_THREADS, int MIN_NUM, int MAX_NUM) throws Exception
  {

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
      SplitOrderHashMap<Integer> map32 = new SplitOrderHashMap<Integer>(32);

      RefinableHashSet<Integer> rMap1= new RefinableHashSet<>(1);
      RefinableHashSet<Integer> rMap2= new RefinableHashSet<>(2);
      RefinableHashSet<Integer> rMap4= new RefinableHashSet<>(4);
      RefinableHashSet<Integer> rMap8= new RefinableHashSet<>(8);
      RefinableHashSet<Integer> rMap16 = new RefinableHashSet<>(16);
      RefinableHashSet<Integer> rMap32 = new RefinableHashSet<>(32);

      ConcurrentHashMap<Integer, Integer> cMap16 = new ConcurrentHashMap<>(16, 2);
      ConcurrentSkipListSet<Integer> csMap1 = new ConcurrentSkipListSet<Integer>();

      ArrayList<SplitOrderHashMap<Integer>> maps = new ArrayList<>(Arrays.asList(map1, map2, map4, map8, map16, map32));
      ArrayList<RefinableHashSet<Integer>> rMaps = new ArrayList<>(Arrays.asList(rMap1, rMap2, rMap4, rMap8, rMap16, rMap32));
      ArrayList<ConcurrentHashMap<Integer, Integer>> cMaps = new ArrayList<>(Arrays.asList(cMap16));
      ArrayList<ConcurrentSkipListSet<Integer>> csMaps = new ArrayList<>(Arrays.asList(csMap1));


      ArrayList<Integer> data1 = new ArrayList<>();
      ArrayList<Integer> operations1 = new ArrayList<>();

      Random rng = new Random();

      for (int j = 0; j < NUM_OPERATIONS; j++) {
        data1.add(rng.nextInt(MAX_NUM - MIN_NUM + 1) + MIN_NUM);
        int randomOp = rng.nextInt(3);
        //System.out.println(randomOp);
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

            executor.execute(new IndividualExperimentRefine(rMap, data1.subList(start, end), operations1.subList(start, end)));
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

      boolean concHashSet = false;
      if (concHashSet) {
        for (ConcurrentHashMap<Integer, Integer> cMap : cMaps) {
          for (int numThreads = 1; numThreads < MAX_THREADS; numThreads++) {
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            int start = 0;
            int end = start + (NUM_OPERATIONS / numThreads);
            long startTime;
            long elapsedTime;
            // start stopwatch for experiment
            startTime = System.nanoTime();

            for (int threadNum = 0; threadNum < numThreads; threadNum++) {

              executor.execute(new IndividualExperimentConcurrentHashMap(cMap, data1.subList(start, end), operations1.subList(start, end)));
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
            String ss = "concHashMap";
            sb.append(ss);
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
      }

      boolean concSkipListSet = true;
      if (concSkipListSet) {
        for (ConcurrentSkipListSet<Integer> csMap : csMaps) {
          for (int numThreads = 1; numThreads < MAX_THREADS; numThreads++) {
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            int start = 0;
            int end = start + (NUM_OPERATIONS / numThreads);
            long startTime;
            long elapsedTime;
            // start stopwatch for experiment
            startTime = System.nanoTime();

            for (int threadNum = 0; threadNum < numThreads; threadNum++) {

              executor.execute(new IndividualExperimentConcurrentSkipListSet(csMap, data1.subList(start, end), operations1.subList(start, end)));
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
            String ss = "concSkipListSet";
            sb.append(ss);
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
      }



        writer.write(sb.toString());
        System.out.println("done with this experiment and printed results to csv!");

    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }




  public static void varyingOperationDistribution(int NUM_REPEATS, int NUM_OPERATIONS, int MAX_THREADS, int MIN_NUM, int MAX_NUM,
                                                  double percentGet, double percentInsert, double percentRemove, String csvName) throws Exception
  {

    System.out.println("We are doing " + percentGet + "GET OPERATIONS");
    System.out.println("We are doing " + percentInsert + "INSERT OPERATIONS");
    System.out.println("We are doing " + percentRemove + "REMOVE OPERATIONS");

    try (PrintWriter writer = new PrintWriter(new File(csvName))) {
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
      int mapIndex = 0;
      int repeat = 0;

      while (repeat < NUM_REPEATS)
      {
        SplitOrderHashMap<Integer> map16 = new SplitOrderHashMap<Integer>(8);

        RefinableHashSet<Integer> rMap16 = new RefinableHashSet<>(16);

        ConcurrentHashMap<Integer, Integer> cMap16 = new ConcurrentHashMap<>(16, 2);

        ConcurrentSkipListSet<Integer> csMap1 = new ConcurrentSkipListSet<Integer>();

        Set<Integer> shMap = Collections.synchronizedSet(new HashSet<Integer>());

        ArrayList<SplitOrderHashMap<Integer>> maps = new ArrayList<>(Arrays.asList(map16));
        ArrayList<RefinableHashSet<Integer>> rMaps = new ArrayList<>(Arrays.asList(rMap16));
        ArrayList<ConcurrentHashMap<Integer, Integer>> cMaps = new ArrayList<>(Arrays.asList(cMap16));
        ArrayList<ConcurrentSkipListSet<Integer>> csMaps = new ArrayList<>(Arrays.asList(csMap1));
        ArrayList<Set<Integer>> shMaps = new ArrayList<>(Arrays.asList(shMap));

        //ArrayList<SplitOrderHashMap> maps = new ArrayList<>(Arrays.asList(map2, map4, map8, map16));

        ArrayList<Integer> data1 = new ArrayList<>();
        ArrayList<Integer> operations1 = new ArrayList<>();

        Random rng = new Random();

        for (int j = 0; j < NUM_OPERATIONS; j++) {
          data1.add(rng.nextInt(MAX_NUM - MIN_NUM + 1) + MIN_NUM);
          double nextDouble = rng.nextDouble();
          int randomOp;
          // factor in distribution of operations defined above when picking ops
          if (nextDouble < percentGet) {
            randomOp = 0;
          } else if (nextDouble <= percentGet + percentInsert) {
            randomOp = 1;
          } else {
            randomOp = 2;
          }
          //System.out.println(randomOp);
          operations1.add(randomOp);
        }
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
              if (threadNum == numThreads - 2) end = NUM_OPERATIONS;
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

              executor.execute(new IndividualExperimentRefine(rMap, data1.subList(start, end), operations1.subList(start, end)));
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

        boolean concHashSet = true;
        if (concHashSet) {
          for (ConcurrentHashMap<Integer, Integer> cMap : cMaps) {
            for (int numThreads = 1; numThreads < MAX_THREADS; numThreads++) {
              ExecutorService executor = Executors.newFixedThreadPool(numThreads);
              int start = 0;
              int end = start + (NUM_OPERATIONS / numThreads);
              long startTime;
              long elapsedTime;
              // start stopwatch for experiment
              startTime = System.nanoTime();

              for (int threadNum = 0; threadNum < numThreads; threadNum++) {

                executor.execute(new IndividualExperimentConcurrentHashMap(cMap, data1.subList(start, end), operations1.subList(start, end)));
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
              try {
                executor.awaitTermination(3, TimeUnit.SECONDS);
              } catch (InterruptedException e) {
                executor.shutdownNow();
              }
              sb.append(mapIndex);
              sb.append(',');
              String ss = "concHashMap";
              sb.append(ss);
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
        }

        boolean concSkipListSet = true;
        if (concSkipListSet) {
          for (ConcurrentSkipListSet<Integer> csMap : csMaps) {
            for (int numThreads = 1; numThreads < MAX_THREADS; numThreads++) {
              ExecutorService executor = Executors.newFixedThreadPool(numThreads);
              int start = 0;
              int end = start + (NUM_OPERATIONS / numThreads);
              long startTime;
              long elapsedTime;
              // start stopwatch for experiment
              startTime = System.nanoTime();

              for (int threadNum = 0; threadNum < numThreads; threadNum++) {

                executor.execute(new IndividualExperimentConcurrentSkipListSet(csMap, data1.subList(start, end), operations1.subList(start, end)));
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
              try {
                executor.awaitTermination(3, TimeUnit.SECONDS);
              } catch (InterruptedException e) {
                executor.shutdownNow();
              }
              sb.append(mapIndex);
              sb.append(',');
              String ss = "concSkipListSet";
              sb.append(ss);
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
        }

        boolean syncHash = true;
        if (syncHash) {
          for (Set<Integer> sMap : shMaps) {
            for (int numThreads = 1; numThreads < MAX_THREADS; numThreads++) {
              ExecutorService executor = Executors.newFixedThreadPool(numThreads);
              int start = 0;
              int end = start + (NUM_OPERATIONS / numThreads);
              long startTime;
              long elapsedTime;
              // start stopwatch for experiment
              startTime = System.nanoTime();

              for (int threadNum = 0; threadNum < numThreads; threadNum++) {

                executor.execute(new IndividualExperimentHashSet(sMap, data1.subList(start, end), operations1.subList(start, end)));
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
              try {
                executor.awaitTermination(3, TimeUnit.SECONDS);
              } catch (InterruptedException e) {
                executor.shutdownNow();
              }
              sb.append(mapIndex);
              sb.append(',');
              String ss = "syncHashSet";
              sb.append(ss);
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
        }
        writer.write(sb.toString());
        System.out.println("done with this experiment and printed results to csv!");
        repeat += 1;
      }

    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    }
  }

  public static void main(String[] args) throws Exception {
    // use random libraries for uniform distribution of operations
    uniformOperationDistribution(100000, 32, 12000, 12222);
    varyingOperationDistribution(10,100000, 32,
            54300, 54400,
            (1./3), (1./3), (1./3), "thirdsDist.csv");

    varyingOperationDistribution(10,100000, 32,
            54300, 54430,
            (1./5), (2./5), (2./5), "20C-40A-40R.csv");

    varyingOperationDistribution(10,100000, 32,
            54300, 54430,
            (1./10), (45./100), (45./100), "10C-45A-45R.csv");

    varyingOperationDistribution(10,100000, 32,
            54300, 54430,
            (2./100), (49./100), (49./100), "2C-49A-49R.csv");

    varyingOperationDistribution(10,100000, 32,
            54300, 54430,
            (2./5), (2./5), (1./5), "40C-40A-20R.csv");

    varyingOperationDistribution(10,100000, 32,
            54300, 54430,
            (1./5), (3./5), (1./5), "20C-60A-20R.csv");

    varyingOperationDistribution(10,100000, 32,
            54300, 54430,
            (1./10), (4./5), (1./10), "10C-80A-10R.csv");
  }

}
