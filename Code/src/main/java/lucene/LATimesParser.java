/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucene;

import java.io.*;

public class LATimesParser {
    private static final String filesPath = "../Assignment Two Dataset/latimes";
    private static final String OUTPUT = "A2-DOC/";

    public static void main(String[] args) {

        Parse();
        //ParseFile(inputFile);
    }
    
    public static void Parse(){
        File folder = new File(filesPath);
        File[] files = folder.listFiles();

        for (File file : files) {
            try {
                LATimesParser.reformate(file);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public static void reformate(File file) throws IOException {
        char State = 'I';

        //open input file;
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader bufferedReader = new BufferedReader(isr);

        //create output file;
        File outputFile = new File(OUTPUT + file.getName());
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter(outputFile, false);
        PrintWriter p = new PrintWriter(fw);

        String currentLine;

        while ((currentLine = bufferedReader.readLine()) != null) {
            //System.out.println(currentLine);
            if (currentLine.equals("<P>") || currentLine.equals("</P>")) {
                continue;
            } else if (currentLine.startsWith("</DOC>")) {
                p.println("</DOC>");

            } else if (currentLine.startsWith("<DOCNO>")) {
                p.println("<DOC>");
                String Id = currentLine.split(">")[1].split("<")[0].trim();
                p.println("<DOCNO>");
                p.println(Id.trim());
                p.println("</DOCNO>");

            } else if (currentLine.equals("<HEADLINE>")) {
                p.println("<TITLE>");
                State = 'T';
            } else if (currentLine.equals("</HEADLINE>")) {
                p.println("</TITLE>");
                State = 'I';
            } else if (currentLine.equals("<TEXT>")) {
                p.println("<TEXT>");
                State = 'W';
            } else if (currentLine.equals("</TEXT>")) {
                p.println("</TEXT>");
                State = 'I';
            } else if (currentLine.equals("<SUBJECT>")) {
                p.println("<SUBJECT>");
                State = 'S';
            } else if (currentLine.equals("</SUBJECT>")) {
                p.println("</SUBJECT>");
                State = 'I';
            } else {
                switch (State) {
                    case 'T':
                        p.println(currentLine);

                        break;
                    case 'W':
                        p.println(currentLine);

                        break;
                    case 'S':
                        p.println(currentLine);

                        break;
                }
            }
        }
        bufferedReader.close();
        p.close();
    }
}
