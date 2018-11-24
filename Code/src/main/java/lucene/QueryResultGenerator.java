package lucene;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class QueryResultGenerator {
	public static void main(String[] args) {
		Analyzer analyzer_standard = new EnglishAnalyzer();
		
		String indexStdBm = "BM25/";
		// Open the folder that contains our search index
		Directory dbstd;
		try {
			// Vector index
			// BM25 index
			dbstd = FSDirectory.open(Paths.get(indexStdBm));
			
			// create objects to read and search across the index
			
			
			DirectoryReader irbstd = DirectoryReader.open(dbstd);
			
			
			
			IndexSearcher isbstd = new IndexSearcher(irbstd);
			// Create the query parser. The default search field is "content", but
			// we can use this to search across any field
			
//			MultiFieldQueryParser qry_std = new MultiFieldQueryParser(new String[] {"filename", "Topic"}, analyzer_standard);
			
			
			QueryParser parser_std = new QueryParser("Text", analyzer_standard);

			BufferedReader br = new BufferedReader(new FileReader("/home/abhishek/Desktop/TCD/IR/Search-Engine-Team-rummage-/topics.401-450"));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while((line = br.readLine()) !=null) {
				sb.append(line);
			}
			String queryString = "";
			String[] content = sb.toString().split("</top>");
			
			BufferedWriter writer_bm_std = null;
			
			writer_bm_std = new BufferedWriter(new FileWriter("./Result.txt"));

			
			for (int i= 1; i< content.length; i++) {
				content[i] = content[i].replace("\"", "");
				content[i] = content[i].replace("/", "");
				queryString =  StringUtils.substringBetween(content[i],"<title>", "<desc>").trim();
				queryString = queryString+ " "+ StringUtils.substringBetween(content[i],"Description:", "<narr>").trim();
				//queryString = queryString + " " + StringUtils.substringAfter(content[i], "Narrative:").trim(); 
				String qryNo = StringUtils.substringBetween(content[i],"Number:", "<title>").trim();
				Query query_std = parser_std.parse(queryString);
				ScoreDoc[] hits_bm_std = isbstd.search(query_std, 1000).scoreDocs;
				System.out.println();
				System.out.println(queryString);
				for (int k = 0; k < hits_bm_std.length; k++) {
					Document hitDoc = isbstd.doc(hits_bm_std[k].doc);
					writer_bm_std.write(qryNo+" "+ "0 "+hitDoc.get("DOCNO")+" 0 " +hits_bm_std[k].score+ " STANDARD" +"\n");
				}
			}
			br.close();
			writer_bm_std.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
    