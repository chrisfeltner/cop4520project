public class Runner {

  /**
   * Main method.
   * 
   * @param args Ignored
   */
  public static void main(String[] args) {
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
  }
}
