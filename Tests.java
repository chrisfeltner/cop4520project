import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Tests {
  @Test
  public void testFind1() throws Exception
  {
    // SubTest 1: insert a single element and try to find
    SplitOrderHashMap map1 = new SplitOrderHashMap();
    map1.insert(2);
    // insert an element, find it 
    assertEquals(1, map1.find(2));
  }

  @Test
  public void testFind2() throws Exception
  {
    // SubTest 2: try to find an unexisting element
    // should not find an unexisting element
    SplitOrderHashMap map2 = new SplitOrderHashMap();
    map2.insert(3);
    map2.insert(6);
    map2.insert(9);
    assertEquals(0, map2.find(1));
  }

  @Test
  public void testFind3() throws Exception
  {
    // SubTest 3: insert multiple elements and try to find them
    SplitOrderHashMap map3 = new SplitOrderHashMap();
    map3.insert(10);
    map3.insert(15);
    map3.insert(20);
    map3.insert(25);
    map3.insert(30);

    // find the entire list
    assertEquals(1, map3.find(10));
    assertEquals(1, map3.find(15));
    assertEquals(1, map3.find(20));
    assertEquals(1, map3.find(25));
    assertEquals(1, map3.find(30));
  }

  @Test
  public void testFind4() throws Exception
  {
    //SubTest 4: try to find on an empty hashMap
    SplitOrderHashMap map4 = new SplitOrderHashMap();
    assertEquals(0, map4.find(2));
  }

  @Test
  public void testInsert1() throws Exception
  {
    // Subtest 1: inserting a few elements in order
    SplitOrderHashMap map1 = new SplitOrderHashMap();
    map1.insert(1);
    map1.insert(2);
    map1.insert(3);
    map1.insert(4);
    
    // check the traversal is as expected
    Node current1 = map1.lockFreeList.head;
    assertEquals(current1.readableKey, 0);
    assertEquals(current1.dummy, true);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 4);
    assertEquals(current1.dummy, false);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 2);
    assertEquals(current1.dummy, false);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 1);
    assertEquals(current1.dummy, true);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 1);
    assertEquals(current1.dummy, false);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 3);
    assertEquals(current1.dummy, false);
  }

  @Test
  public void testInsert2() throws Exception
  {
    // Subtest 2: inserting a few elements out of order
    SplitOrderHashMap map2 = new SplitOrderHashMap();
    map2.insert(1);
    map2.insert(4);
    map2.insert(2);
    map2.insert(3);
    
    // check the traversal is as expected
    Node current2 = map2.lockFreeList.head;
    assertEquals(current2.readableKey, 0);
    assertEquals(current2.dummy, true);
    current2 = current2.next.getReference();

    assertEquals(current2.readableKey, 4);
    assertEquals(current2.dummy, false);
    current2 = current2.next.getReference();

    assertEquals(current2.readableKey, 2);
    assertEquals(current2.dummy, false);
    current2 = current2.next.getReference();

    assertEquals(current2.readableKey, 1);
    assertEquals(current2.dummy, true);
    current2 = current2.next.getReference();

    assertEquals(current2.readableKey, 1);
    assertEquals(current2.dummy, false);
    current2 = current2.next.getReference();

    assertEquals(current2.readableKey, 3);
    assertEquals(current2.dummy, false);
  }

  @Test
  public void testInsert3() throws Exception
  {
    // Subtest 3: inserting a few elements in decreasing order
    SplitOrderHashMap map3 = new SplitOrderHashMap();
    map3.insert(4);
    map3.insert(3);
    map3.insert(2);
    map3.insert(1);
    
    // check the traversal is as expected
    Node current3 = map3.lockFreeList.head;
    assertEquals(current3.readableKey, 0);
    assertEquals(current3.dummy, true);
    current3 = current3.next.getReference();

    assertEquals(current3.readableKey, 4);
    assertEquals(current3.dummy, false);
    current3 = current3.next.getReference();

    assertEquals(current3.readableKey, 2);
    assertEquals(current3.dummy, false);
    current3 = current3.next.getReference();

    assertEquals(current3.readableKey, 1);
    assertEquals(current3.dummy, true);
    current3 = current3.next.getReference();

    assertEquals(current3.readableKey, 1);
    assertEquals(current3.dummy, false);
    current3 = current3.next.getReference();

    assertEquals(current3.readableKey, 3);
    assertEquals(current3.dummy, false);
  }

  @Test
  public void testInsert4() throws Exception
  {
    // Subtest 4: inserting duplicate items
    SplitOrderHashMap map4 = new SplitOrderHashMap();
    map4.insert(4);
    map4.insert(4);
    map4.insert(3);
    map4.insert(3);
    map4.insert(2);
    map4.insert(1);
    
    
    // check the traversal is as expected
    Node current4 = map4.lockFreeList.head;
    assertEquals(current4.readableKey, 0);
    assertEquals(current4.dummy, true);
    current4 = current4.next.getReference();

    assertEquals(current4.readableKey, 4);
    assertEquals(current4.dummy, false);
    current4 = current4.next.getReference();

    assertEquals(current4.readableKey, 2);
    assertEquals(current4.dummy, false);
    current4 = current4.next.getReference();

    assertEquals(current4.readableKey, 1);
    assertEquals(current4.dummy, true);
    current4 = current4.next.getReference();

    assertEquals(current4.readableKey, 1);
    assertEquals(current4.dummy, false);
    current4 = current4.next.getReference();

    assertEquals(current4.readableKey, 3);
    assertEquals(current4.dummy, false);
  }


  @Test
  public void testDelete1() throws Exception
  {
    // Subtest 1: delete an element
    SplitOrderHashMap map1 = new SplitOrderHashMap();
    map1.insert(1);
    map1.insert(2);
    map1.insert(3);
    map1.insert(4);

    map1.delete(4);
    
    // check the traversal is as expected
    Node current1 = map1.lockFreeList.head;
    assertEquals(current1.readableKey, 0);
    assertEquals(current1.dummy, true);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 2);
    assertEquals(current1.dummy, false);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 1);
    assertEquals(current1.dummy, true);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 1);
    assertEquals(current1.dummy, false);
    current1 = current1.next.getReference();

    assertEquals(current1.readableKey, 3);
    assertEquals(current1.dummy, false);
  }

  @Test
  public void testDelete2() throws Exception
  {
    // Subtest 2: delete a couple elements out of order
    SplitOrderHashMap map2 = new SplitOrderHashMap();
    map2.insert(1);
    map2.insert(4);
    map2.insert(2);
    map2.insert(3);

    map2.delete(4);
    map2.delete(1);
    
    // check the traversal is as expected
    Node current2 = map2.lockFreeList.head;
    assertEquals(current2.readableKey, 0);
    assertEquals(current2.dummy, true);
    current2 = current2.next.getReference();

    assertEquals(current2.readableKey, 2);
    assertEquals(current2.dummy, false);
    current2 = current2.next.getReference();

    assertEquals(current2.readableKey, 1);
    assertEquals(current2.dummy, true);
    current2 = current2.next.getReference();

    assertEquals(current2.readableKey, 3);
    assertEquals(current2.dummy, false);
  }

  @Test
  public void testDelete3() throws Exception
  {
    // Subtest 3: delete the whole list
    SplitOrderHashMap map3 = new SplitOrderHashMap();
    map3.insert(4);
    map3.insert(3);
    map3.insert(2);
    map3.insert(1);

    map3.delete(3);
    map3.delete(2);
    map3.delete(1);
    map3.delete(4);

    // check the traversal is as expected
    Node current3 = map3.lockFreeList.head;
    assertEquals(current3.readableKey, 0);
    assertEquals(current3.dummy, true);
    current3 = current3.next.getReference();

    assertEquals(current3.readableKey, 1);
    assertEquals(current3.dummy, true);
    current3 = current3.next.getReference();
  }

  @Test
  public void testDelete4() throws Exception
  {
    // Subtest 4: deleting duplicate items
    SplitOrderHashMap map4 = new SplitOrderHashMap();
    map4.insert(4);
    map4.insert(3);
    map4.insert(2);
    map4.insert(1);

    map4.delete(2);
    map4.delete(2);
    map4.delete(1);
    map4.delete(1);
    
    // check the traversal is as expected
    Node current4 = map4.lockFreeList.head;
    assertEquals(current4.readableKey, 0);
    assertEquals(current4.dummy, true);
    current4 = current4.next.getReference();

    assertEquals(current4.readableKey, 4);
    assertEquals(current4.dummy, false);
    current4 = current4.next.getReference();

    assertEquals(current4.readableKey, 1);
    assertEquals(current4.dummy, true);
    current4 = current4.next.getReference();

    assertEquals(current4.readableKey, 3);
    assertEquals(current4.dummy, false);
  }

}
