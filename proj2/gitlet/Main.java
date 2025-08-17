package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) throws IOException {
        /* If there's no commands, exit the program.*/
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        /* Gets the first argument. */
        String firstArg = args[0];
        Repository repo = new Repository();
        switch(firstArg) {

            case "init":
                repo.init();
                break;

            case "add":
                repo.load();
                repo.add(args[1]);
                break;

            case "rm":
                repo.load();
                repo.remove(args[1]);
                break;

            case "commit":
                repo.load();
                String message = args[1];
                repo.commitFromMain(message);
                break;

            case "log":
                repo.load();
                repo.log();
                break;

            case "global-log":
                repo.load();
                repo.globalLog();
                break;

            case "find":
                repo.load();
                repo.find(args[1]);
                break;

            case "status":
                repo.load();
                repo.status();
                break;

            case "checkout":
                repo.load();
                if (args[1].equals("--")) {
                    String filename = args[2];
                    repo.checkoutFile(null, filename);
                } else if (args[2].equals("--")){
                    String commitHash = args[1];
                    String filename = args[3];
                    repo.checkoutFile(commitHash, filename);
                } else {
                    repo.checkoutBranch(args[1]);
                }
                break;

            case "branch":
                repo.load();
                repo.branch(args[1]);
                break;

            case "rm-branch":
                System.out.println("case rm-branch");
                repo.load();
                repo.rmBranch(args[1]);
                break;

            case "reset":
                repo.load();
                repo.reset(args[1]);
                break;
        }
    }






}
