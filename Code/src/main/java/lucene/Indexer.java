package lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.log4j.xml.DOMConfigurator;

public class Indexer {
	// static Logger log = Logger.getLogger(Indexer.class.getName());
	// Directory where the search index will be saved

	private static String INDEX_DIRECTORY_STD = "BM25/";
	private static String BASE_PATH = "A2-DOC/";
	static Logger log = Logger.getLogger(Indexer.class.getName());

	public static void main(String[] args) throws IOException {
		// Analyzer that is used to process TextField
		// DOMConfigurator.configure("/home/abhishek/Desktop/TCD-DATA/lucene-2/log4j.xml");
		Analyzer analyzer_standard = new EnglishAnalyzer();

		Directory directory_std = FSDirectory.open(Paths.get(INDEX_DIRECTORY_STD));

		IndexWriterConfig config_standard = new IndexWriterConfig(analyzer_standard);

		config_standard.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		config_standard.setSimilarity(new BM25Similarity(0.8f, 0.75f));

		IndexWriter iwriter_std = new IndexWriter(directory_std, config_standard);

		File currentDir = new File(BASE_PATH);
		File[] dirList = currentDir.listFiles();
		int count = 0;
		System.out.println(dirList.length);
		for (File file : dirList) {
			count++;
			try {
				System.out.println("Count" + count);
				System.out.println(("Current File -> " + file.getCanonicalPath()));
				System.out.println("");
				BufferedReader br = new BufferedReader(new FileReader(file.getCanonicalFile()));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
					sb.append(" ");
				}
				String[] content = sb.toString().split("</DOC>");
				for (int i = 0; i < content.length; i++) {
					if(content[i].length() <= 2) continue;
					String topic = "";
					String DOCNO = "";
					String text = content[i].trim();
					Document doc = new Document();
					DOCNO = StringUtils.substringBetween(content[i], "<DOCNO>", "</DOCNO>").trim(); // DocNo
					Field pathField = new StringField("path", file.toString(), Field.Store.YES);
					log.info("Current File -> " + pathField);
					//text = text.replaceAll("\\<.*?\\>", "");
					System.out.println(DOCNO);
					doc.add(new TextField("DOCNO", DOCNO, Field.Store.YES));
					doc.add(new TextField("Text", text, Field.Store.YES));
					iwriter_std.addDocument(doc);
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Commit changes and close everything
		iwriter_std.close();
		directory_std.close();
	}
}
