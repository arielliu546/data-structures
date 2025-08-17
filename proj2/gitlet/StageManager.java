package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static gitlet.Utils.join;
import static gitlet.Utils.restrictedDelete;

public class StageManager implements Serializable {
    private TreeSet<String> staged;
    private TreeSet<String> removed;
    private final File STAGING_AREA;

    public StageManager(File gitletDir) {
        staged = new TreeSet<>();
        removed = new TreeSet<>();
        STAGING_AREA = join(gitletDir, "staging_area");
    }

    public Set<String> getStaged() {
        return staged;
    }

    public Set<String> getRemoved() {
        return removed;
    }

    public void stage(String filename) throws IOException {
        staged.add(filename);
        StorageManager.copyToStage(filename);
    }

    public void unstage(String filename) {
        staged.remove(filename);
        File f = join(STAGING_AREA, filename);
        f.delete();
    }

    public boolean isStaged(String filename) {
        return staged.contains(filename);
    }

    public void remove(String filename) {
        removed.add(filename);
        restrictedDelete(filename);
    }

    public void unremove(String filename){
        removed.remove(filename);
    }

    public void clear() {
        for (String file : staged) {
            unstage(file);
        }
        removed.clear();
    }

}
