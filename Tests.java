import static org.junit.Assert.assertEquals;

import org.junit.Test;

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

}
