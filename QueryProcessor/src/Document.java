import java.io.*;

public class Document {

    private static int globalDocNumber = 0;

    private int docNumber;

    private String headline;

    private long docLength;

    private String snippet;

    private String docPath;

    public Document() {
        docNumber = ++globalDocNumber;
    }

    public void initialize(File path) throws IOException {
        docPath = path.getAbsolutePath();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

        String line;
        boolean isSnippet = false;
        StringBuilder builder = new StringBuilder();
        String snippetString = "";
        final int SNIPPET_LENGTH = 40;

        while ((line = br.readLine()) != null) {
            if (line.equalsIgnoreCase("<HEADLINE>")) {
                headline = br.readLine().replaceAll(",", " ").trim();
            }
            if (line.equalsIgnoreCase("<TEXT>")) {
                isSnippet = true;
            } else if (line.equalsIgnoreCase("</TEXT>")) {
                isSnippet = false;
            }
            if (isSnippet && !line.contains("<TEXT>")) {
                builder = builder.append(line.replaceAll("\t", "")).append(" ");
            }
        }

        String [] arr = builder.toString().trim().split("\\s+");

        for(int i = 0; i < Math.min(SNIPPET_LENGTH, arr.length) ; i++)
            snippetString = snippetString + " " + arr[i].replaceAll(",", "");

        this.snippet = snippetString;
    }

    public String getHeadline() {
        return headline;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getDocPath() {
        return docPath;
    }

    public int getDocNumber() {
        return docNumber;
    }

    public long getDocLength() {
        return docLength;
    }

    public void setDocLength(long docLength) {
        this.docLength = docLength;
    }

    public static void setGlobalDocNumber(int globalDocNumber) {
        Document.globalDocNumber = globalDocNumber;
    }

    public void setDocNumber(int docNumber) {
        this.docNumber = docNumber;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }
}
