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

class ParserFT3 {
	String baseDir = "";
	ArrayList<String> files = new ArrayList<String>();

	ParserFT3(String path) {
		baseDir = path;
	}

	void createFileList() {
		ArrayList<String> dir = new ArrayList<String>();
		File currentDir = new File(baseDir);
		File[] dirList = currentDir.listFiles();
		for (File file : dirList) {
			try {
				if (file.isDirectory()) {
					dir.add(file.getCanonicalPath());
					// files.add(file.getCanonicalPath());
					File curDir = new File(file.getCanonicalPath());
					File[] curFiles = curDir.listFiles();
					ArrayList<String> tempList = new ArrayList<String>();
					for (File str : curFiles) {
						tempList.add(str.getCanonicalPath());
					}
					files.addAll(tempList);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

public class ParserFT2 {
	public static void main(String [] args) {
		Parse();
	}
	public static void Parse(){
		ParserFT3 p = new ParserFT3("../Assignment Two Dataset/ft/");
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
				
//				System.out.println(str);

				String line = null;
				k++;
				br = new BufferedReader(new FileReader(str));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				String[] content = sb.toString().split("</DOC>");
//				System.out.println(content.length);
				
				for (int i = 0; i < content.length; i++) {
					String docno = StringUtils.substringBetween(content[i], "<DOCNO>", "</DOCNO>");
					PrintWriter writer = new PrintWriter("A2-DOC/" + fNm, "UTF-8");
					BufferedWriter bw = new BufferedWriter(writer);
					bw.write("<DOC>");
					
					//System.out.println(StringUtils.substringBetween(content[i], "<DOCNO>", "</DOCNO>"));
					String topic = "";
					try {
						topic = StringUtils.substringBetween(content[i], "<HEADLINE>", "</HEADLINE>").trim(); // Topic
					} catch (Exception e) {
						topic = StringUtils.substringBetween(content[i], "-{EADLANE-", "</DATE>").trim(); // Topic
					}
					String text = "";
					if (StringUtils.contains(content[i], "<TEXT>")) {
						text = StringUtils.substringBetween(content[i], "<TEXT>", "</TEXT>").trim(); // Topic
					} else {
						text = StringUtils.substringBetween(content[i], "<DATELINE>", "</DATELINE>").trim(); // Topic
					}
					bw.write("<DOCNO>" +docno+ "</DOCNO>");
					bw.write("<TITLE>" + topic + "</TITLE>");
					bw.write("<TEXT>" + text + "</TEXT>");
					bw.write("</DOC>");
					bw.flush();
					bw.close();

				}

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