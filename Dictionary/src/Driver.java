import java.io.FileNotFoundException;

public class Driver {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Please enter the input and output file.");
            System.out.println("Arguments format: <file_path/input_file> <output_file>");
            System.exit(1);
        }

        FileProcessor fp = null;

        try {
            fp = new FileProcessor(args[0], args[1]);
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {

        }

        StopWords stopWords = new StopWords();
        stopWords.createStopWordsList();
        Tokenizer tokenizer = new Tokenizer(fp, stopWords);
        tokenizer.Tokenize();
//        tokenizer.displayTokens();
        tokenizer.writeOutput();

    }
}
