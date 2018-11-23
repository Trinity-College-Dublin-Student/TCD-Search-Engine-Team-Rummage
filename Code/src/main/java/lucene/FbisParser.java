package lucene;

import java.io.*;

public class FbisParser {

    private static final String filesPath = "../Assignment Two Dataset/fbis";
    private static final String OUTPUT = "A2-DOC/";

    public static void main(String[] args) {

        Parse();
    }

    public static void Parse() {
        File folder = new File(filesPath);
        File[] files = folder.listFiles();

        for (File file : files) {
            try {
                FbisParser.reformate(file);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static void reformate(File file) throws IOException {
        //open input file;
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader br = new BufferedReader(isr);

        //create output file;
        File outputFile = new File(OUTPUT + file.getName());
        if (!outputFile.exists()) {
            outputFile.getParentFile().mkdirs();
        }
        FileWriter fw = new FileWriter(outputFile, false);
        PrintWriter p = new PrintWriter(fw);

        String lineTxt;
        String state = "FINISH";
        while ((lineTxt = br.readLine()) != null) {
            lineTxt = lineTxt.trim();
            if (lineTxt.matches("<DOC>")) {
                p.println(lineTxt);
            } else if (lineTxt.matches("</DOC>")) {
                p.println(lineTxt);
                p.println();
            } else if(lineTxt.matches("<DOCNO>.*</DOCNO>$")) {
                p.println("<DOCNO>");
                String ID = lineTxt.replace("<DOCNO>", "").replace("</DOCNO>", "");
                p.println(ID.trim());
                p.println("</DOCNO>");
            } else if (lineTxt.matches("<H3> <TI>.*</TI></H3>$")) {
                p.println("<TITLE>");
                String title = lineTxt.replace("<H3> <TI>", "").replace("</TI></H3>", "");
                p.println(title.trim());
                p.println("</TITLE>");
            } else if (lineTxt.matches("<H3> <TI>.*")) {
                p.println("<TITLE>");
                String title = lineTxt.replace("<H3> <TI>", "");
                p.println(title.trim());
            } else if (lineTxt.matches(".*</TI></H3>$")) {
                String title = lineTxt.replace("</TI></H3>", "");
                p.println(title.trim());
                p.println("</TITLE>");
            } else if (lineTxt.matches("<TEXT>")) {
                state = "TEXT";
                p.println(lineTxt);
            } else if (lineTxt.matches("</TEXT>")) {
                p.println(lineTxt);
                state = "FINISH";
            } else if (state.equals("TEXT")) {
                p.println(lineTxt);
            } else;
        }
        br.close();
        p.close();
    }
}
