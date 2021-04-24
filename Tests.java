import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.*;

class IndividualTest implements Runnable {
  public static final int FIND = 0;
  public static final int INSERT = 1;
  public static final int DELETE = 2;

  ArrayList<Integer> data;
  ArrayList<Integer> operations;
  SplitOrderHashMap map;
  ArrayList<Boolean> results;

  public IndividualTest(SplitOrderHashMap map1, ArrayList<Integer> data1, ArrayList<Integer> operations1,
      ArrayList<Boolean> results1) {
    data = data1;
    operations = operations1;
    map = map1;
    results = results1;
  }

  public void run() {
    if (operations.size() != data.size()) {
      System.out.println("Bad test formatting");
      return;
    }

    for (int i = 0; i < operations.size(); i++) {
      Integer currentOperation = operations.get(i);
      Integer currentValue = data.get(i);

      switch (currentOperation) {
      case FIND:
        results.add(this.map.find(currentValue));
        break;
      case INSERT:
        results.add(this.map.insert(currentValue));
        break;
      case DELETE:
        results.add(this.map.delete(currentValue));
        break;
      default:
        System.out.println("WOAH!! ERROR! INVALID OPERATION");
      }
    }
  }
}

public class Tests {
  @Test
  public void testFind1() throws Exception {
    // SubTest 1: insert a single element and try to find
    SplitOrderHashMap<Integer> map1 = new SplitOrderHashMap<Integer>();
    map1.insert(2);
    // insert an element, find it
    assertEquals(true, map1.find(2));
  }

  @Test
  public void testFind2() throws Exception {
    // SubTest 2: try to find an unexisting element
    // should not find an unexisting element
    SplitOrderHashMap<Integer> map2 = new SplitOrderHashMap<Integer>();
    map2.insert(3);
    map2.insert(6);
    map2.insert(9);
    assertEquals(false, map2.find(1));
  }

  @Test
  public void testFind3() throws Exception {
    // SubTest 3: insert multiple elements and try to find them
    SplitOrderHashMap<Integer> map3 = new SplitOrderHashMap<Integer>();
    map3.insert(10);
    map3.insert(15);
    map3.insert(20);
    map3.insert(25);
    map3.insert(30);

    // find the entire list
    assertEquals(true, map3.find(10));
    assertEquals(true, map3.find(15));
    assertEquals(true, map3.find(20));
    assertEquals(true, map3.find(25));
    assertEquals(true, map3.find(30));
  }

  @Test
  public void testFind4() throws Exception {
    // SubTest 4: try to find on an empty hashMap
    SplitOrderHashMap<Integer> map4 = new SplitOrderHashMap<Integer>();
    assertEquals(false, map4.find(2));
  }

  @Test
  public void parallelFind1() throws Exception {
    // SubTest 5: try to find multiple elements that are in the list

    // create a hash map with the elements
    SplitOrderHashMap map = new SplitOrderHashMap();
    map.insert(0);
    map.insert(1);
    map.insert(2);
    map.insert(3);
    map.insert(4);
    map.insert(5);

    // create 2 threads tell one to find the odd indicies,
    // and the other to find the evens
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(1, 3, 5));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(0, 0, 0));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(Arrays.asList(true, true, true));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(0, 2, 4));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(0, 0, 0));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(Arrays.asList(true, true, true));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }
  }

  @Test
  public void parallelFind2() throws Exception {
    // SubTest 5: try to find multiple elements that are not in the list

    // create a hash map with the elements
    SplitOrderHashMap<Integer> map = new SplitOrderHashMap<Integer>();
    map.insert(0);
    map.insert(1);
    map.insert(2);
    map.insert(3);
    map.insert(4);
    map.insert(5);

    // create 2 threads tell them to find elements that are not there
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(11, 13, 15));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(0, 0, 0));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(Arrays.asList(false, false, false));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(10, 12, 14));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(0, 0, 0));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(Arrays.asList(false, false, false));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }
  }

  @Test
  public void parallelFind3() throws Exception {
    // SubTest 5: mixed test, some are in the list and some are not

    // create a hash map with the elements
    SplitOrderHashMap map = new SplitOrderHashMap();
    map.insert(1);
    map.insert(2);
    map.insert(3);
    map.insert(4);
    map.insert(5);

    // create 2 threads tell them to find a few elements
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(1, 27, 3, 0, 5));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(Arrays.asList(true, false, true, false, true));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(0, 2, 14));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(0, 0, 0));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(Arrays.asList(false, true, false));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }
  }

  @Test
  public void testInsert1() throws Exception {
    // Subtest 1: inserting a few elements in order
    SplitOrderHashMap<Integer> map1 = new SplitOrderHashMap<Integer>();
    map1.insert(1);
    map1.insert(2);
    map1.insert(3);
    map1.insert(4);

    // check the traversal is as expected
    Node<Integer> current1 = map1.lockFreeList.head;
    assertEquals(0, current1.bucket);
    assertEquals(true, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(Integer.valueOf(4), current1.data);
    assertEquals(false, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(Integer.valueOf(2), current1.data);
    assertEquals(false, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(1, current1.bucket);
    assertEquals(true, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(Integer.valueOf(1), current1.data);
    assertEquals(false, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(Integer.valueOf(3), current1.data);
    assertEquals(false, current1.dummy);
  }

  @Test
  public void testInsert2() throws Exception {
    // Subtest 2: inserting a few elements out of order
    SplitOrderHashMap<Integer> map2 = new SplitOrderHashMap<Integer>();
    map2.insert(1);
    map2.insert(4);
    map2.insert(2);
    map2.insert(3);

    // check the traversal is as expected
    Node<Integer> current2 = map2.lockFreeList.head;
    assertEquals(0, current2.bucket);
    assertEquals(true, current2.dummy);
    current2 = current2.next.getReference();

    assertEquals(Integer.valueOf(4), current2.data);
    assertEquals(false, current2.dummy);
    current2 = current2.next.getReference();

    assertEquals(Integer.valueOf(2), current2.data);
    assertEquals(false, current2.dummy);
    current2 = current2.next.getReference();

    assertEquals(1, current2.bucket);
    assertEquals(true, current2.dummy);
    current2 = current2.next.getReference();

    assertEquals(Integer.valueOf(1), current2.data);
    assertEquals(false, current2.dummy);
    current2 = current2.next.getReference();

    assertEquals(Integer.valueOf(3), current2.data);
    assertEquals(false, current2.dummy);
  }

  @Test
  public void testInsert3() throws Exception {
    // Subtest 3: inserting a few elements in decreasing order
    SplitOrderHashMap<Integer> map3 = new SplitOrderHashMap<Integer>();
    map3.insert(4);
    map3.insert(3);
    map3.insert(2);
    map3.insert(1);

    // System.out.println(map3.toString());

    // check the traversal is as expected
    Node<Integer> current3 = map3.lockFreeList.head;
    assertEquals(0, current3.bucket);
    assertEquals(true, current3.dummy);
    current3 = current3.next.getReference();

    assertEquals(Integer.valueOf(4), current3.data);
    assertEquals(false, current3.dummy);
    current3 = current3.next.getReference();

    assertEquals(Integer.valueOf(2), current3.data);
    assertEquals(false, current3.dummy);
    current3 = current3.next.getReference();

    assertEquals(1, current3.bucket);
    assertEquals(true, current3.dummy);
    current3 = current3.next.getReference();

    assertEquals(Integer.valueOf(1), current3.data);
    assertEquals(false, current3.dummy);
    current3 = current3.next.getReference();

    assertEquals(Integer.valueOf(3), current3.data);
    assertEquals(false, current3.dummy);
  }

  @Test
  public void testInsert4() throws Exception {
    // Subtest 4: inserting duplicate items
    SplitOrderHashMap<Integer> map4 = new SplitOrderHashMap<Integer>();
    map4.insert(4);
    map4.insert(4);
    map4.insert(3);
    map4.insert(3);
    map4.insert(2);
    map4.insert(1);

    // check the traversal is as expected
    Node<Integer> current4 = map4.lockFreeList.head;
    assertEquals(0, current4.bucket);
    assertEquals(true, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(Integer.valueOf(4), current4.data);
    assertEquals(false, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(Integer.valueOf(2), current4.data);
    assertEquals(false, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(1, current4.bucket);
    assertEquals(true, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(Integer.valueOf(1), current4.data);
    assertEquals(false, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(Integer.valueOf(3), current4.data);
    assertEquals(false, current4.dummy);
  }

  @Test
  public void parallelInsert1() throws Exception {
    // SubTest 5: try to insert multiple elements, no duplicates

    // create a hash map with the elements
    SplitOrderHashMap map = new SplitOrderHashMap();

    // create 2 threads tell them to insert elements that are not there
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(2, 1));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(1, 1));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(Arrays.asList(true, true));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(4, 3));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(1, 1));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(Arrays.asList(true, true));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    // ensure that they all got a success return value
    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }

    for (int i = 1; i < 5; i++) {
      assertEquals(true, map.find(i));
    }
  }

  @Test
  public void parallelInsert2() throws Exception {
    // SubTest 5: try to insert multiple elements, no duplicates

    // create a hash map with the elements
    SplitOrderHashMap map = new SplitOrderHashMap();

    // create 2 threads tell them to insert elements that are not there
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(2, 2, 1, 2, 1));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 1));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(Arrays.asList(true, false, true, false, false));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(4, 3, 3, 4));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(Arrays.asList(true, true, false, false));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    // ensure that they all got a success return value
    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }

    for (int i = 1; i < 5; i++) {
      assertEquals(true, map.find(i));
    }
  }

  @Test
  public void parallelInsert3() throws Exception {
    // SubTest 5: try to insert multiple elements, with some duplicates

    // create a hash map with the elements
    SplitOrderHashMap map = new SplitOrderHashMap();

    // create 2 threads tell them to insert elements
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(200, 50, 10, 50, 900, 200, 77, 35));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(
        Arrays.asList(true, true, true, false, true, false, true, true));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(4, 20, 314, 20, 69, 317, 4, 1225));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(1, 1, 1, 1, 1, 1, 1, 1));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(
        Arrays.asList(true, true, true, false, true, true, false, true));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    // ensure that they all got a success return value
    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }
    System.out.println(map.toString());
    ArrayList<Integer> values = new ArrayList<Integer>(
        Arrays.asList(200, 50, 10, 900, 77, 35, 314, 20, 69, 317, 4, 1225));

    for (int i = 0; i < 12; i++) {
      assertEquals(true, map.find(values.get(i)));
    }
  }

  @Test
  public void testDelete1() throws Exception {
    // Subtest 1: delete an element
    SplitOrderHashMap<Integer> map1 = new SplitOrderHashMap<Integer>();
    map1.insert(1);
    map1.insert(2);
    map1.insert(3);
    map1.insert(4);

    map1.delete(4);

    // check the traversal is as expected
    Node<Integer> current1 = map1.lockFreeList.head;
    assertEquals(0, current1.bucket);
    assertEquals(true, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(Integer.valueOf(2), current1.data);
    assertEquals(false, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(1, current1.bucket);
    assertEquals(true, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(Integer.valueOf(1), current1.data);
    assertEquals(false, current1.dummy);
    current1 = current1.next.getReference();

    assertEquals(Integer.valueOf(3), current1.data);
    assertEquals(false, current1.dummy);
  }

  @Test
  public void testDelete2() throws Exception {
    // Subtest 2: delete a couple elements out of order
    SplitOrderHashMap<Integer> map2 = new SplitOrderHashMap<Integer>();
    map2.insert(1);
    map2.insert(4);
    map2.insert(2);
    map2.insert(3);

    map2.delete(4);
    map2.delete(1);

    // check the traversal is as expected
    Node<Integer> current2 = map2.lockFreeList.head;
    assertEquals(0, current2.bucket);
    assertEquals(true, current2.dummy);
    current2 = current2.next.getReference();

    assertEquals(Integer.valueOf(2), current2.data);
    assertEquals(false, current2.dummy);
    current2 = current2.next.getReference();

    assertEquals(1, current2.bucket);
    assertEquals(true, current2.dummy);
    current2 = current2.next.getReference();

    assertEquals(Integer.valueOf(3), current2.data);
    assertEquals(false, current2.dummy);
  }

  @Test
  public void testDelete3() throws Exception {
    // Subtest 3: delete the whole list
    SplitOrderHashMap<Integer> map3 = new SplitOrderHashMap<Integer>();
    map3.insert(4);
    map3.insert(3);
    map3.insert(2);
    map3.insert(1);

    map3.delete(3);
    map3.delete(2);
    map3.delete(1);
    map3.delete(4);

    // check the traversal is as expected
    Node<Integer> current3 = map3.lockFreeList.head;
    assertEquals(0, current3.bucket);
    assertEquals(true, current3.dummy);
    current3 = current3.next.getReference();

    assertEquals(2, current3.bucket);
    assertEquals(true, current3.dummy);
    current3 = current3.next.getReference();

    assertEquals(1, current3.bucket);
    assertEquals(true, current3.dummy);
    current3 = current3.next.getReference();

    assertEquals(3, current3.bucket);
    assertEquals(true, current3.dummy);
    current3 = current3.next.getReference();
  }

  @Test
  public void testDelete4() throws Exception {
    // Subtest 4: deleting duplicate items
    SplitOrderHashMap<Integer> map4 = new SplitOrderHashMap<Integer>();
    map4.insert(4);
    map4.insert(3);
    map4.insert(2);
    map4.insert(1);

    map4.delete(2);
    map4.delete(2);
    map4.delete(1);
    map4.delete(1);

    // check the traversal is as expected
    Node<Integer> current4 = map4.lockFreeList.head;
    assertEquals(0, current4.bucket);
    assertEquals(true, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(Integer.valueOf(4), current4.data);
    assertEquals(false, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(2, current4.bucket);
    assertEquals(true, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(1, current4.bucket);
    assertEquals(true, current4.dummy);
    current4 = current4.next.getReference();

    assertEquals(Integer.valueOf(3), current4.data);
    assertEquals(false, current4.dummy);
    current4 = current4.next.getReference();
  }

  @Test
  public void parallelDelete1() throws Exception {
    // SubTest 5: try to delete all of the elements in the list

    // create a hash map with the elements
    SplitOrderHashMap map = new SplitOrderHashMap();
    map.insert(1);
    map.insert(2);
    map.insert(3);
    map.insert(4);
    map.insert(5);

    // create 2 threads tell them to delete elements that are there
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(5, 4));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(2, 2));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(Arrays.asList(true, true));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(2, 3, 1));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(2, 2, 2));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(Arrays.asList(true, true, true));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    // ensure that they all got a success return value
    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }

    for (int i = 1; i < 6; i++) {
      assertEquals(false, map.find(i));
    }
  }

  @Test
  public void parallelDelete2() throws Exception {
    // SubTest 5: try to delete all of the elements in the list

    // create a hash map with the elements
    SplitOrderHashMap map = new SplitOrderHashMap();
    map.insert(1);
    map.insert(2);
    map.insert(3);
    map.insert(4);
    map.insert(5);

    // create 2 threads tell them to delete elements that are there
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(4));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(2));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(Arrays.asList(true));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(2, 1));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(2, 2));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(Arrays.asList(true, true));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    // ensure that they all got a success return value
    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }

    for (int i = 1; i < 6; i++) {
      if (i == 3 || i == 5) {
        assertEquals(true, map.find(i));
      }

      else {
        assertEquals(false, map.find(i));
      }
    }
  }

  @Test
  public void parallelDelete3() throws Exception {
    // SubTest 5: try to delete all of the elements in the list

    // create a hash map with the elements
    SplitOrderHashMap map = new SplitOrderHashMap();
    map.insert(1);
    map.insert(2);
    map.insert(3);
    map.insert(4);
    map.insert(5);

    // create 2 threads tell them to delete elements that are there
    ExecutorService executor = Executors.newFixedThreadPool(2);

    ArrayList<Integer> data1 = new ArrayList<Integer>(Arrays.asList(2, 40, 0, 3));
    ArrayList<Integer> operations1 = new ArrayList<Integer>(Arrays.asList(2, 2, 2, 2));
    ArrayList<Boolean> expected1 = new ArrayList<Boolean>(Arrays.asList(true, false, false, true));
    ArrayList<Boolean> results1 = new ArrayList<Boolean>();

    ArrayList<Integer> data2 = new ArrayList<Integer>(Arrays.asList(20, 4, 6, 8));
    ArrayList<Integer> operations2 = new ArrayList<Integer>(Arrays.asList(2, 2, 2, 2));
    ArrayList<Boolean> expected2 = new ArrayList<Boolean>(Arrays.asList(false, true, false, false));
    ArrayList<Boolean> results2 = new ArrayList<Boolean>();

    executor.execute(new IndividualTest(map, data1, operations1, results1));
    executor.execute(new IndividualTest(map, data2, operations2, results2));

    executor.shutdown();
    try {
      executor.awaitTermination(3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      executor.shutdownNow();
    }

    // ensure that they all got the correct return value
    for (int i = 0; i < expected1.size(); i++) {
      assertEquals(expected1.get(i), results1.get(i));
    }

    for (int i = 0; i < expected2.size(); i++) {
      assertEquals(expected2.get(i), results2.get(i));
    }

    for (int i = 1; i < 6; i++) {
      if (i == 1 || i == 5) {
        assertEquals(true, map.find(i));
      }

      else {
        assertEquals(false, map.find(i));
      }
    }
  }

}
