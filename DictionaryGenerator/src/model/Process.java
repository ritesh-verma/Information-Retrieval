package model;

import tokenizer.Tokenizer;
import util.StopWords;

import java.io.*;
import java.util.*;

public class Process {

    /**
     * Map consisting of instances of Dictionary
     */
    private Map<String, Dictionary> dictionaryMap = new TreeMap<>();

    /**
     * List containing objects of PostingList
     */
    private List<PostingList> postingLists = new ArrayList<>();

    /**
     * Map containing instances of
     */
    private Map<Integer, Map> mapOfTokensMap = new Hashtable<>();

    /**
     * This method invokes other methods internally
     * to generate Documents table, Dictionary and Posting List
     * @param filePaths a list containing path of all the input files
     * @param documentList a list containing all Document instances
     */
    public void doProcess(List<File> filePaths, List<Document> documentList) {

        StopWords stopWords = new StopWords();
        stopWords.createStopWordsList();
        for (int i = 0; i < filePaths.size(); i++) {
            Map tokensMap;
            Document document = new Document();
            try {
                tokensMap = Tokenizer.Tokenize(filePaths.get(i).toString(), stopWords);
                document.initialize(filePaths.get(i));
                document.setDocLength(getDocLengthFromTokensMap(tokensMap));
                documentList.add(document);
                createDictionary(document, tokensMap);
                mapOfTokensMap.put(document.getDocNumber(), tokensMap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        generateOffset();

        createPostingList();

    }

    private int getDocLengthFromTokensMap(Map<String, Integer> tokensMap) {
        int count = 0;
        for (Map.Entry<String, Integer> entry : tokensMap.entrySet()) {
            count += entry.getValue();
        }
        return count;
    }

    /**
     * Method to create Posting List
     */
    private void createPostingList() {
        PostingList postingList;
        Dictionary dictionary;

        for (String term : dictionaryMap.keySet()) {
            dictionary = dictionaryMap.get(term);
            for (int i = 0; i < dictionary.getDocumentFrequency(); i++) {
                postingList = new PostingList();
                postingList.setDocId(dictionary.getDocId(i));
                Map tokensMap = mapOfTokensMap.get(dictionary.getDocId(i));
                int count = (int) tokensMap.get(term);
                postingList.setTermFrequency(count);
                postingLists.add(postingList);
            }
        }
    }

    /**
     * Method to create dictionary
     * @param document instance of Document
     * @param tokensMap a map containing terms and their count in the document
     */
    private void createDictionary(Document document, Map<String, Integer> tokensMap) {

        Dictionary dictionary;

        for (String term : tokensMap.keySet()) {
            if (dictionaryMap.containsKey(term))
                dictionary = dictionaryMap.get(term);
            else
                dictionary = new Dictionary(term);

            dictionary.updateDictionary(tokensMap.get(term), document);
            dictionaryMap.put(term, dictionary);
        }
    }

    /**
     * Method to generate offset in Dictionary table
     */
    private void generateOffset() {
        Dictionary dictionary;
        for (String term : dictionaryMap.keySet()) {
            dictionary = dictionaryMap.get(term);
            dictionary.updateOffset();
        }
    }

    /**
     * This method writes Document table to the file
     * @param documentList A list containing instances of Document
     * @param filePath path of output file
     * @throws IOException Error writing to file
     */
    public void writeDocumentTable(List<Document> documentList, String filePath) throws IOException {

        BufferedWriter documentWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));

        for (int i = 0; i < documentList.size(); i++) {
            Document document = documentList.get(i);
            documentWriter.write(document.getDocNumber() + "," + document.getHeadline() + "," +
                    document.getDocLength() + "," + document.getSnippet() + "," +
                    document.getDocPath() + "\n");
            documentWriter.flush();
        }

        documentWriter.close();
    }

    /**
     * This method writes Dictionary to output file
     * @param dictionaryPath path to write output
     * @throws IOException Error writing to file
     */
    public void writeDictionary(String dictionaryPath) throws IOException {
//        System.out.println(dictionaryMap.size());

        BufferedWriter dictionaryWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictionaryPath)));

        for (String term : dictionaryMap.keySet()) {
            Dictionary dictionary = dictionaryMap.get(term);
            dictionaryWriter.write(dictionary.getTerm() + "," + dictionary.getCollectionFrequency() + "," +
                dictionary.getDocumentFrequency() + "," + dictionary.getOffset() + "\n");
            dictionaryWriter.flush();
        }
        dictionaryWriter.close();
    }

    /**
     * This method writes the posting file
     * @param filePath path of output file
     * @throws IOException Error writing to file
     */
    public void writePostingFile(String filePath) throws IOException {
        BufferedWriter postingWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));

        for (PostingList postingList : postingLists) {
            postingWriter.write(postingList.getDocId() + "," + postingList.getTermFrequency() + "\n");
            postingWriter.flush();
        }

        postingWriter.close();
    }

    /**
     * This method writes the total count to output file
     * @param filePath path of output file
     * @throws IOException Error writing to file
     */
    public void writeTotal(String filePath) throws IOException {
        BufferedWriter totalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));

        totalWriter.write(Integer.toString(getTotalCount()));

        totalWriter.flush();
        totalWriter.close();
    }

    /**
     * Method to count total number of terms
     * @return count of terms
     */
    public int getTotalCount() {
        int total = 0;
        for (String term : dictionaryMap.keySet()) {
            Dictionary dictionary = dictionaryMap.get(term);
            total += dictionary.getCollectionFrequency();
        }
        return total;
    }
}
