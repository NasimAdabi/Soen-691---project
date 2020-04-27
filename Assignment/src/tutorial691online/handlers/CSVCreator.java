package tutorial691online.handlers;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class CSVCreator {
	public void createCSV(String outputFileName, Map<String, Integer> metricItems) throws URISyntaxException{
		try {
			//URL projectPath = getClass().getProtectionDomain().getCodeSource().getLocation();
			//String pathToSave = projectPath.toURI()+"/MetricFiles/"+outputFileName.trim()+".csv";
			
			//Path root = FileSystems.getDefault().getPath("").toAbsolutePath();
			//Path pathToSave = Paths.get(root.toString(),"src", "main", "resources", outputFileName+".csv");
			//String path = new File(".").getAbsolutePath();
		    
			String filePath = "/Users/nasim/git/Soen-691---project/MetricFiles/"+outputFileName.trim()+".csv";
			
		    FileWriter fileWriter = new FileWriter(filePath); 
		    
	        CSVWriter csvWriter = new CSVWriter(fileWriter);
	        for(Map.Entry<String, Integer> item : metricItems.entrySet()) {
	        	String fileName = item.getKey();
	        	int value = item.getValue();
	        	
	        	csvWriter.writeNext(new String[]{fileName, value + ""});
	        }
	        csvWriter.close();
	        	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
