import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Tokenizer {
    private FileProcessor fp;
    private StopWords stopWords;
    private Map<String, Integer> termsCountMap;

    public Tokenizer(FileProcessor fpIn, StopWords stopWordsIn) {
        fp = fpIn;
        stopWords = stopWordsIn;
        termsCountMap = new TreeMap<>();
//        termsCountMap = new HashMap<>();
    }

    public void Tokenize() {
        String lineFromFile;
        while ((lineFromFile = fp.readLineFromFile()) != null) {
            String line = lineFromFile.replaceAll("\\<.*?>", "").toLowerCase();

            line = removeHyphens(line);

            String[] words = line.split(" ");

            for (String word : words) {

                word = removeStopWords(word);

                word = removeParenthesisAndQuotes(word);

                word = removePunctuation(word);

                word = removeApostrophes(word);

                word = performStemming(word);

                word = removeSingleLengthWords(word);
                addToMap(word);

            }
        }
    }

    private String removeHyphens(String line) {
        return line.replaceAll("\\-", " ");
    }


    private void addToMap(String term) {
        if (!term.equals("")) {
            if (termsCountMap.containsKey(term)) {
                termsCountMap.put(term, termsCountMap.get(term) + 1);
            } else {
                termsCountMap.put(term, 1);
            }
        }
    }

    private String performStemming(String term) {
        if (term.endsWith("ies") &&
                !term.endsWith("eies") &&
                !term.endsWith("aies")) {
            term = term.substring(0, term.lastIndexOf("i")).concat("y");
        } else if (term.endsWith("es") &&
                !term.endsWith("aes") &&
                !term.endsWith("ees") &&
                !term.endsWith("oes")) {
            term = term.substring(0, term.lastIndexOf("s"));
        } else if (term.endsWith("s") &&
                !term.endsWith("us") &&
                !term.endsWith("ss")) {
            term = term.substring(0, term.lastIndexOf("s"));
        }

        return term;
    }

    private String removeApostrophes(String term) {
        if (term.endsWith("'s") || term.endsWith("s'")) {
            term = term.replace("'", "");
        }

        return term;
    }

    private String removePunctuation(String term) {
        if (term.endsWith(",") || term.endsWith(".") || term.endsWith("?") ||
                term.endsWith(":") || term.endsWith(";") || term.endsWith("!")) {
            term = term.substring(0, term.length() - 1);
        }

        return term;
    }

    private String removeParenthesisAndQuotes(String term) {
        if (term.startsWith("\"") || term.startsWith("\'")
                || term.startsWith("(") || term.startsWith("[")) {
            term = term.substring(1);
        }

        if (term.endsWith("\"") || term.endsWith("\'")
                || term.endsWith(")") || term.endsWith("]")) {
            term = term.substring(0, term.length() - 1);
        }

        return term;
    }

    private String removeStopWords(String term) {
        if (stopWords.getStopWordsList().contains(term)) {
            return "";
        } else return term;
    }

    private String removeSingleLengthWords(String term) {
        if(term.length() < 2) {
            return "";
        } else return term;
    }

    public void writeOutput() {
        Set<Map.Entry<String, Integer>> entrySet = termsCountMap.entrySet();

        try {
            for (Map.Entry entry : entrySet) {
                fp.writeToFile(entry.getKey() + " " + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error while writing to file." + e.getMessage());
            System.exit(1);
        }
    }

    public void displayTokens() {
        Set<Map.Entry<String, Integer>> entrySet = termsCountMap.entrySet();
        System.out.println("Term\t\tCount");
        for (Map.Entry entry : entrySet) {
            System.out.println(entry.getKey() + "\t\t" + entry.getValue());
        }
    }
}
