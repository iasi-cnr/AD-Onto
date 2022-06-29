/**
 * 
 */
package it.modiag;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import it.modiag.db.DBManager;
import it.modiag.manager.OntologyManager;
import it.modiag.util.ADNIOntologyUtil;

/**
 * @author rvoyat
 *
 */
public class Main {

	/**
	 * @param args
	 * args[0]  = files ADNI mappings and raw dir
	 * args[1]  = DB dir
	 * args[2]  = Ontology model file ( complete path)
	 * 
	 */
	public static void main(String[] args) {
		 
		 
		Main main = new Main();
		Properties props;
		try {
			props = main.getProperiesFile();
			main.load(props);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public void load(Properties props) throws IOException {
		
		ADNIOntologyUtil.setFilesHomeDir(props.getProperty("files.home.dir")); 
		ADNIOntologyUtil.setDbHomeDir(props.getProperty("db.home.dir")); 
		ADNIOntologyUtil.setModelPath(props.getProperty("owl.model.path")); 
		
		List<String> datasets = Arrays.asList(new String[] {"npi"});
		
		//Load OWL model
		DBManager.getInstance().loadModel();

		//Load ADNI Dataset 
		new OntologyManager().loadRawFileOnOntology(datasets); 

		//Print model on log
		//DBManager.getInstance().printModel();

		//Close db connection
		DBManager.getInstance().closeDBConnection();
	}
	
	 public Properties getProperiesFile() throws IOException {
		 Properties result = new Properties();
		 result.load(getClass().getClassLoader().getResourceAsStream("paths.properties"));
		 return result;
	 }

}
