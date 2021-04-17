import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
//Written By Lorenzo Velasque Guerrero 40176510

public class Driver {


    public static void processFilesForValidation(String[] args) {
        FileInputStream inputFile = null;
        FileOutputStream outputJSONFile = null;
        File outputDir = null;
        String JSONFileName = null;
        for (String arg : args) { //loops thru the files therefore we should do everything within the loop
            try {
                inputFile = new FileInputStream(arg);
            } catch (FileNotFoundException e) {
                System.err.println("Could not open input file " + arg + " for reading. Please\n" + "check if file exists! Program will terminate after closing any opened files.");
                System.exit(1);
            }

            try {
                outputDir = new File("Output Files");
                outputDir.mkdir();
                JSONFileName = arg.substring(0, arg.indexOf('.'));
                File JSONFile = new File(outputDir, JSONFileName + ".json");
                JSONFile.createNewFile();
                outputJSONFile = new FileOutputStream(JSONFile);
            } catch (FileNotFoundException e) {
                System.err.println("Could not open/create output file " + arg + " for writing. Program will terminate after closing any opened files.");
                System.err.println("Program will terminate after deleting all created files and closing all input files");
                //close
                System.exit(2);
            } catch (IOException e) {
                System.err.println("could not create JSON output file " + JSONFileName + ".");
            }
            CSVtoJSON(outputJSONFile, inputFile, outputDir, arg);
        }
    }

    private static void CSVtoJSON(FileOutputStream outputFile, FileInputStream inputFile, File outputDir, String arg) {
        //convert data to arrayLists so we can properly hold it and convert it to JSON or to other things
        //load attributes
        ArrayList<String> attributes = new ArrayList();
        Scanner scn = new Scanner(inputFile);
        PrintWriter JSONwriter = new PrintWriter(outputFile);

        if (scn.hasNextLine()) {
            try {
                attributes = handleFirstLine(scn.nextLine());
            } catch (CSVFileInvalidException e) {
                System.err.println("Cannot use CSV file " + arg + " due to missing attribute.\nProgram now terminating...");
                System.exit(1);
            }
        }
        //load all data into arrayList
        ArrayList<ArrayList<String>> data = new ArrayList();
        boolean[] missingData = {false};
        while (scn.hasNextLine()) {
            try {
                var lineAsList = handleNextLine(scn.nextLine(), missingData);
                if (missingData[0]) {
                    logMissingRecordData(outputDir, lineAsList, arg);
                    missingData[0] = false;
                    throw new CSVDataMissing();
                }
                data.add(lineAsList);
                //creating rows, current row is stored in i and then we increment it
            } catch (CSVDataMissing csvDataMissing) {
                System.err.println("In File not create a record because of a missing value. Logging record for file " + arg);
            }
        }

        JSONwriter.println("[");
        boolean firstOuterArray = true;
        //print
        for (ArrayList<String> record : data) {
            if (firstOuterArray) {
                firstOuterArray = false;
            } else {
                JSONwriter.println(",");//everything except the first innerArr starts by comma, newline
            }
            JSONwriter.println("  {");
            boolean firstInnerArray = true;

            for (int i = 0; i < attributes.size(); i++) {
                if (firstInnerArray) {
                    firstInnerArray = false;
                } else
                    JSONwriter.println(",");//every JSON object except the first one begins with a comma and newln
                JSONwriter.print("\t\"" + attributes.get(i) + "\": ");
                JSONwriter.print("\"" + record.get(i) + "\"");//if there are any non-number chars
            }
            JSONwriter.println();
            JSONwriter.print("  }");
        }
        JSONwriter.println();
        JSONwriter.print("]");
        JSONwriter.close();

    }

    private static void logMissingRecordData(File dir, ArrayList<String> errorLine, String inputFileName) {
        File errorLog = new File(dir, "log.txt");
        PrintWriter logWriter = null;
        try {
            errorLog.createNewFile();
            logWriter = new PrintWriter(errorLog);
            logWriter.println("Missing data in " + inputFileName);
            logWriter.println(errorLine);
            logWriter.close();
        } catch (IOException e) {
            System.err.println("Could not create log file " + errorLog.getName() + " in " + dir.getName());
        }
    }

    private static ArrayList<String> handleFirstLine(String nextLine) throws CSVFileInvalidException {
        Tokenizer t = new Tokenizer(nextLine);
        ArrayList<String> fields = new ArrayList<>();
        while (t.hasNextToken()) {
            String token = t.nextToken();
            if (token.equals("")) {
                throw new CSVFileInvalidException();
            }
            fields.add(token);
        }
        return fields;
    }


    private static ArrayList<String> handleNextLine(String nextLine, boolean[] missingData) {
        Tokenizer t = new Tokenizer(nextLine);
        ArrayList<String> returnVal = new ArrayList();
        while (t.hasNextToken()) {
            String nextToken = t.nextToken();
            if (nextToken.equals("")) {
                returnVal.add("***");
                missingData[0] = true;
            } else
                returnVal.add(nextToken);
        }
        return returnVal;
    }


    public static void showData() {
        Scanner userInput = new Scanner(System.in);
        int invalidNameCount = 0, maxInvalidNames = 2;
        boolean validName = false;

        System.out.println("Would you like to see a JSON file? y/n");

        String answer = userInput.nextLine();

        if (answer.equals("n")) {
            System.out.println("Goodbye.");
            System.exit(0);
        }
        if (answer.equals("y")) {
            File workingDirectory = new File("Output Files");
            System.out.println("Which one of the output files would you like to view?");

            System.out.println(Arrays.toString(workingDirectory.list()));
            String fileName = null;
            boolean isInOutputDir = false;
            while ((invalidNameCount < maxInvalidNames) && !isInOutputDir) {
                fileName = userInput.nextLine();
                for (String file : workingDirectory.list()) {
                    if (file.equals(fileName)) {
                        isInOutputDir = true;
                        break;
                    }
                }
                invalidNameCount++;
            }
            System.out.println();
            if(maxInvalidNames == invalidNameCount) {
                System.out.println("Exceeded max number of tries. Program is now shutting down.");
                System.exit(0);
            }
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader("Output Files/" + fileName));
                String line = in.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = in.readLine();
                }
                in.close();
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found in ShowData()");
            } catch (IOException e) {
                System.out.println("IO exception in ShowData()");
            }

        }
    }


    public static void main(String[] args) {//CSV files are passed at the command line
        processFilesForValidation(args);
        showData();


    }

}
