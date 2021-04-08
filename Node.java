import java.util.concurrent.atomic.AtomicMarkableReference;


public class Node {
  public AtomicMarkableReference<Node> next;
  public int key;
  public boolean dummy;
  public int readableKey;
  final static int DIGIT_COUNT = 8;



  // value nodes assigned true to MSB, when reversed becomes LSB
  public Node(int key, AtomicMarkableReference<Node> next) {
    this.key = key;
    this.next = next;
    this.dummy = false;
    this.readableKey = key;
  }

  public Node(int key) {
    this.key = key;
    this.next = new AtomicMarkableReference<Node>(null, false);
    this.dummy = false;
  }

  public Node(int key, int dummy) {
    this.key = key;
    this.next = new AtomicMarkableReference<Node>(null, false);
    // in the actual implementation....this would be by setting the reversed LSB to
    // 1;
    // we would have to parse the key to tell if its a dummy or not...
    if (dummy == 1)
      this.dummy = true;
    this.readableKey = key;

  }

  public Node(int key, int readableKey, int dummy) {
    this.key = key;
    this.readableKey = readableKey;
    this.next = new AtomicMarkableReference<Node>(null, false);
    // in the actual implementation....this would be by setting the reversed LSB to
    // 1;
    // we would have to parse the key to tell if its a dummy or not...
    if (dummy == 1)
      this.dummy = true;
  }


  public static String makeBinaryString(int intToMake) {
    return String.format("%32s", Integer.toBinaryString(intToMake)).replace(' ', '0');


  }


  public String toString() {
    boolean mark = next.isMarked();
    String k;
    int keyPrint;
    if (this.readableKey != this.key)
      keyPrint = this.readableKey;
    else
      keyPrint = this.key;

    String binaryString = makeBinaryString(this.key) + "  | ";
    if (this.dummy)
      k = "D_" + keyPrint;
    else
      k = "N_" + keyPrint;
    // return "(" + binaryString + k + ", " + mark + ")";
    return "(" + k + ", " + mark + ")";
  }
}
