package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.TreeMap;

import static gitlet.Utils.*;

public class BranchManager implements Serializable {
    private String HEAD;
    private TreeMap<String, String> branches;
    private final File branchf;


    public BranchManager(File gitDir) {
        branchf = join(gitDir, "branches");
        branches = new TreeMap<>();
        HEAD = "master";
        branches.put(HEAD, null);
    }

    public void createNewBranch(String name) {
        if (branches.containsKey(name)) {
            message("A branch with that name already exists.");
            System.exit(0);
        }
        branches.put(name, getCurrentHash());
    }

    public void remove(String name) {
        if (name.equals(HEAD)) {
            message("Cannot remove the current branch.");
            System.exit(0);
        }
        if (branches.remove(name) == null) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
    }

    public String getHEAD() {
        return HEAD;
    }

    public void switchTo(String branchName) {
        if (!branches.containsKey(branchName)) {
            message("No such branch exists.");
            System.exit(0);
        }
        HEAD = branchName;
    }

    public void move(String toHash) {
        branches.put(HEAD, toHash);
    }

    public String getCurrentHash() {
        return branches.get(HEAD);
    }

    public String getCommitHash(String name) {
        return branches.get(name);
    }

    public void print() {
        for (String name : branches.keySet()) {
            if (name.equals(HEAD)) {
                System.out.print("*");
            }
            System.out.println(name);
        }
    }

    /** find the split point of the current branch and the given branch.
     * */
    public Commit getSplitPoint(String branchName) {
        HashSet<Commit> headParents = new HashSet<>();
        Commit currentCommit = StorageManager.getCommitFromHash(getCurrentHash());
        Commit temp = currentCommit;
        Commit s = null;
        while (temp.getParent() != null) {
            Commit p = temp.getParent();
            headParents.add(p);
        }
        Commit givenCommit = StorageManager.getCommitFromHash(getCommitHash(branchName));
        temp = givenCommit;
        while (temp.getParent() != null) {
            Commit p = temp.getParent();
            if (headParents.contains(p)) {
                s = p;
            }
        }
        assert s != null;
        return s;
    }

}
