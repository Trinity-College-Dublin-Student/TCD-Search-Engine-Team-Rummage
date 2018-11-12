package CS7IS3;

import java.io.*;

public class CheckDoc {
    private static String filesPath = "/Users/mengxuan/Desktop/groupAssignment/reformatedDOC/fbis/";

    public static void main(String[] args) {
        File folder = new File(filesPath);
        File[] files = folder.listFiles();

        for(File file : files) {
            try {
                if(CheckDoc.check(file).equals("Succeed")) continue;
                System.out.println(CheckDoc.check(file));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private static String check(File file) throws IOException {
        //open input file;
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "utf-8");
        BufferedReader br = new BufferedReader(isr);

        int countDOC = 0;
        int countTITLE = 0;
        String lineTxt;
        while ((lineTxt = br.readLine()) != null) {
            lineTxt = lineTxt.trim();
            if(lineTxt.matches("<DOC>")) {
                countDOC++;
            } else if(lineTxt.matches("<TITLE>")) {
                countTITLE++;
            } else;
        }
        br.close();
        if(countDOC != countTITLE) {
            return "Failed in:" + file.getName();
        } else return "Succeed";
    }
}
