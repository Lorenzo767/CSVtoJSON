public class CSVDataMissing extends Exception{
    public CSVDataMissing() {
        super("CSV file cannot be used due to missing data.");
    }
    public CSVDataMissing(String message) {
        super(message);

    }


}
