import java.util.ArrayList;
import java.util.List;

public class StopWords {

    private List<String> stopWordsList;

    public List<String> getStopWordsList() {
        return stopWordsList;
    }

    public StopWords() {
        stopWordsList = new ArrayList<>();
    }

    public void createStopWordsList() {

        stopWordsList.add("and");
        stopWordsList.add("a");
        stopWordsList.add("the");
        stopWordsList.add("an");
        stopWordsList.add("by");
        stopWordsList.add("from");
        stopWordsList.add("for");
        stopWordsList.add("hence");
        stopWordsList.add("of");
        stopWordsList.add("with");
        stopWordsList.add("in");
        stopWordsList.add("within");
        stopWordsList.add("who");
        stopWordsList.add("when");
        stopWordsList.add("where");
        stopWordsList.add("why");
        stopWordsList.add("was");
        stopWordsList.add("how");
        stopWordsList.add("whom");
        stopWordsList.add("have");
        stopWordsList.add("had");
        stopWordsList.add("has");
        stopWordsList.add("not");
        stopWordsList.add("but");
        stopWordsList.add("do");
        stopWordsList.add("does");
        stopWordsList.add("done");
    }
}
