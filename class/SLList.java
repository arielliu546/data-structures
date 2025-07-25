public class SLList {
    private static class IntNode {
       public int item;
       public IntNode next;

       public IntNode(int i, IntNode n) {
           item = i;
           next = n;
       }
    }

    private IntNode first;

    public SLList(int i) {
        first = new IntNode(i, null);
    }

    public void addFirst(int i) {
        first = new IntNode(i, first);
    }

    public int getFirst() {
        return first.item;
    }

    public void addLast(int i) {
        IntNode p = first;

        while (p.next != null) {
            p = p.next;
        }

        p.next = new IntNode(i, null);
    }

    private static int size(IntNode p) {
        if (p.next == null) {
            return 1;
        }

        return 1 + size(p.next);
    }

    public int size() {
        /* IntNode p = first;
          int count = 0;
          while(p.next != null) {
              p = p.next;
              count ++;
          }
          return count;
              */
        return size(first);
    }

    public static void main(String[] args) {
        SLList L = new SLList(15);
        L.addFirst(10);
        System.out.print(L.size());
    }
}