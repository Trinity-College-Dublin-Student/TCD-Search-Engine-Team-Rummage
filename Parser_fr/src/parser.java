

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;




public class parser {
	
	public static ArrayList<String> get_tag_values(Book b) {
		
		ArrayList<String> tags = new ArrayList<String>();
		tags.add(b.USDEPT);
		tags.add(b.USBUREAU);
		tags.add(b.CFRNO);
		tags.add(b.RINDOCK);
		tags.add(b.AGENCY);
		tags.add(b.SUMMARY);
		tags.add(b.DATE);
		//tags.add(b.ACTION);
		tags.add(b.FURTHER);
		//tags.add(b.BILLING);
		tags.add(b.SUPPLEM);
		tags.add(b.FRFILING);
		
		return tags;
	}
	
	public static void main(String [] args) throws IOException {
		
		String fileName = "null";
		
		System.out.println(args[1]);
        // The name of the file to open.
		if("Parse_Doc".equals(args[1])) {
			fileName = "/Users/sid/Downloads/cran/cran.all.1400"; Book.DocTParse = 0;
		}
		
		if("Parse_Queries".equals(args[1])) {
			fileName = "/Users/sid/Downloads/cran/cran.qry"; Book.DocTParse = 1;
		}
		
		Path source = Paths.get("/Users/sid/Desktop/ft");
	
		Stream<Path> paths = Files.walk(source).filter(Files::isRegularFile);
		
		for (Stream path : Stream) {
			System.out.println(path);
		}
  
		fileName = "/Users/sid/Desktop/fr940104.0";
		
		System.out.println(args[1]+ " " + fileName);
    	File file = new File(fileName);

        // This will reference one line at a time
        String line = null;
        

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(file);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);
            
            ArrayList<Book> list = new ArrayList<Book>();
            
            String state = "0";

            while((line = bufferedReader.readLine()) != null) {
            	
            	//System.out.println(line);
            	
            	if (line.equals("<DOC>"))
            	{  
            	   state = "D";
            	   Book book = new Book();
            	   list.add(book);
            	   //System.out.println(line);
            	   
            	}
            	else if(line.contains("<DOCNO>")) 
            	{
            		state = "DN";
            		Book book = list.get(list.size() - 1);
            		book.set_DOCNO(line);
            		//System.out.println(line);
            	}
            	else if (line.contains("<PARENT>"))
            	{
            		state = "P";
            		Book book = list.get(list.size() - 1);
            		book.set_PARENT(line);
            		//System.out.println(line);
            	}
            	else if (line.equals("<TEXT>"))
            	{
            		state = "T";
            		//System.out.println(line);
            	}
            	else if(line.contains("<USDEPT>")) {
            		state = "U";
            		Book book = list.get(list.size() - 1);
            		line = line.replace("<USDEPT>","");
            		line =line.replace("</USDEPT>","");
            		book.set_USDEPT(line);
            		//System.out.println(line);
            	}
            	else if(line.contains("<FRFILING>")) {
            		state = "F";
            		Book book = list.get(list.size() - 1);
            		line = line.replace("<FRFILING>","");
            		line =line.replace("</FRFILING>","");
            		book.set_BILLING(line);
            		//System.out.println(line);
            	}
            	else if(line.contains("<ACTION>")) {
            		state = "A";
            		Book book = list.get(list.size() - 1);
            		line = line.replace("<ACTION>","");
            		line =line.replace("</ACTION>","");
            		book.set_ACTION(line);
            		//System.out.println(line);
            	}
            	else if(line.contains("<USBUREAU>")) {
            		state = "US";
            		Book book = list.get(list.size() - 1);
            		line = line.replace("<USBUREAU>","");
            		line =line.replace("</USBUREAU>","");
            		book.set_USBUREAU(line);
            		//System.out.println(line);
            	}
            	else if(line.contains("<CFRNO>")) {
            		state = "C";
            		Book book = list.get(list.size() - 1);
            		line = line.replace("<CFRNO>","");
            		line =line.replace("</CFRNO>","");
            		book.set_CFRNO(line);
            		//System.out.println(line);
            	}
            	else if(line.contains("<RINDOCK>")) {
            		state = "R";
            		line = line.replace("<RINDOCK>","");
            		line =line.replace("</RINDOCK>","");
            		//System.out.println(line);
            	}
            	else if(line.contains("<AGENCY>")) {
            		state = "A";
            		//System.out.println(line);
            	}
            	else if(line.contains("<SUMMARY>")) {
            		state = "S";
            		//System.out.println(line);
            	}
            	else if(line.contains("<DATE>")) {
            		state = "D";
            		//System.out.println(line);
            	}
            	else if(line.contains("<SUPPLEM>")) {
            		state = "SU";
            		Book book = list.get(list.size() - 1);
            		line = line.replace("<SUPPLEM>","");
            		line =line.replace("</SUPPLEM>","");
            		book.set_USDEPT(line);
            		//System.out.println(line);
            	}
            	else {
          
            		Book book = list.get(list.size() - 1);
            		if (!line.contains("<")) {
	            		if (state == "T") {
	            			if(line.contains("<!--") == false) {book.set_TEXT(line);}	
	            		}
	            		if (state == "U") {
	            			if(line.contains("<!--") == false) {book.set_USDEPT(line);}	
	            		}
	            		if (state == "C") {
	            			if(line.contains("<!--") == false) {book.set_CFRNO(line);}				
	            		}
	            		if (state == "R") {
	            			if(line.contains("<!--") == false) {book.set_RINDOCK(line);}		
	            		}
	            		if (state == "A") {
	            			if(line.contains("<!--") == false) {book.set_AGENCY(line);}			
	            		}
	            		if (state == "S") {
	            			if(line.contains("<!--") == false) {book.set_SUMMARY(line);	}
	            		}
	            		if (state == "D") {
	            			if(line.contains("<!--") == false) {book.set_DATE(line);}		
	            		}
	            		if (state == "F") {
	            			if(line.contains("<!--") == false) {book.set_FRFILING(line);}	
	            		}
	            		if (state == "SU") {
	            			if(line.contains("<!--") == false) {book.set_SUPPLEM(line);}
	            		}
            		}
            	}
            } 
              
           String[] tags = new String[] {"USDEPT","USBUREAU","CFRNO", "RINDOCK", "AGENCY", "SUMMARY", "DATE", "FURTHER", "SUPPLEM","FRFILING"};

            if(Book.DocTParse == 0) {
	            int index = 1; // Comment out for parsing Cranfeild Collection
	            for (Book book : list) {
	            	
	            	//System.out.println(book.author);
	            	ArrayList<String> tag_values = get_tag_values(book);
	            	
	            	String fileName1 = "/Users/sid/Downloads/Cran_parsed_files/"+"Book"+Integer.toString(index)+".txt";
	            	File file2 = new File(fileName1);
	            	file2.createNewFile();
	            	
	            	// Assume default encoding.
	                FileWriter fileWriter =
	                    new FileWriter(file2);
	
	                // Always wrap FileWriter in BufferedWriter.
	                BufferedWriter bufferedWriter =
	                    new BufferedWriter(fileWriter);
	                
	                bufferedWriter.write("<DOC>");
	                bufferedWriter.newLine();
	                
	                bufferedWriter.write("<DOCNO>");
	                bufferedWriter.newLine();
	                bufferedWriter.write(book.DOCNO);
	                bufferedWriter.newLine();
	                bufferedWriter.write("</DOCNO>");
	                bufferedWriter.newLine();
	                
	                bufferedWriter.write("<PARENT>");
	                bufferedWriter.newLine();
	                bufferedWriter.write(book.PARENT);
	                bufferedWriter.newLine();
	                bufferedWriter.write("</PARENT>");
	                bufferedWriter.newLine();
	                bufferedWriter.write("<TEXT>");
	                
	               for(int i = 0 ; i < tags.length; i++) {
	            	   bufferedWriter.newLine();
	            	   bufferedWriter.write("<"+ tags[i] +">");
	            	   bufferedWriter.newLine();
	            	   bufferedWriter.write(tag_values.get(i));
	            	   bufferedWriter.newLine();
	            	   bufferedWriter.write("</"+ tags[i] +">");
	               }
	                
	                
	                bufferedWriter.newLine();
	                bufferedWriter.write(book.TEXT);
	                bufferedWriter.newLine();
	                bufferedWriter.write("</TEXT>");
	                bufferedWriter.newLine();
	                
	                bufferedWriter.write("</DOC>");
	                
	                // Always close files.
	                bufferedWriter.close();
	                fileWriter.close();
	                index = index + 1;
	    		} 
            }
            
            if(Book.DocTParse == 1) {
	           // Create a file for storing parsed queries
	           // Comment out for parsing Cranfeild Collection
	            String fileName1 = "/Users/sid/Downloads/Parsed_cranqrel/Parsed_cranqrel";
	        	File file2 = new File(fileName1);
	        	file2.createNewFile();
	        	
	        	// Assume default encoding.
	            FileWriter fileWriter = new FileWriter(file2);
	            
	            // Always wrap FileWriter in BufferedWriter.
	            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	            
	            for (Book book : list) {
	
	                bufferedWriter.write(book.text);
	                bufferedWriter.newLine();
	               
	    		}
	            
	         // Always close files.
	            bufferedWriter.close();
	            fileWriter.close();   
          }   
            
        } 
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
             ex.printStackTrace();
        }
    }
	
}

	class Book {
		
		public String doc = "<DOC>";
		public String text = "<TEXT>";
		public String DOCNO;
		public String PARENT;
		public String TEXT;
		public String USDEPT;
		public String USBUREAU;
		public String CFRNO;
		public String RINDOCK;
		public String AGENCY; 
		public String SUMMARY;
		public String DATE; 
		public String FURTHER;
		public String SUPPLEM;
		public String BILLING;
		public String FRFILING;
		public String ACTION;
		

		public static int DocTParse;
		
		public static String escapeMetaCharacters(String inputString){
		    final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")","*","+","?","|","<",">","-","&","%"};

		    for (int i = 0 ; i < metaCharacters.length ; i++){
		        if(inputString.contains(metaCharacters[i])){
		            inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
		        }
		    }
		    return inputString;
		}

		
		Book(){
			this.DOCNO = "Not Specified";
			this.PARENT = "Not Specified";
			this.TEXT = "Not Specified";
			this.USDEPT = "Not Specified";
			this.USBUREAU = "Not Specified";
			this.CFRNO   = "Not Specified";
			this.RINDOCK = "Not Specified";
			this.AGENCY = "Not Specified";
			this.SUMMARY = "Not Specified";
			this.DATE = "Not Specified";
			this.FURTHER = "Not Specified";
			this.SUPPLEM = "Not Specified";
			this.BILLING = "Not Specified";
			this.FRFILING = "Not Specified";
			this.ACTION = "Not Specified";;
		}
		
		public void set_DOCNO(String line) {
			
			line = line.replaceAll("<DOCNO>", "");
			line = line.replaceAll("</DOCNO>", "");
			this.DOCNO = line;
			
		}
		
		public void set_PARENT(String line) {
			line = line.replaceAll("<PARENT>", "");
			line = line.replaceAll("</PARENT>", "");
			this.PARENT = line;
		}
		
		public void set_TEXT(String line) {
			if(this.text == "Not Specified") {
				if (DocTParse == 0) {
					//line = escapeMetaCharacters(line); 
					this.TEXT = line;
				}
				else {
					//line = escapeMetaCharacters(line);
					//text = "context:"+ line;
					this.TEXT = line;
				}
			}
			else {
				//line = escapeMetaCharacters(line);
				this.TEXT = this.TEXT + " " + line;
			}
		}
		
		public void set_USDEPT(String line) {
			if(this.USDEPT == "Not Specified") {this.USDEPT = line;}
			else { this.USDEPT = this.USDEPT + " " + line;}
		}
		
		public void set_USBUREAU(String line) {
			if(this.USBUREAU == "Not Specified") {this.USBUREAU = line;}
			else {this.USBUREAU = this.USBUREAU + " " + line;}
		}
		
		public void set_CFRNO(String line) {
			if(this.CFRNO == "Not Specified") {this.CFRNO = line;}
			else {this.CFRNO = this.CFRNO + " " + line;}
		}
		public void set_RINDOCK(String line) {
			if(this.RINDOCK == "Not Specified") {this.RINDOCK = line;}
			else {this.RINDOCK = this.RINDOCK + " " + line;}
		}
		public void set_AGENCY(String line) {
			if(this.AGENCY == "Not Specified") {this.AGENCY = line;}
			else {this.AGENCY = this.AGENCY + " " + line;}
		}
		public void set_SUMMARY(String line) {
			if(this.SUMMARY == "Not Specified") {this.SUMMARY = line;}
			else {this.SUMMARY = this.SUMMARY + " " + line;}
		}
		public void set_DATE(String line) {
			if(this.DATE == "Not Specified") {this.DATE = line;}
			else {this.DATE = this.DATE + " " + line;}
		}
		public void set_SUPPLEM(String line) {
			if(this.SUPPLEM == "Not Specified") {this.SUPPLEM = line;}
			else {this.SUPPLEM = this.SUPPLEM + " " + line;}
		}	
		public void set_FURTHER(String line) {
			if(this.DATE == "Not Specified") {this.DATE = line;}
			else {this.FURTHER = this.FURTHER + " " + line;}
		}
		public void set_BILLING(String line) {
			if(this.BILLING  == "Not Specified") {this.BILLING  = line;}
			else {this.BILLING  = this.BILLING  + " " + line;}
		}
		public void set_FRFILING(String line) {
			if(this.FRFILING  == "Not Specified") {this.FRFILING = line;}
			else {this.FRFILING  = this.FRFILING + " " + line;}
		}
		public void set_ACTION(String line) {
			if(this.ACTION  == "Not Specified") {this.ACTION = line;}
			else {this.ACTION  = this.ACTION + " " + line;}
		}	
		
	}

