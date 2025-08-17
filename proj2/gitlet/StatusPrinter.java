package gitlet;

import java.io.File;

import static gitlet.Utils.join;

public class StatusPrinter {
    public static void print(StageManager sm, BranchManager bm) {
        printBranches(bm);
        printStage(sm);
        printUnstaged();
        printUntracked();
    }

    private static void printBranches(BranchManager bm) {
        System.out.println("=== Branches ===");
        bm.print();
        System.out.println();
    }

    private static void printStage(StageManager sm) {
        System.out.println("=== Staged Files ===");
        for (String file : sm.getStaged()) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String file : sm.getRemoved()) {
            System.out.println(file);
        }
        System.out.println();
    }


    private static void printUnstaged() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
    }

    private static void printUntracked() {
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }
}
