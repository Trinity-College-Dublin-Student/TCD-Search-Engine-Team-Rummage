/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;

/**
 * Simple command-line based search demo.
 */
public class Searcher {

    /**
     * Simple command-line based search demo.
     */
    public static void main(String[] args) throws Exception {
        String usage
                = "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }

        File dir = new File("output");
        if (dir.isDirectory()) {
            for (File f : dir.listFiles()) {
                f.delete();
            }
        }

        String index = "BM25";
        String field = "Text";
        String queries = null;
        int repeat = 0;
        boolean raw = false;
        String queryString = null;
        int hitsPerPage = 1000;

        for (int i = 0; i < args.length; i++) {
            if ("-index".equals(args[i])) {
                index = args[i + 1];
                i++;
            } else if ("-field".equals(args[i])) {
                field = args[i + 1];
                i++;
            } else if ("-queries".equals(args[i])) {
                queries = args[i + 1];
                i++;
            } else if ("-query".equals(args[i])) {
                queryString = args[i + 1];
                i++;
            } else if ("-repeat".equals(args[i])) {
                repeat = Integer.parseInt(args[i + 1]);
                i++;
            } else if ("-raw".equals(args[i])) {
                raw = true;
            } else if ("-paging".equals(args[i])) {
                hitsPerPage = Integer.parseInt(args[i + 1]);
                if (hitsPerPage <= 0) {
                    System.err.println("There must be at least 1 hit per page.");
                    System.exit(1);
                }
                i++;
            }
        }

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new StandardAnalyzer();
        //Analyzer analyzer = new StandardAnalyzer(stopSet);        
        //Analyzer analyzer = new StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        //Analyzer analyzer = new StopAnalyzer();
        //Analyzer analyzer = new EnglishAnalyzer();
        //Analyzer analyzer = new CustomAnalyzer();

        BufferedReader in = null;
        if (queries != null) {
            File parsedFile = ParseQueryFile(Paths.get(queries).toFile(), index);
            in = Files.newBufferedReader(parsedFile.toPath(), StandardCharsets.UTF_8);
        } else {
            in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        }
        QueryParser parser = new QueryParser(field, analyzer);

        int queryCounter = 0;

        while (true) {
            queryCounter++;
            if (queries == null && queryString == null) {                        // prompt the user
                System.out.println("Enter query: ");
            }

            String line = queryString != null ? queryString : in.readLine();

            if (line == null || line.length() == -1) {
                break;
            }

            line = line.trim();
            if (line.length() == 0) {
                break;
            }

            Query query = parser.parse(line);
            System.out.println("Searching for: " + query.toString(field));

            if (repeat > 0) {                           // repeat & time as benchmark
                Date start = new Date();
                for (int i = 0; i < repeat; i++) {
                    searcher.search(query, 100);
                }
                Date end = new Date();
                System.out.println("Time: " + (end.getTime() - start.getTime()) + "ms");
            }

            doPagingSearch(in, searcher, query, hitsPerPage, raw, queries == null && queryString == null, queries != null, queryCounter);

            if (queryString != null) {
                break;
            }
        }
        reader.close();
    }

    /**
     * This demonstrates a typical paging search scenario, where the search
     * engine presents pages of size n to the user. The user can then go to the
     * next page if interested in the next hits.
     *
     * When the query is executed for the first time, then only enough results
     * are collected to fill 5 result pages. If the user wants to page beyond
     * this limit, then the query is executed another time and all hits are
     * collected.
     *
     */
    public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query,
            int hitsPerPage, boolean raw, boolean interactive, boolean queriesFile, int queryCounter) throws IOException {

        // Collect enough docs to show 5 pages
        //searcher.setSimilarity(new ClassicSimilarity());
        searcher.setSimilarity(new BM25Similarity());

        TopDocs results = searcher.search(query, 5 * hitsPerPage);
        ScoreDoc[] hits = results.scoreDocs;

        int numTotalHits = Math.toIntExact(results.totalHits);
        System.out.println(numTotalHits + " total matching documents");

        int start = 0;
        int end = Math.min(numTotalHits, hitsPerPage);

        new File("output//").mkdir();

        FileWriter fileWriter = new FileWriter("output//Results.txt", true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        while (true) {
            if (end > hits.length) {
                System.out.println("Only results 1 - " + hits.length + " of " + numTotalHits + " total matching documents collected.");
                System.out.println("Collect more (y/n) ?");
                String line = in.readLine();
                if (line.length() == 0 || line.charAt(0) == 'n') {
                    break;
                }

                hits = searcher.search(query, numTotalHits).scoreDocs;
            }

            end = Math.min(hits.length, start + hitsPerPage);

            for (int i = start; i < end; i++) {
                if (raw) {                              // output raw format
                    System.out.println("doc=" + hits[i].doc + " score=" + hits[i].score);
                    continue;
                }

                Document doc = searcher.doc(hits[i].doc);
                String path = doc.get("DOCNO");
                float score = hits[i].score;
                if (path != null) {
                	
                    if (queriesFile) {
                        bufferedWriter.write(queryCounter + " 0 " + getFileName(path) + " " + (int) ((hits[i].score / hits[0].score) * 5) + " " + hits[i].score + " STANDARD");
                        bufferedWriter.newLine();
                    }

                    System.out.println((i + 1) + ". " + path + "Score: " + score);
                    String title = doc.get("title");
                    if (title != null) {
                        System.out.println("   Title: " + doc.get("title"));
                    }

                } else {
                    System.out.println((i + 1) + ". " + "No path for this document");
                }

            }

            if (!interactive || end == 0) {
                break;
            }

            if (numTotalHits >= end) {
                boolean quit = false;
                while (true) {
                    System.out.print("Press ");
                    if (start - hitsPerPage >= 0) {
                        System.out.print("(p)revious page, ");
                    }
                    if (start + hitsPerPage < numTotalHits) {
                        System.out.print("(n)ext page, ");
                    }
                    System.out.println("(q)uit or enter number to jump to a page.");

                    String line = in.readLine();
                    if (line.length() == 0 || line.charAt(0) == 'q') {
                        quit = true;
                        break;
                    }
                    if (line.charAt(0) == 'p') {
                        start = Math.max(0, start - hitsPerPage);
                        break;
                    } else if (line.charAt(0) == 'n') {
                        if (start + hitsPerPage < numTotalHits) {
                            start += hitsPerPage;
                        }
                        break;
                    } else {
                        int page = Integer.parseInt(line);
                        if ((page - 1) * hitsPerPage < numTotalHits) {
                            start = (page - 1) * hitsPerPage;
                            break;
                        } else {
                            System.out.println("No such page");
                        }
                    }
                }
                if (quit) {
                    break;
                }
                end = Math.min(numTotalHits, start + hitsPerPage);
            }
        }

        bufferedWriter.close();
        fileWriter.close();
    }

    private static File ParseQueryFile(File file, String index) throws FileNotFoundException, IOException {

        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        ArrayList<SearchQuery> queryList = new ArrayList();

        String currentLine;
        char State = 'I';
        while ((currentLine = bufferedReader.readLine()) != null) {
            currentLine = currentLine.replace("?", "");

            if (currentLine.equals("<top>") || currentLine.equals("") || currentLine.equals("</top>")) {
                continue;
            } else if (currentLine.startsWith("<num>")) {
                int Id = Integer.parseInt(currentLine.split(":")[1].trim());

                SearchQuery query = new SearchQuery();
                query.setID(Id);
                queryList.add(query);
            } else if (currentLine.startsWith("<title>")) {
                State = 'T';
                SearchQuery currentQuery = queryList.get(queryList.size() - 1);
                String Title = currentLine.split(">")[1];

                currentQuery.setTitle(Title);
                currentQuery.setCombined(Title);
            } else if (currentLine.startsWith("<desc>")) {
                State = 'D';
                SearchQuery currentQuery = queryList.get(queryList.size() - 1);
                String Desc = currentLine.split(":")[1];

                currentQuery.setDescription(Desc);
                currentQuery.setCombined(Desc);
            } else if (currentLine.startsWith("<narr>")) {
                State = 'N';
                SearchQuery currentQuery = queryList.get(queryList.size() - 1);
                String Narr = currentLine.split(":")[1];

                currentQuery.setNarrative(Narr);
                currentQuery.setCombined(Narr);
            } else {
                SearchQuery currentQuery = queryList.get(queryList.size() - 1);

                //remove non alphanumeric chars     
                currentLine = currentLine.replaceAll("[^A-Za-z0-9 ]", "");
                currentLine = currentLine.trim().replaceAll(" +", " ");
                System.out.println("State= " + State + "\ncurrent=" + currentLine);
                switch (State) {
                    case 'T':
                        String Title = currentLine;
                        currentQuery.setTitle(Title);
                        currentQuery.setCombined(Title);
                        break;
                    case 'D':
                        String Desc = currentLine;
                        currentQuery.setDescription(Desc);
                        currentQuery.setCombined(Desc);
                        break;
                    case 'N':
                        String Narr = currentLine;
                        currentQuery.setNarrative(Narr);
                        currentQuery.setCombined(Narr);
                        break;
                    default:
                        System.err.println("Undefined State:" + State);
                        System.exit(2);

                }
            }
        }
        bufferedReader.close();

        String test = file.getParent();
        File output;
        if (test != null) {
            output = new File(test + "//Parsed.qry");
        } else {
            output = new File("Parsed.qry");
        }
        FileWriter fileWriter = new FileWriter(output);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        int counter = 1;

        for (SearchQuery query : queryList) {
            /*if (query.Title != null) {
                bufferedWriter.write(query.Title);
                bufferedWriter.newLine();
            }
            if (query.Description != null) {
                bufferedWriter.write(query.Description);
                bufferedWriter.newLine();
            }
            if (query.Narrative != null) {
                bufferedWriter.write(query.Narrative);
                bufferedWriter.newLine();
             */
            if (query.Combined != null) {
                bufferedWriter.write(query.Combined);
                bufferedWriter.newLine();
            }
            counter++;
        }

        bufferedWriter.close();

        return output;
    }

    private static String getFileName(String path) {
        String[] split = path.split("\\\\");

        String x = split[split.length - 1];

        String[] ans = x.split("\\.");

        if (ans[0].contains("/")) {
            String[] ubuntuAns = ans[0].split("/");
            return ubuntuAns[ubuntuAns.length - 1];
        }
        return ans[0];
    }
}

class SearchQuery {

    int ID;
    String Title;
    String Description;
    String Narrative;
    String Combined;

    public int getID() {
        return ID;
    }

    public String getCombined() {
        return Combined;
    }

    public void setCombined(String Combined) {
        if (this.Combined == null) {
            this.Combined = Combined;
        } else {
            this.Combined += " " + Combined;
        }
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        if (this.Title == null) {
            this.Title = Title;
        } else {
            this.Title += " " + Title;
        }
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        if (this.Description == null) {
            this.Description = Description;
        } else {
            this.Description += " " + Description;
        }
    }

    public String getNarrative() {
        return Narrative;
    }

    public void setNarrative(String Narrative) {
        if (this.Narrative == null) {
            this.Narrative = Narrative;
        } else {
            this.Narrative += " " + Narrative;
        }
    }

}
