/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class LATimesParser {

    public static void main(String[] args) throws FileNotFoundException {

        
        Parse();
        //ParseFile(inputFile);
    }
    
    public static void Parse(){
        new File("Parsed").mkdir();

        File folder = new File("..\\Assignment Two Dataset\\latimes");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                //System.out.println(i);
                ParseFile(listOfFiles[i].getAbsolutePath());
            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }

    private static void ParseFile(String inputFile) {
        char State = 'I';

        try {
            FileReader reader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(reader);

            
            String[] split = inputFile.split("\\\\");
            String fileName = split[split.length-1];
            
            FileWriter writer = new FileWriter("A2-DOC/" + fileName + ".txt");
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            String currentLine;

            while ((currentLine = bufferedReader.readLine()) != null) {
                //System.out.println(currentLine);
                if (currentLine.equals("<P>") || currentLine.equals("</P>")) {
                    continue;
                } else if (currentLine.startsWith("</DOC>")) {
                    bufferedWriter.write("</DOC>");

                } else if (currentLine.startsWith("<DOCNO>")) {
                    String Id = currentLine.split(">")[1].split("<")[0].trim();

                    bufferedWriter.write("<DOC>");
                    bufferedWriter.newLine();

                } else if (currentLine.equals("<HEADLINE>")) {
                    bufferedWriter.write("<TITLE>");
                    bufferedWriter.newLine();
                    State = 'T';
                } else if (currentLine.equals("</HEADLINE>")) {
                    bufferedWriter.write("</TITLE>");
                    bufferedWriter.newLine();
                    State = 'I';
                } else if (currentLine.equals("<TEXT>")) {
                    bufferedWriter.write("<TEXT>");
                    bufferedWriter.newLine();
                    State = 'W';

                } else if (currentLine.equals("</TEXT>")) {
                    bufferedWriter.write("</TEXT>");
                    bufferedWriter.newLine();
                    State = 'I';
                } else if (currentLine.equals("<SUBJECT>")) {
                    bufferedWriter.write("<SUBJECT>");
                    bufferedWriter.newLine();
                    State = 'S';

                } else if (currentLine.equals("</SUBJECT>")) {
                    bufferedWriter.write("</SUBJECT>");
                    bufferedWriter.newLine();
                    State = 'I';
                } else {
                    switch (State) {
                        case 'T':
                            bufferedWriter.write(currentLine);
                            bufferedWriter.newLine();
                            break;
                        case 'W':
                            bufferedWriter.write(currentLine);
                            bufferedWriter.newLine();
                            break;
                        case 'S':
                            bufferedWriter.write(currentLine);
                            bufferedWriter.newLine();
                            break;
                    }
                }

            }
            bufferedWriter.close();
            
        } catch (Exception ex) {
            System.out.println("ERROR: " + ex);
            System.exit(1);
        }
    }
}
