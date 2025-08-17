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
 *  @author Ariel
 */
public class Repository {
    /**
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    BranchManager branchManager;
    StageManager stageManager;
    HashSet<String> unstagedDeleted;
    HashSet<String> unstagedModified;
    HashSet<String> untrackedFiles;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGING_AREA = join(GITLET_DIR, "staging_area");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");



    public void init() throws IOException {
        // first of course we need the folders
        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }

        StorageManager.initializeFolders();

        // initializes branches and HEAD
        branchManager = new BranchManager(GITLET_DIR);
        stageManager = new StageManager(GITLET_DIR);

        // initial commit and also write to file.
        String initialCommitHash = createCommit("initial commit", null);

        // moves HEAD to the initial commit
        branchManager.move(initialCommitHash);

        // saves branch and head info
        StorageManager.saveBranches(GITLET_DIR, branchManager);
        StorageManager.saveStages(GITLET_DIR, stageManager);
    }
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
    public void add(String filename) throws IOException {
        load();
        // first, make sure the file exists in the working directory
        File f = join(CWD, filename);
        if (!f.exists()) {
            throw new GitletException("File does not exist.");
        }
        // create new blob out of it (blob stores the file name with the hash code)
        Blob b = new Blob(filename, StorageManager.getFileHash(f));
        // read bm and get current commit object
        Commit currentCommit = StorageManager.getCommitFromHash(branchManager.getCurrentHash());
        // if current commit already includes the exact same blob to be staged,
        // aka the file isn't changed, do not stage it
        if (currentCommit.contains(b)) {
            stageManager.unstage(b.name);
        } else {
            stageManager.stage(b.name);
        }
        // if the file is in the removing area, get it out of there
        stageManager.unremove(b.name);
        StorageManager.saveStages(GITLET_DIR, stageManager);
    }

    /**  Unstage the file if it is currently staged for addition.
     * If the file is tracked in the current commit, stage it for
     * removal and remove the file from the working directory if
     * the user has not already done so (do not remove it unless
     * it is tracked in the current commit). */
    public void remove(String filename) {
        File f = join(CWD, filename);
        Blob b = new Blob(filename, StorageManager.getFileHash(f));
        load();
        Commit currentCommit = StorageManager.getCommitFromHash(branchManager.getCurrentHash());
        if (!stageManager.isStaged(filename) && !currentCommit.contains(b)) {
            throw new GitletException("No reason to remove the file.");
        } else {
            if (stageManager.isStaged(filename)) {
                stageManager.unstage(filename);
            }
            if (currentCommit.contains(b)) {
                stageManager.remove(b.name);
            }
        }
        StorageManager.saveStages(GITLET_DIR, stageManager);
    }

    // called by the main function
    public void commitFromMain(String m) throws IOException {
        load();
        if (m == null) {
            throw new GitletException("Please enter a commit message.");
        }
        // writes the commit and also saves blobs and branches
        String commitHash = createCommit(m, branchManager.getCurrentHash());
        branchManager.move(commitHash);
        // clear staging area and removing area
        stageManager.clear();
        StorageManager.saveStages(GITLET_DIR, stageManager);
    }

    public void log() {
        load();
        Commit c = StorageManager.getCommitFromHash(branchManager.getCurrentHash());
        c.log(branchManager.getCurrentHash());
    }

    public void globalLog() {
        load();
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);
        assert commitList != null;
        for (String hash : commitList) {
            Commit c = StorageManager.getCommitFromHash(hash);
            c.logSingle(hash);
        }
    }

    public void find(String message) {
        load();
        List<String> commitList = plainFilenamesIn(COMMITS_DIR);
        assert commitList != null;
        boolean printed = false;
        // for every filename in the commit directory
        for (String hash : commitList) {
            Commit c = StorageManager.getCommitFromHash(hash);
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
        load();
        StatusPrinter.print(stageManager, branchManager);
    }

    /* Takes the version of the file as it exists in the commit
    with the given id, and puts it in the working directory, overwriting
    the version of the file that’s already there if there is one. The new
    version of the file is not staged. */
    public void checkoutFile(String commitHash, String filename) throws IOException {
        load();
        if (commitHash == null) {
            commitHash = branchManager.getCurrentHash();
        }
        Commit c = StorageManager.getCommitFromHash(commitHash);
        File fileToRead = c.getBlob(filename);
        c.writeToWD(fileToRead, filename);
    }

    /*Takes all files in the commit at the head of the given branch, and puts
    them in the working directory, overwriting the versions of the files that are
    already there if they exist. Also, at the end of this command, the given branch
    will now be considered the current branch (HEAD). Any files that are tracked in
    the current branch but are not present in the checked-out branch are deleted.
    The staging area is cleared, unless the checked-out branch is the current branch */
    public void checkoutBranch(String branchName) throws IOException {
        load();
        if (branchName.equals(branchManager.getHEAD())) {
            throw new GitletException("No need to checkout the current branch.");
        }
        String commitHash = branchManager.getCommitHash(branchName);
        if (commitHash == null) {
            throw new GitletException("No such branch exists.");
        }
        Commit c = StorageManager.getCommitFromHash(commitHash);
        c.checkForUntracked(branchManager.getCurrentHash());
        c.writeAllToWD();
        branchManager.switchTo(branchName);
        stageManager.clear();
        StorageManager.saveStages(GITLET_DIR, stageManager);
    }

    /** Creates a new branch with the given name, and points it at the current
     * head commit. A branch is nothing more than a name for a reference (an SHA-1
     * identifier) to a commit node. This command does NOT immediately
     * switch to the newly created branch (just as in real Git). */
    public void branch(String branchName) {
        branchManager = StorageManager.readBranches(GITLET_DIR);
        branchManager.createNewBranch(branchName);
    }

    /**  Deletes the branch with the given name. This only means to delete
     * the pointer associated with the branch; it does not mean to delete all
     * commits that were created under the branch, or anything like that. */
    public void rmBranch(String branchName) {
        branchManager = StorageManager.readBranches(GITLET_DIR);
        branchManager.remove(branchName);
    }

    /** Checks out all the files tracked by the given commit. Removes tracked
     * files that are not present in that commit. Also moves the current
     * branch’s head to that commit node. See the intro for an example of
     * what happens to the head pointer after using reset.
     * The [commit id] may be abbreviated as for checkout.
     * The staging area is cleared. The command is essentially checkout
     * of an arbitrary commit that also changes the current branch head. */
    public void reset(String commitHash) throws IOException {
        load();
        Commit c = StorageManager.getCommitFromHash(commitHash);
        c.checkForUntracked(commitHash);
        c.writeAllToWD();
        branchManager.move(commitHash);
        stageManager.clear();
        StorageManager.saveStages(GITLET_DIR, stageManager);
    }

    public void merge(String branchName) {

    }

    // commits with message and parent info. writes to file
    /* creates the commit object, save it and get its hash */
    private String createCommit(String _message, String _parent) throws IOException {
        // create new commit object
        Commit commit = new Commit(_message, _parent, stageManager);
        // saves this commit and returns the hash of this commit object
        String commitHash = saveCommit(commit);
        // move HEAD's pointing branch
        branchManager.move(commitHash);
        StorageManager.saveBranches(GITLET_DIR, branchManager);
        return commitHash;
    }

    /* saves commit to file .gitlet/commits/hash, and returns the hash code. */
    private String saveCommit(Commit c) {
        String hash = StorageManager.getHash(c);
        File f = join(COMMITS_DIR, hash);
        writeObject(f, c);
        return hash;
    }

    private void load() {
        branchManager = StorageManager.readBranches(GITLET_DIR);
        stageManager = StorageManager.readStage(GITLET_DIR);
    }
}
