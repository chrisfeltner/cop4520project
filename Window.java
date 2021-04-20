public class Window<T> {
  public Node<T> pred;
  public Node<T> curr;

  /**
   * Create a Window object.
   *
   * @param myPred The predecessor node
   * @param myCurr The current node
   */
  public Window(Node<T> myPred, Node<T> myCurr) {
    pred = myPred;
    curr = myCurr;
  }
}