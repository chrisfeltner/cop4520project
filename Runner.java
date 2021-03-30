public class Runner {

  /**
   * Main method.
   *
   * @param args Ignored
   */
public static void main(String[] args) {
    System.out.println("Testing LockFreeList \n");
    LockFreeList list = new LockFreeList();
    var testsPassed = 0;
    var testsFailed = 0;
    System.out.println("-----------------------------------------------------------");
    System.out.println("TEST CASE 1:");
    System.out.println("Adding...");
    System.out.println("7, 8, 9, 10, 13 \n");

    // from the textbook, figure 13.13
    list.add(7);
    list.add(8);
    list.add(9);
    list.add(10);
    list.add(13);
    var testRun = list.toString();
    System.out.println("Output:");
    System.out.println(testRun + '\n');
    System.out.println("Expected:");
    var testCase1 = "(0, false) 0  -> (8, false) 134217729  -> (10, false) 671088641  -> "
        + "(9, false) 1207959553  -> (13, false) 1476395009  -> (7, false) 1879048193  -> "
        + "(2147483646, false) 2147483392  -> NULL";
    System.out.println(testCase1);
    if (testRun.equals(testCase1)) {
      System.out.println("\nTEST PASSED! \n");
      testsPassed++;
    } else {
      System.out.println("\nTEST FAILED! \n");
      testsFailed++;
    }

    System.out.println("-----------------------------------------------------------");
    System.out.println("TEST CASE 2:");
    System.out.println("Adding...");
    System.out.println("7, 8, 9, 10, 13 \n");
    System.out.println("Removing...");
    System.out.println("9, 8, 13 \n");

    list.add(7);
    list.add(8);
    list.add(9);
    list.add(10);
    list.add(13);

    list.remove(9);
    list.remove(8);
    list.remove(13);
    var testRun2 = list.toString();
    System.out.println("Output:");
    System.out.println(testRun2 + '\n');
    System.out.println("Expected:");
    var testCase2 = "(0, false) 0  -> (10, false) 671088641  -> (7, false) 1879048193  -> "
        + "(2147483646, false) 2147483392  -> NULL";
    System.out.println(testCase2);
    if (testRun2.equals(testCase2)) {
      System.out.println("\nTEST PASSED! \n");
      testsPassed++;
    } else {
      System.out.println("\nTEST FAILED! \n");
      testsFailed++;
    }

    System.out.println("-----------------------------------------------------------");
    System.out.println("TEST CASE 3:");
    System.out.println("Adding...");
    System.out.println("6, 5, 2, 10, 3, 9, 8, 4, 1, 7 \n");

    list.add(6);
    list.add(5);
    list.add(2);
    list.add(10);
    list.add(3);
    list.add(9);
    list.add(8);
    list.add(4);
    list.add(1);
    list.add(7);

    var testRun3 = list.toString();
    System.out.println("Output:");
    System.out.println(testRun3 + '\n');
    System.out.println("Expected:");
    var testCase3 = "(0, false) 0  -> (8, false) 134217729  -> (4, false) 268435457  -> " + "(2, false) 536870913  "
        + "-> (10, false) 671088641  -> (6, false) 805306369  -> (1, false) 1073741825  -> " + "(9, false) 1207959553  "
        + "-> (5, false) 1342177281  -> (3, false) 1610612737  -> (7, false) 1879048193  -> " + "(2147483646, false)"
        + " 2147483392  -> NULL";
    System.out.println(testCase3);
    if (testRun3.equals(testCase3)) {
      System.out.println("\nTEST PASSED! \n");
      testsPassed++;
    } else {
      System.out.println("\nTEST FAILED! \n");
      testsFailed++;
    }

    System.out.println("-----------------------------------------------------------");
    System.out.println("TEST CASE 4:");
    System.out.println("Adding...");
    System.out.println("6, 5, 2, 10, 3, 9, 8, 4, 1, 7 \n");

    list.add(6);
    list.add(5);
    list.add(2);
    list.add(10);
    list.add(3);
    list.add(9);
    list.add(8);
    list.add(4);
    list.add(1);
    list.add(7);

    System.out.println("Removing...");
    System.out.println("2, 7, 8, 10, 3 \n");

    list.remove(2);
    list.remove(7);
    list.remove(8);
    list.remove(10);
    list.remove(3);

    var testRun4 = list.toString();
    System.out.println("Output:");
    System.out.println(testRun4 + '\n');
    System.out.println("Expected:");
    var testCase4 = "(0, false) 0  -> (4, false) 268435457" + "  "
        + "-> (6, false) 805306369  -> (1, false) 1073741825  -> " + "(9, false) 1207959553  "
        + "-> (5, false) 1342177281  -> " + "(2147483646, false)" + " 2147483392  -> NULL";
    System.out.println(testCase4);
    if (testRun4.equals(testCase4)) {
      System.out.println("\nTEST PASSED! \n");
      testsPassed++;
    } else {
      System.out.println("\nTEST FAILED! \n");
      testsFailed++;
    }

    System.out.println("PASSED " + testsPassed + "/" + (testsFailed + testsPassed) + " TESTS.");
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
