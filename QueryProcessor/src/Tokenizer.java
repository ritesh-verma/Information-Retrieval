import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Tokenizer {
    private static Map<String, Integer> termsCountMap;

    public static Map Tokenize(String fileName, StopWords stopWords) {
        termsCountMap = new TreeMap<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

            String lineFromFile;
            while ((lineFromFile = br.readLine()) != null) {
                String line = lineFromFile.replaceAll("\\<.*?>", "").toLowerCase();

                line = removeHyphens(line);

                String[] words = line.split(" ");

                for (String word : words) {

                    word = removeStopWords(word, stopWords);

                    word = removeParenthesisAndQuotes(word);

                    word = removePunctuation(word);

                    word = removeApostrophes(word);

                    word = performStemming(word);

                    word = removeSpecialCharacters(word);

                    word = removeSingleLengthWords(word);

                    addToMap(word);
                }
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return termsCountMap;
    }

    public static String[] TokenizeQuery(String query, StopWords stopWords) {
        termsCountMap = new TreeMap<>();
        String line = query.replaceAll("\\<.*?>", "").toLowerCase();

        line = removeHyphens(line);

        String[] words = line.split(" ");

        for (String word : words) {

            word = removeStopWords(word, stopWords);

            word = removeParenthesisAndQuotes(word);

            word = removePunctuation(word);

            word = removeApostrophes(word);

            word = performStemming(word);

            word = removeSpecialCharacters(word);

            word = removeSingleLengthWords(word);
        }

        return words;
    }


    private static String removeSpecialCharacters(String line) {
        line = line.replaceAll("[^\\p{Alpha}\\p{Digit}]+","");
        return line;
    }

    private static String removeHyphens(String line) {
        return line.replaceAll("\\-", " ");
    }

    private static void addToMap(String term) {
        if (!term.equals("")) {
            if (termsCountMap.containsKey(term)) {
                termsCountMap.put(term, termsCountMap.get(term) + 1);
            } else {
                termsCountMap.put(term, 1);
            }
        }
    }

    private static String performStemming(String term) {
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

    private static String removeApostrophes(String term) {
        if (term.endsWith("'s") || term.endsWith("s'")) {
            term = term.replace("'", "");
        }

        return term;
    }

    private static String removePunctuation(String term) {
        if (term.endsWith(",") || term.endsWith(".") || term.endsWith("?") ||
                term.endsWith(":") || term.endsWith(";") || term.endsWith("!")) {
            term = term.substring(0, term.length() - 1);
        }

        return term;
    }

    private static String removeParenthesisAndQuotes(String term) {
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

    private static String removeStopWords(String term, StopWords stopWords) {
        if (stopWords.getStopWordsList().contains(term)) {
            return "";
        } else return term;
    }

    private static String removeSingleLengthWords(String term) {
        if(term.length() < 2) {
            return "";
        } else return term;
    }

    public static void writeOutput(FileProcessor fp) {
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

    public static void displayTokens() {
        Set<Map.Entry<String, Integer>> entrySet = termsCountMap.entrySet();
        System.out.println("Term\t\tCount");
        for (Map.Entry entry : entrySet) {
            System.out.println(entry.getKey() + "\t\t" + entry.getValue());
        }
    }
}
