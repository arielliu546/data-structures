package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import static gitlet.Utils.join;

public class BranchManager implements Serializable {
    private String HEAD;
    private TreeMap<String, String> branches;
    private final File branchf;


    public BranchManager(File GIT_DIR) {
        branchf = join(GIT_DIR, "branches");
        branches = new TreeMap<>();
        HEAD = "master";
        branches.put(HEAD, null);
    }

    public void createNewBranch(String name) {
        if (branches.containsKey(name)) {
            throw new GitletException("A branch with that name already exists.");
        }
        branches.put(name, getCurrentHash());
    }

    public void remove(String name) {
        if (name.equals(HEAD)) {
            throw new GitletException("Cannot remove the current branch.");
        }
        if (branches.remove(name) == null) {
            throw new GitletException("A branch with that name does not exist.");
        }
    }

    public String getHEAD() {
        return HEAD;
    }

    public void switchTo(String branchName) {
        if (!branches.containsKey(branchName)) {
            throw new GitletException("No such branch exists.");
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
