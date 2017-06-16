import java.io.*;

public class FileProcessor {
    private BufferedReader br;
    private static int lineNumber = 0;
    private File inputFile;
    private BufferedWriter bw;

    public FileProcessor(String inputFileName, String outputFileName) throws FileNotFoundException {
        inputFile = new File(inputFileName);
        File outputFile = new File(outputFileName);
        br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
    }

    public FileProcessor(String inputFileName) throws FileNotFoundException {
        inputFile = new File(inputFileName);
        br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
    }

    public String readLineFromFile(){
        String line = null;
        try {
            if(br != null)
                line = br.readLine();
            lineNumber++;
        } catch(IOException e) {
            System.out.println("Error while reading from file at line: " + lineNumber);
            e.printStackTrace();
            System.exit(1);
        }
        return line;
    }

    public void writeToFile(String line) throws IOException {
        bw.write(line);
        bw.flush();
    }
}
