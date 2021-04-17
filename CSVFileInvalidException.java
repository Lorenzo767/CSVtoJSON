public class CSVFileInvalidException extends Exception {//missing attribute error

    public CSVFileInvalidException() {
        super("The CSV file is invalid.");
    }
    public CSVFileInvalidException(String message) {
        super(message);

    }



}
