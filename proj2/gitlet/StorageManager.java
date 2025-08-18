package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static gitlet.Utils.*;

class StorageManager {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File STAGING_AREA = join(GITLET_DIR, "staging_area");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");

    public static void initializeFolders() {
        GITLET_DIR.mkdir();
        STAGING_AREA.mkdir();
        BLOBS_DIR.mkdir();
        COMMITS_DIR.mkdir();
    }

    // turns out there's this useful function in utils!
    public static String getFileHash(File f) {
        byte[] b = readContents(f);
        return sha1(b);
    }

    public static String getHash(Serializable o) {
        byte[] b = serialize(o);
        return sha1(b);
    }

    /** read commit object from the commit folder, indexed by hash */
    public static Commit getCommitFromHash(String hash) {
        File f = join(COMMITS_DIR, hash);
        if (f.exists()) {
            return readObject(f, Commit.class);
        } else {
            throw new GitletException("No commit with that id exists.");
        }
    }

    public static void copyToStage(String filename) throws IOException {
        File f = join(CWD, filename);
        // copy it to the staging folder with filename as its original name
        Files.copy(f.toPath(),
                join(STAGING_AREA, filename).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
    }

    /** copy the file blob in the staging area to the blobs folder, named as hash */
     public static void saveBlob(Blob b) throws IOException {
        // the new file is in the blobs folder, named as its hash
        File newFile = join(BLOBS_DIR, b.hash);
        // it is taken form the staged file with the corresponding name
        File stagedFile = join(STAGING_AREA, b.name);
        // I think even when there was an exact same blob, this still works
        Files.copy(stagedFile.toPath(), newFile.toPath());
    }

    public static void writeToWD(File f, String filename) throws IOException {
         File newFile = join(CWD, filename);
         if (newFile.exists()) {
             restrictedDelete(newFile);
         }
        Files.copy(f.toPath(),
                newFile.toPath());
    }

    public static void saveBranches(File GITLET_DIR, BranchManager bm) {
        File f = join(GITLET_DIR, "branches");
        writeObject(f, bm);
    }

    public static BranchManager readBranches(File GITLET_DIR) {
        File f = join(GITLET_DIR, "branches");
        return readObject(f, BranchManager.class);
    }

    public static void saveStages(File GITLET_DIR, StageManager sm) {
        File f = join(GITLET_DIR, "stages");
        writeObject(f, sm);
    }

    public static StageManager readStage(File GITLET_DIR) {
        File f = join(GITLET_DIR, "stages");
        return readObject(f, StageManager.class);
    }

    public static String getContentString(File f) {
        if (f == null) {
            return "";
        } else {
            return readContentsAsString(f);
        }
    }

}