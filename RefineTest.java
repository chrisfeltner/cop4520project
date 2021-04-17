import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class RefineTest 
{
  @Test
  public void testcontains1() throws Exception
  {
    // SubTest 1: add a single element and try to contains
    RefinableHashSet<Integer> set1 = new RefinableHashSet<Integer>(4);
    
    // add an element, contains it 
    assertEquals(true, set1.add(2));
  }

  @Test
  public void testcontains2() throws Exception
  {
    // SubTest 2: try to contains an unexisting element
    // should not contains an unexisting element
    RefinableHashSet<Integer> set2 = new RefinableHashSet<Integer>(4);
    set2.add(3);
    set2.add(6);
    set2.add(9);

    assertEquals(false, set2.contains(1));
  }

  @Test
  public void testcontains3() throws Exception
  {
    // SubTest 3: add multiple elements and try to contains them
    RefinableHashSet<Integer> set3 = new RefinableHashSet<Integer>(4);
    set3.add(10);
    set3.add(15);
    set3.add(20);
    set3.add(25);
    set3.add(30);

    // contains the entire list
    assertEquals(true, set3.contains(10));
    assertEquals(true, set3.contains(15));
    assertEquals(true, set3.contains(20));
    assertEquals(true, set3.contains(25));
    assertEquals(true, set3.contains(30));
  }

  @Test
  public void testContains4() throws Exception
  {
    //SubTest 4: try to find on an empty hashSet
    RefinableHashSet<Integer> set4 = new RefinableHashSet<Integer>(4);
    assertEquals(false, set4.contains(2));
  }

  @Test
  public void testcontains5() throws Exception
  {
    // SubTest 2: try to contains an unexisting element
    // should not contains an unexisting element
    RefinableHashSet<Integer> set2 = new RefinableHashSet<Integer>(4);
    
    for(int i = 0; i < 10; i++)
    {
      set2.add(i);
    }

    for(int i = 0; i < 10; i++)
    {
      assertEquals(true, set2.contains(i));
    }
  }

  @Test
  public void testadd1() throws Exception
  {
    // Subtest 1: adding a few elements in order
    RefinableHashSet<Integer> set1 = new RefinableHashSet<Integer>(2);
    set1.add(1);
    set1.add(2);
    set1.add(3);
    set1.add(4);
    
    int expected = 0;
    ArrayList<Integer> expectedValues = new ArrayList<Integer>((Arrays.asList(2, 4, 1, 3)));
    
    // check the traversal is as expected
    for(int i = 0; i < set1.table.length; i++)
    {
      for(int j = 0; j < set1.table[i].size(); j++)
      {
         assertEquals(expectedValues.get(expected), set1.table[i].get(j));
         expected++;
      }
    }

    assertEquals(2, set1.table.length);
    assertEquals(4, set1.setSize);
  }


  @Test
  public void testAdd2() throws Exception
  {
    // Subtest 4: adding duplicate items
    RefinableHashSet<Integer> set4 = new RefinableHashSet<Integer>(2);
    set4.add(4);
    assertEquals(false, set4.add(4));
    set4.add(3);
    assertEquals(false, set4.add(3));
    set4.add(2);
    set4.add(1);
    
    int expected = 0;
    ArrayList<Integer> expectedValues = new ArrayList<Integer>((Arrays.asList(4, 2, 3, 1)));

     // check the traversal is as expected
     for(int i = 0; i < set4.table.length; i++)
     {
       for(int j = 0; j < set4.table[i].size(); j++)
       {
          assertEquals(expectedValues.get(expected), set4.table[i].get(j));
          expected++;
       }
     }
 
     assertEquals(2, set4.table.length);
     assertEquals(4, set4.setSize);
  }

  @Test
  public void testRemove1() throws Exception
  {
    // Subtest 1: remove an element
    RefinableHashSet<Integer> set1 = new RefinableHashSet<Integer>(2);
    set1.add(1);
    set1.add(2);
    set1.add(3);
    set1.add(4);

    set1.remove(4);
    
    for(int i = 1; i < 4; i ++)
    {
      assertEquals(true, set1.contains(i));
    }

    assertEquals(false, set1.contains(4));

    int expected = 0;
    ArrayList<Integer> expectedValues = new ArrayList<Integer>((Arrays.asList(2, 1, 3)));

     // check the traversal is as expected
     for(int i = 0; i < set1.table.length; i++)
     {
       for(int j = 0; j < set1.table[i].size(); j++)
       {
          assertEquals(expectedValues.get(expected), set1.table[i].get(j));
          expected++;
       }
     }
 
     assertEquals(2, set1.table.length);
     assertEquals(3, set1.setSize);
  }

  @Test
  public void testRemove2() throws Exception
  {
    // Subtest 2: remove a couple elements out of order
    RefinableHashSet<Integer> set2 = new RefinableHashSet<Integer>(2);
    set2.add(1);
    set2.add(4);
    set2.add(2);
    set2.add(3);

    set2.remove(4);
    set2.remove(1);
        
    assertEquals(true, set2.contains(2));
    assertEquals(true, set2.contains(3));
    assertEquals(false, set2.contains(1));
    assertEquals(false, set2.contains(4));

    int expected = 0;
    ArrayList<Integer> expectedValues = new ArrayList<Integer>((Arrays.asList(2, 3)));

     // check the traversal is as expected
     for(int i = 0; i < set2.table.length; i++)
     {
       for(int j = 0; j < set2.table[i].size(); j++)
       {
          assertEquals(expectedValues.get(expected), set2.table[i].get(j));
          expected++;
       }
     }
 
     assertEquals(2, set2.table.length);
     assertEquals(2, set2.setSize);
  }

  @Test
  public void testRemove3() throws Exception
  {
    // Subtest 3: remove the whole list
    RefinableHashSet<Integer> set3 = new RefinableHashSet<Integer>(2);
    set3.add(4);
    set3.add(3);
    set3.add(2);
    set3.add(1);

    set3.remove(3);
    set3.remove(2);
    set3.remove(1);
    set3.remove(4);

    assertEquals(false, set3.contains(1));
    assertEquals(false, set3.contains(2));
    assertEquals(false, set3.contains(3));
    assertEquals(false, set3.contains(4));
    
    assertEquals(2, set3.table.length);
    assertEquals(0, set3.setSize);
  }

  @Test
  public void testRemove4() throws Exception
  {
    // Subtest 4: deleting duplicate items
    RefinableHashSet<Integer> set4 = new RefinableHashSet<Integer>(2);
    set4.add(4);
    set4.add(3);
    set4.add(2);
    set4.add(1);

    set4.remove(2);
    assertEquals(false, set4.remove(2));
    set4.remove(1);
    assertEquals(false, set4.remove(1));

    int expected = 0;
    ArrayList<Integer> expectedValues = new ArrayList<Integer>((Arrays.asList(4, 3)));

     // check the traversal is as expected
     for(int i = 0; i < set4.table.length; i++)
     {
       for(int j = 0; j < set4.table[i].size(); j++)
       {
          assertEquals(expectedValues.get(expected), set4.table[i].get(j));
          expected++;
       }
     }

    assertEquals(2, set4.table.length);
    assertEquals(2, set4.setSize);
    
  }

}