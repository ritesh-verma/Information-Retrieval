import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class QueryProcessor {

    private static final String dictionaryFile = "dictionary.csv";
    private static final String docsTableFile = "docsTable.csv";
    private static final String postingsFile = "postings.csv";
    private static final String totalFile = "total.txt";
    private static final String resultFile = "result.txt";

    private static final float W = 0.1f;

    private static Map<String, Dictionary> dictionaryMap;
    private static List<Document> documentList;
    private static Map<Integer, PostingList> postingsList;

    private static int totalWordsInCollection;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String query;
        Tokenizer tokenizer = new Tokenizer();
        StopWords stopWords = new StopWords();
        BufferedWriter bw = null;

        try {
            totalWordsInCollection = getTotalCount(totalFile);
            dictionaryMap = createDictionaryMap(dictionaryFile);
            documentList = createDocumentList(docsTableFile);
            postingsList = createPostingList(postingsFile);
            bw = new BufferedWriter(new FileWriter(resultFile));

        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        stopWords.createStopWordsList();


        while (true) {
            System.out.println();
            System.out.println("Please enter query (or EXIT to exit).");
            query = sc.nextLine();

            // Exit program if user enters EXIT
            if (query.equalsIgnoreCase("EXIT")) {
                System.out.println("EXIT!");
                sc.close();
                System.exit(1);
            }

            // Process query here
            String[] queryTokens = tokenizer.TokenizeQuery(query, stopWords);
            List<Ranking> rankingList = getQueryRanking(queryTokens);
            Collections.sort(rankingList);
            writeResult(query, bw, rankingList);
        }
    }

    private static void writeResult(String query, BufferedWriter bw, List<Ranking> rankingList) {

        StringBuilder content = new StringBuilder();
        int counter = 0;

        if (rankingList.isEmpty())
            content.append("NO RESULTS").append("\n");

        else {
            for (Ranking ranking : rankingList) {
                if (counter++ > 5)
                    break;

                content.append(ranking.getHeadline()).append("\n");
                content.append(ranking.getDocumentPath()).append("\n");
                content.append("Computed probability: " + ranking.getProbability()).append("\n");
                content.append(ranking.getSnippet()).append("\n\n");
            }
        }

        try {
            bw.write(content.toString());
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<Document> createDocumentList(String docsTableFile) throws IOException {
        List<Document> list = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(docsTableFile)));

        Document document;
        String line;
        String[] tokens;

        while ((line = reader.readLine()) != null) {
            tokens = line.split(",");
            document = new Document();
            document.setDocNumber(Integer.parseInt(tokens[0]));
            document.setHeadline(tokens[1]);
            document.setDocLength(Integer.parseInt(tokens[2]));
            document.setSnippet(tokens[3]);
            document.setDocPath(tokens[4]);

            list.add(document);
        }

        return list;
    }

    private static Map<Integer, PostingList> createPostingList(String postingsFile) throws IOException {
        Map<Integer, PostingList> list = new Hashtable<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(postingsFile)));

        PostingList postingList;
        String line;
        String[] tokens;
        int index = 0;

        while ((line = reader.readLine()) != null) {
            tokens = line.split(",");
            postingList = new PostingList();
            postingList.setDocId(Integer.parseInt(tokens[0]));
            postingList.setTermFrequency(Integer.parseInt(tokens[1]));

            list.put(index++, postingList);
        }

        return list;
    }

    private static Map<String, Dictionary> createDictionaryMap(String dictionaryFile) throws IOException {
        Map<String, Dictionary> map = new TreeMap<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFile)));

        Dictionary dictionary;
        String[] tokens;
        String line;

        while ((line = reader.readLine()) != null) {
            tokens = line.split(",");
            dictionary = new Dictionary();
            dictionary.setTerm(tokens[0]);
            dictionary.setCollectionFrequency(Integer.parseInt(tokens[1]));
            dictionary.setDocumentFrequency(Integer.parseInt(tokens[2]));
            dictionary.setOffset(Integer.parseInt(tokens[3]));

            map.put(tokens[0], dictionary);
        }

        return map;
    }

    private static float log2(float value) {
        return (float) (Math.log(value) / Math.log(2));
    }

    private static List<Ranking> getQueryRanking(String[] queryTokens) {
        List<Ranking> list = new ArrayList<>();
        Set<Integer> docIdSet = new TreeSet<>();

        for (String term : queryTokens) {
            Dictionary dictionary = dictionaryMap.get(term);
            if (dictionary == null) {
                System.out.println("NO RESULTS");
                continue;
            }

            List<PostingList> postings = getListOfPostingList(dictionary);
            for (PostingList posting : postings) {
                docIdSet.add(posting.getDocId());
            }
        }

        for (Integer docId : docIdSet) {
            float probability = 0;
            Document document = getDocumentByDocId(docId);

            for (String term : queryTokens) {
                Dictionary dictionary = dictionaryMap.get(term);
                if (dictionary != null) {
                    int collectionFrequency = dictionary.getCollectionFrequency();
                    int termFrequency = getTermFrequencyFromPostings(docId, getListOfPostingList(dictionary));

                    long docLength = document.getDocLength();

                    probability += calculateRank(W, termFrequency, docLength, collectionFrequency, totalWordsInCollection);
                }

            }

            Ranking ranking = new Ranking();
            ranking.setHeadline(document.getHeadline());
            ranking.setDocumentPath(document.getDocPath());
            ranking.setProbability(probability);
            ranking.setSnippet(document.getSnippet());

            list.add(ranking);
        }

        return list;
    }

    private static int getTermFrequencyFromPostings(Integer docId, List<PostingList> postings) {
        for(PostingList posting : postings) {
            if(docId == posting.getDocId()) {
                return posting.getTermFrequency();
            }
        }
        return 0;

    }

    private static float calculateRank(float w, float termFrequency, float docLength, float collectionFrequency, float totalWordsInCollection) {
        return (log2(((1 - w) * (termFrequency / docLength)) + (w * (collectionFrequency / totalWordsInCollection))));
    }

    private static Document getDocumentByDocId(Integer docId) {
        for (Document document : documentList) {
            if (document.getDocNumber() == docId)
                return document;
        }

        return null;
    }

    private static List<PostingList> getListOfPostingList(Dictionary dictionary) {
        List<PostingList> list = new ArrayList<>();

        int offset = dictionary.getOffset();
        int df = dictionary.getDocumentFrequency();

        for (int i = offset; i < (offset + df); i++) {
            list.add(postingsList.get(i));
        }

        return list;
    }

    private static int getTotalCount(String total) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(total)));
        int count = Integer.parseInt(br.readLine().trim());

        br.close();
        return count;
    }
}
