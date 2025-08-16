package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGING_AREA = join(GITLET_DIR, "staging_area");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

    String HEAD;
    Branches branches;
    HashSet<String> stagingArea;
    HashSet<String> removedFiles;
    HashSet<String> unstagedDeleted;
    HashSet<String> unstagedModified;
    HashSet<String> untrackedFiles;

    public void init() throws IOException {
        // first of course we need the folders
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }
        GITLET_DIR.mkdir();
        STAGING_AREA.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        stagingArea = new HashSet<>();
        removedFiles = new HashSet<>();

        // initializes branches and HEAD
        branches = new Branches();
        Branch master = new Branch("master", null);
        branches.put(master);

        HEAD = master.name;

        // initial commit and also write to file.
        String initialCommitHash = createCommit("initial commit", null);

        // moves HEAD to the initial commit
        branches.move(HEAD, initialCommitHash);

        // saves branch and head info
        saveBranch();
        saveHead();
        saveAreas();
    }

    public void add(String filename) throws IOException {
        /** 1 Adds a copy of the file as it currently exists to the staging area
         * (see the description of the commit command). For this reason, adding
         * a file is also called staging the file for addition.
         * 2 Staging an already-staged file overwrites the previous entry in the staging area
         * with the new contents. The staging area should be somewhere in .gitlet.
         * 3 If the current working version of the file is identical to the version
         * in the current commit, do not stage it to be added, and remove it from
         * the staging area if it is already there (as can happen when a file is
         * changed, added, and then changed back to it’s original version).
         * 3.1 The file will no longer be staged for removal (see gitlet rm), if it was
         * at the time of the command. */
        // first, make sure the file exists in the working directory
        File f = new File(filename);
        if (!f.exists()) {
            throw new GitletException("File does not exist.");
        }
        // create new blob out of it (blob stores the file name with the hash code)
        Blob b = new Blob(filename, getHash(f));
        // get current commit object
        Commit currentCommit = getCommitFromHash(branches.get(HEAD));
        // if current commit already includes the exact same blob to be staged,
        // aka the file isn't changed
        // do not stage it
        if (currentCommit.contains(b)) {
            unstage(b.name);
        } else {
            stage(b.name);
        }
        // if the file is in the removing area, get it out of there
        removedFiles.remove(filename);
        saveAreas();
    }

    private void stage(String filename) throws IOException {
        // put it in the staging area. automatically handles if the file is
        // already there
        stagingArea.add(filename);
        File f = new File(filename);
        // copy it to the staging folder with filename as its original name
        Files.copy(f.toPath(), join(STAGING_AREA, filename).toPath());
    }

    private void unstage(String filename) {
        stagingArea.remove(filename);
        File f = join(STAGING_AREA, filename);
        f.delete();
    }

    public void remove(String filename) {
        /**  Unstage the file if it is currently staged for addition.
         * If the file is tracked in the current commit, stage it for
         * removal and remove the file from the working directory if
         * the user has not already done so (do not remove it unless
         * it is tracked in the current commit). */
        File f = new File(filename);
        Blob b = new Blob(filename, getHash(f));
        Commit currentCommit = getCommitFromHash(branches.get(HEAD));
        if (!stagingArea.contains(filename) && !currentCommit.contains(b)) {
            throw new GitletException("No reason to remove the file.");
        } else {
            if (stagingArea.contains(filename)) {
                stagingArea.remove(filename);
                restrictedDelete(f);
                File fStaged = join(STAGING_AREA, filename);
                fStaged.delete();
            }
            if (currentCommit.contains(b)) {
                removedFiles.add(b.name);
                File fileToDelete = new File(filename);
                restrictedDelete(fileToDelete);
            }
        }
        saveAreas();
    }

    // called by the main function
    public void commitFromMain(String m) throws IOException {
        if (m == null) {
            throw new GitletException("Please enter a commit message.");
        }
        // writes the commit and also saves blobs and branches
        String commitHash = createCommit(m, branches.get(HEAD));
        branches.move(HEAD, commitHash);
        // clear staging area and removing area
        clearAreas();
    }

    public void log() {
        Commit c = getCommitFromHash(branches.get(HEAD));
        c.log(branches.get(HEAD));
    }

    public void globalLog() {
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);
        for (String hash : commitList) {
            File f = join(COMMITS_DIR, hash);
            Commit c = readObject(f, Commit.class);
            c.logSingle(hash);
        }
    }

    public void find(String message) {
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);
        Boolean printed = false;
        for (String hash : commitList) {
            File f = join(COMMITS_DIR, hash);
            Commit c = readObject(f, Commit.class);
            if (c.getMessage().contains(message)) {
                System.out.println(hash);
                printed = true;
            }
        }
        if (!printed) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void status() {
        // TODO: still need to revise the order thing
        // TODO: need constant time relative to the number of items
        // this mean that i might need a priority queue
        printBranches();
        printStagedFiles();
        printRemovedFiles();
        printUnstaged();
        printUntracked();
    }

    private void printBranches() {
        System.out.println("=== Branches ===");
        for (String name : branches.keySet()) {
            if (name.equals(HEAD)) {
                System.out.print("*");
            }
            System.out.println(name);
        }
        System.out.println();
    }

    private void printStagedFiles() {
        TreeSet<String> tree = new TreeSet<>(stagingArea);
        System.out.println("=== Staged Files ===");
        for (String filename : tree) {
            System.out.println(filename);
        }
        System.out.println();
    }

    private void printRemovedFiles() {
        TreeSet<String> tree = new TreeSet<>(removedFiles);
        System.out.println("=== Removed Files ===");
        for (String filename : tree) {
            System.out.println(filename);
        }
        System.out.println();
    }

    private void printUnstaged() {
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
    }

    private void printUntracked() {
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /* Takes the version of the file as it exists in the commit
    with the given id, and puts it in the working directory, overwriting
    the version of the file that’s already there if there is one. The new
    version of the file is not staged. */
    public void checkoutFile(String commitHash, String filename) throws IOException {
        if (commitHash == null) {
            commitHash = branches.get(HEAD);
        }
        Commit c = getCommitFromHash(commitHash);
        File fileToRead = c.getFile(filename);
        c.writeToWD(fileToRead, filename);
    }

    /*Takes all files in the commit at the head of the given branch, and puts
    them in the working directory, overwriting the versions of the files that are
    already there if they exist. Also, at the end of this command, the given branch
    will now be considered the current branch (HEAD). Any files that are tracked in
    the current branch but are not present in the checked-out branch are deleted.
    The staging area is cleared, unless the checked-out branch is the current branch */
    public void checkoutBranch(String branchName) throws IOException {
        if (branchName.equals(HEAD)) {
            throw new GitletException("No need to checkout the current branch.");
        }
        String commitHash = branches.get(branchName);
        if (commitHash == null) {
            throw new GitletException("No such branch exists.");
        }
        Commit c = getCommitFromHash(commitHash);
        c.writeAllToWD();
        // TODO: did not clear files that isn't included in the checked out branch
        HEAD = branchName;
        clearAreas();
    }

    /** Creates a new branch with the given name, and points it at the current
     * head commit. A branch is nothing more than a name for a reference (an SHA-1
     * identifier) to a commit node. This command does NOT immediately
     * switch to the newly created branch (just as in real Git). */
    public void branch(String branchName) {
        Branch b = new Branch(branchName, branches.get(HEAD));
        branches.put(b);
    }

    /**  Deletes the branch with the given name. This only means to delete
     * the pointer associated with the branch; it does not mean to delete all
     * commits that were created under the branch, or anything like that. */
    public void rmBranch(String branchName) {
        if (branchName.equals(HEAD)) {
            throw new GitletException("Cannot remove the current branch.");
        }
        if (branches.remove(branchName) == null) {
            throw new GitletException("A branch with that name does not exist.");
        }
    }

    /** Checks out all the files tracked by the given commit. Removes tracked
     * files that are not present in that commit. Also moves the current
     * branch’s head to that commit node. See the intro for an example of
     * what happens to the head pointer after using reset.
     * The [commit id] may be abbreviated as for checkout.
     * The staging area is cleared. The command is essentially checkout
     * of an arbitrary commit that also changes the current branch head. */
    public void reset(String commitHash) throws IOException {
        Commit c = getCommitFromHash(commitHash);
        c.writeAllToWD();
        branches.move(HEAD, commitHash);
        clearAreas();
    }

    private void clearAreas() {
        for (String file : stagingArea) {
            unstage(file);
        }
        removedFiles.clear();
        saveAreas();
    }

    // commits with message and parent info. writes to file
    /* creates the commit object, save it and get its hash */
    private String createCommit(String _message, String _parent) throws IOException {
        // create new commit object
        Commit commit = new Commit(_message, _parent, stagingArea, removedFiles);
        // saves this commit and returns the hash of this commit object
        String commitHash = saveCommit(commit);
        // move HEAD's pointing branch
        branches.move(HEAD, commitHash);
        saveBranch();
        saveHead();
        return commitHash;
    }

    /* saves commit to file .gitlet/commits/hash, and returns the hash code. */
    public String saveCommit(Commit c) {
        String hash = getHash(c);
        File f = join(COMMITS_DIR, hash);
        writeObject(f, c);
        return hash;
    }

    public String getHash(Serializable o) {
        byte[] b = serialize(o);
        return sha1(b);
    }

    private Commit getCommitFromHash(String _hash) {
        File f = join(COMMITS_DIR, _hash);
        if (f.exists()) {
            return readObject(f, Commit.class);
        } else {
            throw new GitletException("No commit with that id exists.");
        }
    }

    public void load() {
        readHead();
        readBranches();
        readAreas();
    }

    private void saveAreas() {
        File sf = join(GITLET_DIR, "staging");
        File rf = join(GITLET_DIR, "removing");
        writeObject(sf, stagingArea);
        writeObject(rf, removedFiles);
    }

    private void readAreas() {
        File sf = join(GITLET_DIR, "staging");
        File rf = join(GITLET_DIR, "removing");
        stagingArea = readObject(sf, HashSet.class);
        removedFiles = readObject(rf, HashSet.class);
    }

    private void saveBranch() {
        File f = join(GITLET_DIR, "branches");
        writeObject(f, branches);
    }

    private void readBranches() {
        File f = join(GITLET_DIR, "branches");
        branches = readObject(f, Branches.class);
    }

    private void saveHead() {
        File f = join(GITLET_DIR, "HEAD");
        writeObject(f, HEAD);
    }

    private void readHead() {
        File f = join(GITLET_DIR, "HEAD");
        HEAD = readObject(f, String.class);
    }
}
