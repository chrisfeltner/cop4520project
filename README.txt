To compile the project using the command line, please first make sure that you have the Java Development Kit installed.

To compile the project using macOS/Linux, from the root directory of the project, please run:
javac *.java -cp .:lib/hamcrest.jar:lib/junit.jar

To compile the project using Windows, in all of the commands, please replace the colons with semicolons as shown here:
javac *.java -cp .;lib/hamcrest.jar;lib/junit.jar

To run the experiments, from the root directory of the project, please run:
java -cp .:lib/hamcrest.jar:lib/junit.jar Experiments

To run unit tests, from the root directory of the project, please run:
java -cp .:lib/hamcrest.jar:lib/junit.jar org.junit.runner.JUnitCore Tests