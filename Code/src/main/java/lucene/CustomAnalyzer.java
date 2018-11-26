/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucene;

import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

/**
 *
 * @author Nicky
 */
public class CustomAnalyzer extends Analyzer {

    @Override   
    protected Analyzer.TokenStreamComponents createComponents(String fieldName) {

        CharArraySet stopSet = CharArraySet.copy(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        /*
        "a", "an", "and", "are", "as", "at", "be", "but", "by",
         "for", "if", "in", "into", "is", "it",
         "no", "not", "of", "on", "or", "such",
         "that", "the", "their", "then", "there", "these",
         "they", "this", "to", "was", "will", "with"
         */
                 
        stopSet.add("because");
        stopSet.add("were");
        stopSet.add("due");
        stopSet.add("where");
        stopSet.add("why");
        stopSet.add("what");
        stopSet.add("which");
        stopSet.add("when");
        stopSet.add("how");
        stopSet.add("can");
        stopSet.add("does");
        stopSet.add("must");
        stopSet.add("have");
        stopSet.add("been");
        stopSet.add("so");
        stopSet.add("can");
        stopSet.add("does");
        stopSet.add("has");
        stopSet.add(".");
        stopSet.add("results");
        stopSet.add("flow");
        stopSet.add("pressure");
        stopSet.add("number");
        stopSet.add("theory");
        stopSet.add("mach");
        stopSet.add("boundary");
        stopSet.add("given");
        stopSet.add("method");
        stopSet.add("obtained");
        stopSet.add("made");
        stopSet.add("layer");
        
        // My stopword list
    List<String> myStopWords = Arrays.asList(
            "a", "an", "and", "any", "are", "all", "as", "am", "at", "about", "another", "above", "after", "along", "amid", "among", "anyone", "anyplace", "anything", "anytime", "anywhere",
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
            "that", "the", "their", "then", "there", "these", "those", "they", "them", "this", "to", "till", "too", "theirs",
            "until", "under", "up", "us",
            "via", "vs",
            "we", "was", "were", "will", "with", "would", "when", "why", "where", "what", "who", "whom", "which", "whether", "whose", "whatever", "whenever", "whereever", "whichever", "whoever", "whomever",
            "you", "your", "yours");

    myStopWords.forEach((word) -> {
        stopSet.add(word);
        });
        
        Tokenizer src = new StandardTokenizer();
        
        TokenStream result = new EnglishPossessiveFilter(src);
        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopSet);
        result = new EnglishMinimalStemFilter(result);
        result = new PorterStemFilter(result);
        result = new CapitalizationFilter(result);
        return new Analyzer.TokenStreamComponents(src, result);
    }
}
