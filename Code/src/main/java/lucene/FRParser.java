package lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

class ParserFR {
	String baseDir = "";
	ArrayList<String> files = new ArrayList<String>();

	ParserFR(String path) {
		baseDir = path;
	}

	void createFileList() {
		ArrayList<String> dir = new ArrayList<String>();
		File currentDir = new File(baseDir);
		File[] dirList = currentDir.listFiles();
		for (File file : dirList) {
			try {
				files.add(file.getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

public class FRParser {
	//static Logger log = Logger.getLogger(Indexer.class.getName());

	public static void main(String[] args) {
		Parse();
	}

	public static void Parse() {
		//DOMConfigurator.configure("/home/abhishek/Desktop/TCD-DATA/lucene-2/log4j.xml");
		ParserFR p = new ParserFR("../Assignment Two Dataset/fr94_ParsedDoc/");
		p.createFileList();
		BufferedReader br = null;

		System.out.println(p.files.size());
		int k = 0;
		for (String str : p.files) {
			try {
				int len = str.split("/").length;
				String[] arr = str.split("/");
				String fNm = arr[len - 1];
				StringBuilder sb = new StringBuilder();
				System.out.println(fNm);
				// log.info("Current File Topic -> "+ topic);
				PrintWriter writer = new PrintWriter("A2-DOC/" + fNm, "UTF-8");
//				System.out.println(str);

				String line = null;
				k++;
				br = new BufferedReader(new FileReader(str));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				String[] content = sb.toString().split("</DOC>");
//				System.out.println(content.length);
				BufferedWriter bw = new BufferedWriter(writer);
				for (int i = 0; i < content.length; i++) {
					bw.write("<DOC>");
					String docno = StringUtils.substringBetween(content[i], "<DOCNO>", "</DOCNO>");
					// System.out.println(StringUtils.substringBetween(content[i], "<DOCNO>",
					// "</DOCNO>"));
					// text = text.replaceAll("\\<.*?\\>", "");
					String topic = "";
					//log.info("Processed " + fNm);
					
					String text = StringUtils.substringAfter(content[i], "</DOCNO>");
//					log.info("Data " + text);
					bw.write("<DOCNO>" + docno + "</DOCNO>");
//					bw.write("<TITLE>" + text.replaceAll("\\<.*?\\>", "") + "</TITLE>");
					bw.write("<TEXT>" + text.replaceAll("\\<.*?\\>", "").replaceAll("AGENCY:", "").replaceAll("SUMMARY:", "").replaceAll("ACTION:", "") + "</TEXT>");
					bw.write("</DOC>");

				}
				bw.flush();
				bw.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
