
public class Runner {
  /**
   * Main method.
   *
   * @param args Ignored
   */
  public static void main(String[] args) {
    // System.out.println("Testing LockFreeList");
    // LockFreeList list = new LockFreeList();
    // System.out.println(list.toString());
    // int[] bag = new int[]{ 0,10,5 }; 
    // for (int i = 5; i > 0; i--)
    // {
    //   list.insert(i);
    //   System.out.println(list.toString());
    // }


    // list.insert(new Node(30, null));
    // System.out.println(list.toString());
    // System.out.println(list.find(10));
    // System.out.println(list.find(20));
    // list.insert(new Node(20, null));
    // list.insert(new Node(10, null));
    // System.out.println(list.toString());
    // list.delete(30);
    // System.out.println(list.toString());
    // list.delete(10);
    // System.out.println(list.toString());
    // list.delete(20);
    // System.out.println(list.toString());
    // list.delete(50);
    // System.out.println(list.toString());
   

    System.out.println("Testing SplitOrderedHashMap");
    SplitOrderHashMap hashmap = new SplitOrderHashMap();
    System.out.println(hashmap.lockFreeList);
    // int[] bag = new int[]{ 0,1,2,4}; 
    for (int i = 0; i<=20; i+=2)
    {
      hashmap.insert(i);
      // System.out.println("Inserting " + i);
    }
    System.out.println(hashmap.lockFreeList);
    hashmap.insert(15);
    System.out.println(hashmap.lockFreeList);


    // hashmap.insert(1);
    // System.out.println();

    // System.out.println(hashmap);
    // System.out.println("Inserting 3");
    // System.out.println();

    // hashmap.insert(3);
    // System.out.println();

    // System.out.println(hashmap);
    // System.out.println("Inserting 4");
    // System.out.println();

    // hashmap.insert(4);
    // System.out.println();

    // System.out.println(hashmap);
    // System.out.println("Inserting 2");
    // System.out.println();

    // hashmap.insert(2);
    // System.out.println();


    // System.out.println(hashmap);
    // System.out.println("Inserting 20");
    // System.out.println();

    // hashmap.insert(20);
    // System.out.println();

    // System.out.println(hashmap);
    // System.out.println("Inserting 21");
    // System.out.println();

    // hashmap.insert(21);
    // System.out.println();

    // // this should fail because three is already in the hashmap
    // System.out.println(hashmap);
    // System.out.println("Inserting 3");
    // System.out.println();

    // System.out.println(hashmap);

    // System.out.println("Inserting 22");

    // hashmap.insert(22);

    // System.out.println(hashmap);

    // System.out.println("Inserting 19");

    // hashmap.insert(19);

    // System.out.println(hashmap);

  }
}
