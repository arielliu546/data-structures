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
            // make time 0, and no tracked files
            timeStamp = new Date(0L);
            trackedFiles = new HashMap<>();
        } else {
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
            File fileToAdd = new File(filename);
            String fileHash = getHash(fileToAdd);
            Blob blobToAdd = new Blob(filename, fileHash);
            // adds the file with appointed name in the working directory to the tracked files
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

    private void addToTrack(Blob b) throws IOException {
        // in this way, the old value is automatically replaced
        trackedFiles.put(b.name, b.hash);
        // save the file to the BLOBS_DIR, named as its hash
        writeBlob(b);
    }

    private boolean containedInTrack(Blob b) {
        return trackedFiles.get(b.name) != null;
    }

    private void writeBlob(Blob f) throws IOException {
        File newFile = join(BLOBS_DIR, f.hash);
        File stagedFile = join(STAGING_AREA, f.name);
        Files.copy(stagedFile.toPath(), newFile.toPath());
    }

    // if files related to the current commit already includes the file blob (name, hash), return True
    // O(1)
    public boolean contains(Blob fb) {
        return containedInTrack(fb);
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
}
