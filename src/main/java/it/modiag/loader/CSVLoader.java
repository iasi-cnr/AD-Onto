package it.modiag.loader;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.log4j.Logger;

/**
 * 
 * @author rvoyat
 *
 */
public class CSVLoader { 
	
	public static final char TSV_delimiter = '\t'; 
	public static final char CSV_delimiter = ';'; 
	
	public  Logger logger = Logger.getLogger(getClass()); 

	public CSVParser load(Path filePath, char fieldDelimiter) { 
		CSVParser csvParser = null;
		try { 
	        Reader reader = Files.newBufferedReader(filePath); 
	        csvParser = CSVFormat.RFC4180.withFirstRecordAsHeader().withDelimiter(fieldDelimiter).parse(reader); 

		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
		return csvParser;
	} 

 

}
