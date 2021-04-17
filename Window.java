public class Window {
    public Node pred, curr;

    /**
     * Create a Window object
     *
     * @param myPred The predecessor node
     * @param myCurr The current node
     */
    public Window(Node myPred, Node myCurr) {
        pred = myPred;
        curr = myCurr;
    }
}