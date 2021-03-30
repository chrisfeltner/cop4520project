public class Runner {

  /**
   * Main method.
   *
   * @param args Ignored
   */
  public static void main(String[] args) {
    System.out.println("Testing LockFreeList");
    LockFreeList list = new LockFreeList();
    System.out.println("4:" + list.makeOrdinaryKey(4));
    System.out.println("5:" + list.makeOrdinaryKey(5));
    System.out.println("6:" + list.makeOrdinaryKey(6));
    System.out.println("7:" + list.makeOrdinaryKey(7));
    System.out.println("m:" + list.makeOrdinaryKey(1000));

    // System.out.println("10:" + list.makeOrdinaryKey(10));
    // System.out.println("20:" + list.makeOrdinaryKey(20));

    System.out.println(list.toString());
    // list.add(4);
    // list.add(5);
    // list.add(6);
    // list.add(7);

    // list.add(30);
    System.out.println(list.toString());
    // System.out.println(list.contains(10));
    // System.out.println(list.contains(20));
    // list.add(20);
    // list.add(10);
    // System.out.println(list.toString());
    // list.remove(30);
    // System.out.println(list.toString());
    // list.remove(10);
    // System.out.println(list.toString());
    // list.remove(20);
    // System.out.println(list.toString());
    // list.remove(50);
    // System.out.println(list.toString());

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
