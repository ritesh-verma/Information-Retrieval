package driver;

import model.Document;
import model.Process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Driver {

    private static List<Document> documentList = new ArrayList<>();

    private static final String DOCUMENT_TABLE = "docsTable.csv";
    private static final String DICTIONARY = "dictionary.csv";
    private static final String POSTING = "postings.csv";
    private static final String TOTAL = "total.txt";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide directory path name.");
            System.exit(1);
        }

        String filePath = args[0];
        File dir = new File(filePath);

        List<File> filePaths = new ArrayList<>();

        Set<String> fileNames = new HashSet<>();
        getFiles(dir, fileNames, filePaths);

        Process processor = new Process();
        processor.doProcess(filePaths, documentList);

        try {
            processor.writeDocumentTable(documentList, DOCUMENT_TABLE);
            processor.writeDictionary(DICTIONARY);
            processor.writePostingFile(POSTING);
            processor.writeTotal(TOTAL);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads file recursively and adds to a list
     * @param file the starting directory path
     * @param fileNames set of file names
     * @param filePaths list of file paths
     */
    private static void getFiles(File file, Set<String> fileNames, List<File> filePaths) {
        if(file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File f : subFiles)
                getFiles(f, fileNames, filePaths);
        } else {
            if(!"".equalsIgnoreCase(file.getName()) && !fileNames.contains(file.getName()))
                filePaths.add(file);
        }
    }
}
