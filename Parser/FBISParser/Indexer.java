package CS7IS3;

import java.io.*;

import java.util.ArrayList;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.commons.lang3.StringUtils;

public class Indexer {
    // Directory where the search index will be saved
    private static String INDEX_DIRECTORY = "indexedFiles";
    private static String INPUT_FILE_PATH = "/Users/mengxuan/Desktop/groupAssignment/docs/parsedDoc/";

    public static void main(String[] args) {
        try {
            Indexer.indexFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void indexFiles() throws Exception {
        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();
        //Analyzer analyzer = new StandardAnalyzer(new CharArraySet(myStopWords, false));

        // ArrayList of documents in the corpus
        ArrayList<Document> documents = new ArrayList<>();

        // Open the directory that contains the search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // Set up an index writer to add process and save documents to the index
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(directory, config);

        // Index cranfields
        File folder = new File(INPUT_FILE_PATH);
        File[] files = folder.listFiles();
        for(File file : files) {
            Indexer.indexFile(file,documents);
        }

        // Write all the documents in the linked list to the search index
        iwriter.addDocuments(documents);

        // Commit everything and close
        iwriter.close();
        directory.close();
    }

    private static void indexFile(File file, ArrayList<Document> documents) throws Exception {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        String[] content = sb.toString().split("</DOC>");
        for (String doc : content) {
            Document curDoc = new Document();
            String title = "";
            String text = "";
            try{
                title = StringUtils.substringBetween(doc, "<TITLE>", "</TITLE>").trim(); // Title
            } catch (NullPointerException npe) {
                curDoc.add(new TextField("title", "", Field.Store.YES));
            }
            //System.out.println(title);
            curDoc.add(new TextField("title", title.trim(), Field.Store.YES));
            try {
                text = StringUtils.substringBetween(doc, "<TEXT>", "</TEXT>").trim(); // Text
            } catch  (NullPointerException npe) {
                //curDoc.add(new TextField("text", "", Field.Store.YES));
                continue;
            }
            //System.out.println(text);
            curDoc.add(new TextField("text", text.trim(), Field.Store.YES));
            documents.add(curDoc);
        }
    }
}
