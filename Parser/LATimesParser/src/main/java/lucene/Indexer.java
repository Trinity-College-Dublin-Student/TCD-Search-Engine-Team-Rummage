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
	static Logger log = Logger.getLogger(Indexer.class.getName());
 	// Directory where the search index will be saved
	private static String INDEX_DIRECTORY_STD = "/home/abhishek/Desktop/lucene-2/BM25/";
	private static String BASE_PATH = "/home/abhishek/Desktop/TCD-DATA/A2-DOC/";
	public static void main(String[] args) throws IOException {
		// Analyzer that is used to process TextField
		DOMConfigurator.configure("/home/abhishek/Desktop/TCD-DATA/lucene-2/log4j.xml");
		Analyzer analyzer_standard = new StandardAnalyzer();
		
		Directory directory_std = FSDirectory.open(Paths.get(INDEX_DIRECTORY_STD));
		
		IndexWriterConfig config_standard = new IndexWriterConfig(analyzer_standard);
	
		
		config_standard.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		config_standard.setSimilarity(new BM25Similarity());

		IndexWriter iwriter_std = new IndexWriter(directory_std, config_standard);
		
		File currentDir = new File(BASE_PATH);
		File[] dirList = currentDir.listFiles();
		int count = 0;
		System.out.println(dirList.length);
		for(File file : dirList) {
			count++;
			try {
				log.info("Count"+ count);
				log.info("Current File -> "+ file.getCanonicalPath());
				System.out.println("");
				BufferedReader br = new BufferedReader(new FileReader(file.getCanonicalFile()));
				String line = null;
				StringBuilder sb = new StringBuilder();
				while((line = br.readLine()) !=null) {
					sb.append(line);
				}
				String[] content = sb.toString().split("</DOC>");
				for (int i= 0; i< content.length; i++) {
					String topic = StringUtils.substringBetween(content[i], "<TITLE>", "</TITLE>").trim(); // Topic
					String text = StringUtils.substringBetween(content[i], "<TEXT>", "</TEXT>").trim(); // Author
					System.out.println();
					Document doc = new Document();
//					log.info("Current File Topic -> "+ topic);
					doc.add(new TextField("Topic", topic, Field.Store.YES));
					doc.add(new TextField("Text", text, Field.Store.YES));
					iwriter_std.addDocument(doc);
//					System.out.println("Indexed :: " + topic);
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) { 
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		// Commit changes and close everything
		iwriter_std.close();
		directory_std.close();
		
	}
}