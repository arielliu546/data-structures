package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Ariel
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        /* If there's no commands, exit the program.*/
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        /* Gets the first argument. */
        String firstArg = args[0];
        Repository repo = new Repository();
        switch (firstArg) {

            case "init":
                repo.init();
                break;

            case "add":
                repo.add(args[1]);
                break;

            case "rm":
                repo.remove(args[1]);
                break;

            case "commit":
                String message = args[1];
                repo.commitFromMain(message);
                break;

            case "log":
                repo.log();
                break;

            case "global-log":
                repo.globalLog();
                break;

            case "find":
                repo.find(args[1]);
                break;

            case "status":
                repo.status();
                break;

            case "checkout":
                if (args.length == 3) {
                    String filename = args[2];
                    repo.checkoutFile(null, filename);
                } else if (args.length == 4) {
                    String commitHash = args[1];
                    String filename = args[3];
                    repo.checkoutFile(commitHash, filename);
                } else if (args.length == 2) {
                    repo.checkoutBranch(args[1]);
                }
                break;

            case "branch":
                repo.branch(args[1]);
                break;

            case "rm-branch":
                repo.rmBranch(args[1]);
                break;

            case "reset":
                repo.reset(args[1]);
                break;

            case "merge":
                repo.merge(args[1]);
                break;

            default:
                System.out.println("Wrong command!");
                System.exit(0);
                break;
        }
    }
}
