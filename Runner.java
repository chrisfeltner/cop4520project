public class Runner {

  /**
   * Main method.
   *
   * @param args Ignored
   */
  public static void main(String[] args) {
    System.out.println("Testing LockFreeList");
    LockFreeList list = new LockFreeList();
    System.out.println(list.toString());
    list.insert(new Node(30, null, false));
    System.out.println(list.toString());
    System.out.println(list.find(10));
    System.out.println(list.find(11));
    list.insert(new Node(11, null, false));
    list.insert(new Node(10, null, false));
    System.out.println(list.toString());
    list.delete(30);
    System.out.println(list.toString());
    list.delete(10);
    System.out.println(list.toString());
    list.delete(11);
    System.out.println(list.toString());
    list.delete(50);
    System.out.println(list.toString());

    System.out.println("Testing SplitOrderedHashMap");
    SplitOrderHashMap hashmap = new SplitOrderHashMap();
    System.out.println(hashmap);
    hashmap.insert(1);
    System.out.println(hashmap);
    hashmap.insert(3);
    System.out.println(hashmap);
    hashmap.insert(4);
    System.out.println(hashmap);
    hashmap.insert(2);
    System.out.println(hashmap);
    hashmap.insert(11);
    System.out.println(hashmap);
    hashmap.insert(21);
    System.out.println(hashmap);

  }
}
