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
    HashSet<String> removingFiles;

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
        removingFiles = new HashSet<>();

        // initializes branches and HEAD
        branches = new Branches();
        Branch master = new Branch("master", null);
        branches.put(master);

        HEAD = master.name;

        // initial commit and alse write to file.
        String initialCommit = commit("initial commit", null);

        // saves branch and head info
        saveBranch();
        saveHead();
        saveAreas();
    }

    public void add(String filename) throws IOException {
        /** Adds a copy of the file as it currently exists to the staging area
         * (see the description of the commit command). For this reason, adding
         * a file is also called staging the file for addition. Staging an
         * already-staged file overwrites the previous entry in the staging area
         * with the new contents. The staging area should be somewhere in .gitlet.
         * If the current working version of the file is identical to the version
         * in the current commit, do not stage it to be added, and remove it from
         * the staging area if it is already there (as can happen when a file is
         * changed, added, and then changed back to it’s original version). The
         * file will no longer be staged for removal (see gitlet rm), if it was
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
        // if current commit already include the exact same blob to be staged,
        // aka the file isn't changed
        // do not stage it
        if (currentCommit.contains(b)) {
            stagingArea.remove(b.name);
        } else {
            // put it in the staging area. automatically handles if the file is
            // already there
            stagingArea.add(filename);
            // copy it to the staging folder with filename as its original name
            Files.copy(f.toPath(), join(STAGING_AREA, filename).toPath());
        }
        // if the file is in the removing area, get it out of there
        removingFiles.remove(filename);
        saveAreas();
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
        if (!stagingArea.contains(b) && !currentCommit.contains(b)) {
            throw new GitletException("No reason to remove the file.");
        } else {
            if (stagingArea.contains(filename)) {
                stagingArea.remove(filename);
                restrictedDelete(f);
            }
            if (currentCommit.contains(b)) {
                removingFiles.add(b.name);
                File fileToDelete = new File(filename);
                restrictedDelete(fileToDelete);
            }
        }
        saveAreas();
    }

    // called by the main function
    public void commit(String m) throws IOException {
        // writes the commit and also saves blobs and branches
        String commitHash = commit(m, branches.get(HEAD));
        // clear staging area and removing area
        clearAreas();
    }

    private void clearAreas() {
        stagingArea = new HashSet<>();
        removingFiles = new HashSet<>();
        saveAreas();
    }

    // a very simple helper function that creates a folder with given name.
    public static void createFolder(String folderName) {
        File FOLDER = join(folderName);
        FOLDER.mkdir();
    }

    // commits with message and parent info. writes to file
    /* creates the commit object, save it and get its hash */
    private String commit(String _message, String _parent) throws IOException {
        // create new commit object
        Commit commit = new Commit(_message, _parent, stagingArea, removingFiles);
        // returns the hash of this commit object
        String commitHash = saveCommit(commit);
        // move HEAD's pointing branch
        branches.put(HEAD, commitHash);
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
        return readObject(f, Commit.class);
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
        writeObject(rf, removingFiles);
    }

    private void readAreas() {
        File sf = join(GITLET_DIR, "staging");
        File rf = join(GITLET_DIR, "removing");
        stagingArea = readObject(sf, HashSet.class);
        removingFiles = readObject(rf, HashSet.class);
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
