public class Ranking implements Comparable<Ranking> {

    private String headline;

    private String documentPath;

    private Float probability;

    private String snippet;

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }


    @Override
    public int compareTo(Ranking ranking) {
        return this.probability.compareTo(ranking.probability);
    }
}
