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
    list.insert(new Node(30, null));
    System.out.println(list.toString());
    System.out.println(list.find(10));
    System.out.println(list.find(20));
    list.insert(new Node(20, null));
    list.insert(new Node(10, null));
    System.out.println(list.toString());
    list.delete(30);
    System.out.println(list.toString());
    list.delete(10);
    System.out.println(list.toString());
    list.delete(20);
    System.out.println(list.toString());
    list.delete(50);
    System.out.println(list.toString());


    System.out.println("Testing SplitOrderedHashMap");
    SplitOrderHashMap hashmap = new SplitOrderHashMap();
    System.out.println(hashmap);
    hashmap.insert(1);
    System.out.println("Inserting One");

    System.out.println(hashmap);
    hashmap.insert(3);
    System.out.println(hashmap);

    System.out.println("Inserting Three");
    hashmap.insert(4);
    System.out.println(hashmap);

    hashmap.insert(2);
    System.out.println("Inserting 2");

    System.out.println(hashmap);
    hashmap.insert(20);
    System.out.println("Inserting 20");

    System.out.println(hashmap);
    hashmap.insert(21);
    System.out.println("Inserting 21");

    System.out.println(hashmap);

  }
}
