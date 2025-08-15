package gitlet;

import java.util.HashMap;

public class Blob extends HashMap<String, String> {
    String name;
    String hash;

    public Blob(String _name, String _hash) {
        name = _name;
        hash = _hash;
    }

}