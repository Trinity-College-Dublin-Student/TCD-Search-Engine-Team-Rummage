import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File; 
import java.util.ArrayList;

class FilesWalk {
	public static void main(String... args) {
		ArrayList<String> paths = new ArrayList<> ();
	    File[] files = new File("C:\\Users\\stiwari\\Downloads\\Assignment_Two").listFiles();
	    showFiles(files,paths);
	    
	    for(String s : paths) {
	    	System.out.println(s);
	    }
	}
	
	public static void showFiles(File[] files,ArrayList<String> paths) {
	    for (File file : files) {
	        if (file.isDirectory()) {
	            //System.out.println("Directory: " + file.getPath());
	            showFiles(file.listFiles() ,paths); // Calls same method again.
	        } else {
	        	paths.add(file.getPath());
	            //System.out.println("File: " + file.getPath());
	        }
	    }
	}
}