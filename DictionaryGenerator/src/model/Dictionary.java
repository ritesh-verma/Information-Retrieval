package model;

import java.util.ArrayList;
import java.util.List;

public class Dictionary {
    private String term;

    private int collectionFrequency;

    private int documentFrequency;

    private List<Document> documentList;

    private static int globalOffset = 0;

    private int offset;


    /**
     * Constructor to create Dictionary
     * @param term the term to be added
     */
    public Dictionary(String term) {
        this.term = term;
        this.collectionFrequency = 0;
        this.documentFrequency = 0;
        documentList = new ArrayList<>();
    }

    /**
     * Method to update the values in dictionary
     * @param termCount count of the term in document
     * @param document the document containing the term
     */
    public void updateDictionary(int termCount, Document document) {
        this.collectionFrequency += termCount;
        this.documentFrequency++;
        documentList.add(document);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public static int getGlobalOffset() {
        return globalOffset;
    }

    public int getCollectionFrequency() {
        return collectionFrequency;
    }

    public void setCollectionFrequency(int collectionFrequency) {
        this.collectionFrequency = collectionFrequency;
    }

    public int getDocumentFrequency() {
        return documentFrequency;
    }

    public void setDocumentFrequency(int documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void updateOffset() {
        setOffset(globalOffset);
        globalOffset += documentFrequency;
    }

    public int getDocId(int index) {
        return documentList.get(index).getDocNumber();
    }
}
