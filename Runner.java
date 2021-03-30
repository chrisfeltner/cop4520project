public class Runner {

  /**
   * Main method.
   *
   * @param args Ignored
   */
  public static void main(String[] args) {
    System.out.println("Testing LockFreeList");
    LockFreeList list = new LockFreeList();
  
    for (int i = 1; i < 10; i++) {
      // System.out.println(i + " : " + list.makeOrdinaryKey(i));
      list.add(i);
      System.out.println(list.toString());
    }

    System.out.println(list.toString());


    // System.out.println("Testing SplitOrderedHashMap");
    // SplitOrderHashMap hashmap = new SplitOrderHashMap();
    // System.out.println(hashmap);
    // hashmap.insert(1);
    // System.out.println(hashmap);
    // hashmap.insert(3);
    // System.out.println(hashmap);
    // hashmap.insert(4);
    // System.out.println(hashmap);
    // hashmap.insert(2);
    // System.out.println(hashmap);
    // hashmap.insert(20);
    // System.out.println(hashmap);
    // hashmap.insert(21);
    // System.out.println(hashmap);

  }
}
