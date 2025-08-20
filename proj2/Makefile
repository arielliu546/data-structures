# This makefile is defined to give you the following targets:
#
#    default: The default target: Compiles the program in package db61b.
#    check: Compiles the gitlet package, if needed, and then performs the
#           tests described in testing/Makefile.
#    clean: Remove regeneratable files (such as .class files) produced by
#           other targets and Emacs backup files.
#
# In other words, type 'make' to compile everything; 'make check' to 
# compile and test everything, and 'make clean' to clean things up.
# 
# You can use this file without understanding most of it, of course, but
# I strongly recommend that you try to figure it out, and where you cannot,
# that you ask questions.  The Lab Reader contains documentation.

# Name of package containing main procedure 
PACKAGE = gitlet

# The name of the Python 3 program, used in the 'check' target.  If your system
# has a different name for this program (such as just "python"), run
# the Makefile with
#	make PYTHON=python check
PYTHON = python3

# Flags to pass to tester.py.
TESTER_FLAGS =

RMAKE = "$(MAKE)"

# Targets that don't correspond to files, but are to be treated as commands.
.PHONY: default check clean

default:
	$(RMAKE) -C $(PACKAGE) default

check: default
	$(RMAKE) -C testing PYTHON=$(PYTHON) TESTER_FLAGS="$(TESTER_FLAGS)" check

# 'make clean' will clean up stuff you can reconstruct.
clean:
	$(RM) *~
	$(RMAKE) -C $(PACKAGE) clean
	$(RMAKE) -C testing clean
	rm -r .gitlet

test-find:
	java gitlet.Main init
	java gitlet.Main add wug.txt
	java gitlet.Main commit "added wug.txt"
	java gitlet.Main add 1.txt
	java gitlet.Main commit "added 1.txt"
	java gitlet.Main log
	java gitlet.Main find add

test-status:
	make clean
	make
	java gitlet.Main init
	java gitlet.Main add wug.txt
	java gitlet.Main status
	java gitlet.Main add 1.txt
	java gitlet.Main commit "added 1.txt"
	java gitlet.Main status
	java gitlet.Main branch "new branch"
	java gitlet.Main branch "another branch"
	java gitlet.Main status
	java gitlet.Main rm 1.txt
	java gitlet.Main status

test-checkout:
	make clean
	make
	java gitlet.Main init
	java gitlet.Main add wug.txt
	java gitlet.Main commit "added wug.txt"
	echo "haha i'm the breaker" > wug.txt
	java gitlet.Main checkout -- wug.txt
	java gitlet.Main add 2.txt
	java gitlet.Main commit "the second commit"
	echo "haha i'm the breaker" > wug.txt
	java gitlet.Main log

test-test07:
	make
	java gitlet.Main init
	java gitlet.Main add wug.txt
	java gitlet.Main commit "added wug.txt"
	java gitlet.Main rm wug.txt
	java gitlet.Main commit "removed wug.txt"
	java gitlet.Main log
