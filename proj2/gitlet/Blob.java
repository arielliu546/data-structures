package gitlet;

import java.io.File;
import java.util.HashMap;

public class Blob extends HashMap<String, String> {
    String name;
    String hash;

    public Blob(String _name, String _hash) {
        name = _name;
        hash = _hash;
    }

    public static Blob makeBlob(File f) {
        String hash = StorageManager.getFileHash(f);
        return new Blob(f.getName(), hash);
    }

}