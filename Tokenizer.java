
class Tokenizer {
    private String data;
    private int index = 0;

    public Tokenizer(String data) {
        this.data = data;
    }

    public String nextToken() {
        String returnContent = null;
        if (data.startsWith("\"")) {
            returnContent = contentInsideTheQuotes(); //throws an error if there is no quote after the one that is detected
        } else {
            returnContent = returnUnquotedField();
        }
        shrinkStr();
        return returnContent;
    }

    private void shrinkStr() {//does account for quotes
        if (!(data.indexOf(",") == -1)) {
            if (data.startsWith("\""))
                data = data.substring(data.indexOf("\",") + 1);
            data = data.substring(data.indexOf(",") + 1);
        } else //it's the last token
            data = null;

    }

    private String contentInsideTheQuotes() {
        int startIndex = 0;

        int endIndex = data.indexOf("\"", 1);
        if (endIndex != -1) {
            return data.substring(startIndex+1, endIndex );
        } else {
            throw new RuntimeException("no end quotes");
        }
    }

    private String returnUnquotedField() {
        if (data.indexOf(",") == -1) { //if last token
            return data;
        }
        return data.substring(0, data.indexOf(",")); //returns no comma
    }

    public boolean hasNextToken() {
        return data != null;
    }
}