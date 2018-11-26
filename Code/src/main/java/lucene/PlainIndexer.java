package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

// Index original files with BM25;
public class PlainIndexer {
    private static String INDEX_DIRECTORY_STD = "BM25-PLAIN/";
    private static String BASE_PATH = "../Assignment Two Dataset/";

    public static void main(String[] args) throws IOException{
        //Analyzer analyzer = new EnglishAnalyzer();
        Analyzer analyzer = new CustomAnalyzer();
        Directory directory_std = FSDirectory.open(Paths.get(INDEX_DIRECTORY_STD));
        IndexWriterConfig config_standard = new IndexWriterConfig(analyzer);

        config_standard.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        config_standard.setSimilarity(new BM25Similarity(0.8f, 0.75f));

        IndexWriter iwriter_std = new IndexWriter(directory_std, config_standard);

        try {
            PlainIndexer.indexFiles(new File(BASE_PATH), iwriter_std);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Commit changes and close everything
        iwriter_std.close();
        directory_std.close();
    }

    public static void indexFiles(File fileOrFolder, IndexWriter iwriter_std) throws Exception {
        System.out.println(fileOrFolder.getAbsolutePath());
        String fileName = fileOrFolder.getName();
        if(fileName.contains("read") || fileName.contains("READ") || fileName.contains("Read")
                || fileName.equals("fbisdtd.dtd") || fileName.equals("fr94dtd")
                || fileName.equals("ftdtd") || fileName.equals("latimesdtd.dtd")) {
            return;
        }
        if(fileOrFolder.isDirectory()) {
            File[] dirList = fileOrFolder.listFiles();
            for(File file : dirList) {
                PlainIndexer.indexFiles(file, iwriter_std);
            }
            return;
        }
        BufferedReader br = new BufferedReader(new FileReader(fileOrFolder.getCanonicalFile()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);
            sb.append(" ");
        }
        String[] content = sb.toString().split("</DOC>");
        for (int i = 0; i < content.length - 1; i++) {
            String originalDoc = content[i];
            if(originalDoc.length() <= 2) continue;
            Document doc = new Document();
            String DOCNO = StringUtils.substringBetween(originalDoc, "<DOCNO>", "</DOCNO>").trim(); // DocNo
            System.out.println(DOCNO);
            doc.add(new TextField("DOCNO", DOCNO, Field.Store.YES));
            doc.add(new TextField("ALL_CONTENT", originalDoc, Field.Store.YES));
            iwriter_std.addDocument(doc);
        }
        br.close();
    }
}
