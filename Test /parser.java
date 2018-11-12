package org.apache.lucene.demo.my_app;

import java.io.*;
import java.util.ArrayList;

public class parser {
	public static void main(String [] args) {
		
		String fileName = "null";
		
		System.out.println(args[1]);
        // The name of the file to open.
		if("Parse_Doc".equals(args[1])) {
			fileName = "/Users/sid/Downloads/cran/cran.all.1400"; Book.DocTParse = 0;
		}
		
		if("Parse_Queries".equals(args[1])) {
			fileName = "/Users/sid/Downloads/cran/cran.qry"; Book.DocTParse = 1;
		}
		
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
            
            char state = '0';

            while((line = bufferedReader.readLine()) != null) {
            	
            	String tag = line.substring(0, 2);
            	
            	if (tag.contains(".I"))
            	{  
            	   state = 'I';
            	   Book book = new Book();
            	   list.add(book);
            	   //System.out.println(line);
            	   
            	}
            	else if(tag.contains(".T") &  line.length() == 2) 
            	{
            		state = 'T';
            		//System.out.println(line);
            	}
            	else if (tag.contains(".A") & line.length() == 2) 
            	{
            		state = 'A';
            		//System.out.println(line);
            	}
            	else if (tag.contains(".B") & line.length() == 2)
            	{
            		state = 'B';
            		//System.out.println(line);
            	}
            	else if (tag.contains(".W") & line.length() == 2)
            	{
            		state = 'W';
            		//System.out.println(line);
            	}
            	else {
            		Book book = list.get(list.size() - 1);
            		//System.out.println(state);
            		//System.out.println(line);
            		
            		if(state == 'T') {
            			book.set_title(line);
            			//System.out.println(line);
            		}
            		
            		else if(state == 'A') {
            			book.set_author(line);
            			//System.out.println(line);
            			
            		}
            		
            		else if(state == 'B') {
            			book.set_biblo(line);
            			//System.out.println(line);
            			
            		}
            		
            		else if (state == 'W') {
            			book.set_text(line);
            			//System.out.println(line);
            			
            		}
            	
            	}
            }
              

            if(Book.DocTParse == 0) {
	            int index = 1; // Comment out for parsing Cranfeild Collection
	            for (Book book : list) {
	            	
	            	//System.out.println(book.author);
	            	
	            	
	            	String fileName1 = "/Users/sid/Downloads/Cran_parsed_files/"+"Book"+Integer.toString(index)+".txt";
	            	File file2 = new File(fileName1);
	            	file2.createNewFile();
	            	
	            	// Assume default encoding.
	                FileWriter fileWriter =
	                    new FileWriter(file2);
	
	                // Always wrap FileWriter in BufferedWriter.
	                BufferedWriter bufferedWriter =
	                    new BufferedWriter(fileWriter);
	                
	                bufferedWriter.write(book.title);
	                bufferedWriter.newLine();
	                bufferedWriter.write(book.author);
	                bufferedWriter.newLine();
	                bufferedWriter.write(book.biblo);
	                bufferedWriter.newLine();
	                bufferedWriter.write(book.text);
	                
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
            // ex.printStackTrace();
        }
    }
	
}

	class Book {
		
		public String title;
		public String author;
		public String biblo;
		public String text;
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
			this.title = "Not Specified";
			this.author = "Not Specified";
			this.biblo = "Not Specified";
			this.text = "Not Specified";
		}
		
		public void set_title(String line) {
			if(this.title == "Not Specified") {
				title ="./ " + line;
			}
			else {
				title = title + " " + line;
			}
		}
		
		public void set_author(String line) {
			if(this.author == "Not Specified") {
				author ="./ "+ line;
			}
			else {
				author = author+" "+ line;
			}
		}
		
		public void set_biblo(String line) {
			if(this.biblo == "Not Specified") {
				biblo = "./ " + line;
			}
			else {
				biblo = biblo + " " + line;
			}
		}
		public void set_text(String line) {
			if(this.text == "Not Specified") {
				if (DocTParse == 0) {
					line = escapeMetaCharacters(line); 
					text = "./ " + line;
				}
				else {
					line = escapeMetaCharacters(line);
					//text = "context:"+ line;
					text = line;
				}
			}
			else {
				line = escapeMetaCharacters(line);
				text = text+ " " + line;
			}
		}
		
		
	}

