package gitlet;

// TODO: any imports you need here

import edu.princeton.cs.algs4.ST;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    /** A mapping of file names to its hash.
     * stores name and content hash. */
    private HashMap<String, String> trackedFiles;
    /** Records the committing date and time. */
    private Date timeStamp;
    /**stores hash of this commit's parent for future search. */
    String parent;
    String secondParent;

    final File CWD = new File(System.getProperty("user.dir"));
    final File BLOBS_DIR = join(CWD, ".gitlet", "blobs");
    final File STAGING_AREA = join(CWD, ".gitlet", "staging_area");

    public Commit(String _message, String _parent, Collection<String> stagedFiles, Collection<String> removedFiles) throws IOException {
        message = _message;
        parent = _parent;
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
            trackedFiles = getCommitFromHash(parent).trackedFiles;
            processStagedFiles(stagedFiles);
            processRemovedFiles(removedFiles);
        }
    }

    /* add staged files (filename) to tracked files (Blob)
    * also writes to the blob folder with file's hash as name */
    private void processStagedFiles(Collection<String> stagedFiles) throws IOException {
        for (String filename : stagedFiles) {
            File fileToAdd = join(STAGING_AREA, filename); // gets the file from the staging area
            String fileHash = getHash(fileToAdd); // get its unique hash and make the blob
            Blob blobToAdd = new Blob(filename, fileHash);
            // adds the file with appointed name in the staging area to the tracked files
            // of this commit
            // consider versions! what if the older version of this file is already tracked?
            addToTrack(blobToAdd);
        }
    }

    private void processRemovedFiles(Collection<String> rf) {
        for (String filename : rf) {
            trackedFiles.remove(filename);
        }
    }

    // tracks the blob and writes it
    private void addToTrack(Blob b) throws IOException {
        // in this way, the old value (hash) is automatically replaced
        trackedFiles.put(b.name, b.hash);
        // save the file to the BLOBS_DIR, named as its hash
        writeBlob(b);
    }
    // copy the file blob in the staging area to the blobs folder, named as hash
    private void writeBlob(Blob f) throws IOException {
        // the new file is in the blobs folder, named as its hash
        File newFile = join(BLOBS_DIR, f.hash);
        // it is taken form the staged file with the corresponding name
        File stagedFile = join(STAGING_AREA, f.name);
        // I think even when there was an exact same blob, this still works
        Files.copy(stagedFile.toPath(), newFile.toPath());
    }

    // if files related to the current commit already includes the file blob (name, hash), return True
    // O(1)
    public boolean contains(Blob fb) {
        return trackedFiles.get(fb.name) != null;
    }

    private Commit getCommitFromHash(String _hash) {
        final File CWD = new File(System.getProperty("user.dir"));
        final File COMMITS_DIR = join(CWD, ".gitlet", "commits");
        File f = join(COMMITS_DIR, _hash);
        return readObject(f, Commit.class);
    }

    public String getHash(Serializable o) {
        byte[] b = serialize(o);
        return sha1(b);
    }

    // returns the tracked file with the given name
    public File getFile(String filename) {
        String fileHash = trackedFiles.get(filename);
        if (fileHash == null) {
            throw new GitletException("File does not exist in that commit.");
        }
        return join(BLOBS_DIR, fileHash);
    }

    public void writeAllToWD() throws IOException {
        for (String filename : trackedFiles.keySet()) {
            File fileToRead = getFile(filename);
            writeToWD(fileToRead, filename);
        }
    }

    public void writeToWD(File fileToRead, String filename) throws IOException {
        File fileToWrite = new File(filename);
        if (fileToWrite.exists()) {
            fileToWrite.delete();
        }
        Files.copy(fileToRead.toPath(), fileToWrite.toPath());
    }


    /* example:
    ===
    commit a0da1ea5a15ab613bf9961fd86f010cf74c7ee48
    Date: Thu Nov 9 20:00:05 2017 -0800
    A commit message.

     */
    public void log(String hash) {
        logSingle(hash);
        if (parent != null) {
            Commit parentCommit = getCommitFromHash(parent);
            parentCommit.log(parent);
        }
    }

    public void logSingle(String hash) {
        System.out.println("===");
        System.out.println("commit " + hash);
        String dateS = String.format(
                "%ta %1$tb %1$td %1$tT %1$tz", timeStamp);
        System.out.println("Date: " + dateS);
        System.out.println(message);
        System.out.println();
    }

    public String getMessage() {
        return message;
    }
}
