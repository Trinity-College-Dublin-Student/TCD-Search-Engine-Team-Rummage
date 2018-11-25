package lucene;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class QueryResultGenerator {
    public static void main(String[] args) {
        Analyzer analyzer = new EnglishAnalyzer();

        //String indexStdBm = "BM25/";
        String indexStdBm = "BM25-PLAIN";
        // Open the folder that contains our search index
        Directory dbstd;
        try {
            // Vector index
            // BM25 index
            dbstd = FSDirectory.open(Paths.get(indexStdBm));

            // create objects to read and search across the index

            DirectoryReader irbstd = DirectoryReader.open(dbstd);

            IndexSearcher isbstd = new IndexSearcher(irbstd);
            isbstd.setSimilarity(new BM25Similarity(0.8f, 0.75f));
            // Create the query parser. The default search field is "content", but
            // we can use this to search across any field

//			MultiFieldQueryParser qry_std = new MultiFieldQueryParser(new String[] {"filename", "Topic"}, analyzer);

            //QueryParser parser_std = new QueryParser("Text", analyzer);
            QueryParser parser_std = new QueryParser("ALL_CONTENT", analyzer);

            BufferedReader br = new BufferedReader(new FileReader("../topics.401-450"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String queryString = "";
            String[] content = sb.toString().split("</top>");

            BufferedWriter writer_bm_std = null;

            writer_bm_std = new BufferedWriter(new FileWriter("./Result.txt"));

            for (int i = 1; i < content.length; i++) {	
                content[i] = content[i].replace("\"", "");
                content[i] = content[i].replace("/", "");
                queryString = StringUtils.substringBetween(content[i], "<title>", "<desc>").trim();
                String[] qryStr = queryString.trim().split(" ");
                @SuppressWarnings("deprecation")
				String scrambledWord = StringUtils.reverseDelimitedString(queryString, " ");
                System.out.println(scrambledWord);
                queryString = queryString+ " "+ queryString + " "+qryStr[0] +" "+ StringUtils.substringBetween(content[i], "Description:", "<narr>")+ " " + scrambledWord.trim();
                queryString = queryString + " " + StringUtils.substringAfter(content[i], "Narrative:").toLowerCase().
                        replace("document", "").replace("documents","").replace("relevant", "").replace("discuss","").replace("provide", "").replace("mention","").
                        replace("describing","").replace("find","").replace("information",""). replace("relevant","").trim();
                //queryString = QueryResultGenerator.replace(queryString);
                String qryNo = StringUtils.substringBetween(content[i], "Number:", "<title>").trim();
                Query query_std = parser_std.parse(queryString);
                ScoreDoc[] hits_bm_std = isbstd.search(query_std, 1000).scoreDocs;
                System.out.println();
                System.out.println(queryString);
                for (int k = 0; k < hits_bm_std.length; k++) {
                    Document hitDoc = isbstd.doc(hits_bm_std[k].doc);
                    writer_bm_std.write(qryNo + " " + "0 " + hitDoc.get("DOCNO") + " 0 " + hits_bm_std[k].score + " STANDARD" + "\n");
                }
            }
            br.close();
            writer_bm_std.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String replace(String s) {
        for(String word : myStopWords) {
            s = s.replace(" " + word + " ", "");
        }
        //s.replaceAll("\\s*", " ");
        s = s.trim();
        return s;
    }

    // My stopword list
    static List<String> myStopWords = Arrays.asList(
            "a", "an", "and", "any", "are", "all", "as", "am", "at", "about","another", "above", "after", "along", "amid", "among", "anyone", "anyplace", "anything", "anytime", "anywhere",
            "be", "but", "by", "being", "been", "both",
            "can", "cannot", "could",
            "different", "document", "documents", "discuss", "describing", "Do",
            "enough", "every", "either", "each", "even", "especially", "everybody", "everyday", "everyone", "everyplace", "everything", "everywhere",
            "for", "from", "find",
            "get", "gets", "got", "gotten", "getting",
            "have", "had", "has", "having", "his", "her", "how", "he", "him",
            "if", "in", "into", "is", "it", "its", "I", "information",
            "just",
            "like", "less", "least",
            "minus", "may", "might", "must", "my", "mere", "merely", "more", "most", "me", "mine", "much", "mention",
            "no", "not", "near", "need", "neither", "namely",
            "of", "on", "or", "off", "our", "onto", "only", "out", "over", "ought",
            "past", "per", "plus", "pretty", "provide",
            "quite",
            "right", "rather", "relevant",
            "so", "such", "since", "shall", "should", "seemed", "seems", "seem", "seeming", "some", "sheer", "somewhat", "sufficiently", "same", "she",
            "that", "the", "their", "then", "there", "these","those", "they", "them", "this", "to", "till", "too", "theirs",
            "until", "under", "up", "us",
            "via", "vs",
            "we", "was", "were", "will", "with", "would", "when", "why", "where", "what", "who", "whom", "which" ,"whether", "whose", "whatever", "whenever", "whereever", "whichever", "whoever", "whomever",
            "you", "your", "yours");
}
    