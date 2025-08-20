package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Date; // TODO: You'll likely use this in this class
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Ariel
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    /** A mapping of file names to its hash, stores name and content hash. */
    private HashMap<String, String> trackedFiles;
    /** Records the committing date and time. */
    private Date timeStamp;
    /**stores hash of this commit's parent for future search. */
    String parent;
    String secondParent;

    final File CWD = new File(System.getProperty("user.dir"));
    final File BLOBS_DIR = join(CWD, ".gitlet", "blobs");
    final File STAGING_AREA = join(CWD, ".gitlet", "staging_area");

    // constructor
    public Commit(String _message, String _parent, String _secondParent, StageManager sm) {

        Set<String> stagedFiles = sm.getStaged();
        Set<String> removedFiles = sm.getRemoved();

        message = _message;
        parent = _parent;
        // handles the initialize case
        if (parent == null) {
            // this means that this is the initial commit
            // make time 0, and there's no tracked files
            timeStamp = new Date(0L);
            trackedFiles = new HashMap<>();
        } else {
            if (stagedFiles.isEmpty() && removedFiles.isEmpty()) {
                throw new GitletException("No changes added to the commit.");
            }
            timeStamp = new Date();
            trackedFiles = StorageManager.getCommitFromHash(parent).trackedFiles;
            processStage(sm);
        }
    }

    /* add staged files (filename) to tracked files (Blob)
     * also writes to the blob folder with file's hash as name
     * removed files are untracked */
    private void processStage(StageManager sm) {
        for (String filename : sm.getStaged()) {
            File file = join(STAGING_AREA, filename); // gets the file from the staging area
            Blob blob = Blob.makeBlob(file);
            // adds the file with appointed name in the staging area to the tracked files
            // of this commit
            // consider versions! what if the older version of this file is already tracked?
            addToTrack(blob);
        }
        for (String filename : sm.getRemoved()) {
            trackedFiles.remove(filename);
        }
    }

    // tracks the blob and writes it
    private void addToTrack(Blob b) {
        // in this way, the old value (hash) is automatically replaced
        trackedFiles.put(b.name, b.hash);
        // save the file to the BLOBS_DIR, named as its hash
        StorageManager.saveBlob(b);
    }

    // if files related to the current commit already includes the file blob (name, hash), return True
    // O(1)
    public boolean contains(Blob fb) {
        String storedHash = trackedFiles.get(fb.name);
        if (storedHash != null) {
            return storedHash.equals(fb.hash);
        }
        return false;
    }

    // returns the tracked file with the given name
    public File getBlob(String filename) {
        String fileHash = trackedFiles.get(filename);
        if (fileHash == null) {
            return null;
        }
        return join(BLOBS_DIR, fileHash);
    }

    /** returns a TreeMap of all the tracked files (name, hash) in the commit */
    public Map<String, String> getAllTracked() {
        Map<String, String> res = new HashMap<>();
        for (String name : trackedFiles.keySet()) {
            res.put(name, trackedFiles.get(name));
        }
        return res;
    }

    /* checks if a working file is untracked in the current branch and
    would be overwritten by the reset */
    public void checkForUntracked(String oldCommitHash) {
        Commit oldC = StorageManager.getCommitFromHash(oldCommitHash);
        // for all the files in the working directory
        for (String fileInWD : Objects.requireNonNull(plainFilenamesIn(CWD))) {
            // get the blob of the file in WD
            File f = join(CWD, fileInWD);
            Blob b = new Blob(fileInWD, StorageManager.getFileHash(f));
            // if the file is untracked, aka if the blob is not tracked
            if (!oldC.contains(b)) {
                /* if the file will be overwritten (modified or deleted),
                aka the blob is not in the checked out commit */
                if (!contains(b)){
                    throw new GitletException("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                }
            }
        }
    }

    public void writeAllToWD() {
        for (String filename : plainFilenamesIn(CWD)) {
            File f = join(CWD, filename);
            restrictedDelete(f);
        }
        for (String filename : trackedFiles.keySet()) {
            File fileToRead = getBlob(filename);
            StorageManager.writeToWD(fileToRead, filename);
        }
    }

    /** example:
    ===
    commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
    Date: Thu Nov 9 20:00:05 2017 -0800
    A commit message.

     */
    public void log(String hash) {
        logSingle(hash);
        if (parent != null) {
            Commit parentCommit = StorageManager.getCommitFromHash(parent);
            parentCommit.log(parent);
        }
    }

    public void logSingle(String hash) {
        System.out.println("===");
        System.out.println("commit " + hash);
        String dateS = String.format(
                "%ta %1$tb %1$td %1$tT %1$tY %1$tz", timeStamp);
        System.out.println("Date: " + dateS);
        System.out.println(message);
        System.out.println();
    }

    public String getMessage() {
        return message;
    }

    public Commit getParent() {
        if (parent != null) {
            return StorageManager.getCommitFromHash(parent);
        }
        return null;
    }
}
