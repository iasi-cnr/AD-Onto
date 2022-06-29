package it.modiag.manager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.sparql.util.StringUtils;
import com.hp.hpl.jena.vocabulary.RDF;

import it.modiag.db.DBManager;
import it.modiag.loader.CSVLoader; 
import it.modiag.util.ADNIOntologyUtil;
import it.modiag.value.ADNIMapping;
import it.modiag.value.ADNIVariableTransfomation;

/**
 * 
 * @author rvoyat
 *
 */
public class OntologyManager { 
	
	private CSVLoader csvLoader;    
	public  Logger logger = Logger.getLogger(getClass());
	private long id = 0;  
	
	/**
	 * Load ADNI table raw file
	 * 
	 * @param datasets
	 * @throws IOException
	 */
	public void loadRawFileOnOntology(List<String> datasets) throws IOException { 
		
		String home_dir  = ADNIOntologyUtil.getFilesHomeDir();
		List<ADNIVariableTransfomation> transformationValues = loadTransfValues(Paths.get(home_dir,String.format("%s.%s.%s", "adni","transformations","csv")), CSVLoader.CSV_delimiter);
		
		datasets.forEach(dataset -> {
			try {
				id = 0;
				List<CSVRecord> ontologyMappings = getCsvLoader().load(Paths.get(home_dir.concat(String.format("%s.%s.%s", dataset,"mapping","csv"))), CSVLoader.CSV_delimiter).getRecords();
				CSVParser parser = getCsvLoader().load(Paths.get(home_dir.concat(dataset).concat(".tsv")), CSVLoader.TSV_delimiter);
				
				parser.getRecords().forEach(csvRecord -> { 
	            		insertInADNIOntology(csvRecord,dataset.toUpperCase(), parser.getHeaderMap(),ontologyMappings,transformationValues, getId());
				});
			} catch(Exception e) {
				logger.error(e.getMessage(),e);
			}
		});
	}
	
	/**
	 * Insert record from ADNI table raw in ontology DB
	 * 
	 * @param record
	 * @param datasetPrefix
	 * @param csvHeader
	 * @param ontologyMappings
	 * @param transformationValues
	 * @param id
	 */
	private void insertInADNIOntology(CSVRecord record,String datasetPrefix, Map<String,Integer> csvHeader, List<CSVRecord> ontologyMappings,List<ADNIVariableTransfomation> transformationValues, long id) {

		try {
			String RID = record.get(csvHeader.get(ADNIOntologyUtil.RID_HEADER));  
			
			
			ontologyMappings.forEach(csvRecord -> {
				
				if(csvRecord != null && csvRecord.size()>1) {
					
					ADNIMapping adniMapping = new ADNIMapping(csvRecord);
					
					String value = extractValue(record, csvHeader, adniMapping,datasetPrefix, transformationValues);
					
					if (isNotMissingValue(value)) { 
						
						int indexObj = 0; 
						for(String nodoCorrente:adniMapping.getOwlXpath()) {
							
							boolean primoNodo = false;
							boolean ultimoNodo = false;
							String nodoPrecedente = null;
							
							if(indexObj == 0) {
								//Primo nodo ontologia
								primoNodo = true;
							} else {
								nodoPrecedente = adniMapping.getOwlXpath()[(indexObj-1)];
							}
							
							//Test se si tratta dell'ultimo nodo dell'ontologia ( l'attributo in cui inserire il valore
							if( (indexObj+1) == adniMapping.getOwlXpath().length) {
								ultimoNodo = true;
							}
							
							if(primoNodo) {
								//Compongo l'ontologia al primo nodo
								DBManager.getInstance().addStatement(ADNIOntologyUtil.MODEL_URI, 
										ADNIOntologyUtil.prefix.concat(nodoCorrente).concat(ADNIOntologyUtil.UNDERSCORE).concat(String.valueOf(id)), 
										RDF.type.getURI(), 
										ADNIOntologyUtil.prefix.concat(nodoCorrente));
								DBManager.getInstance().addStatement(ADNIOntologyUtil.MODEL_URI, 
										ADNIOntologyUtil.prefix.concat(RID), 
										RDF.type.getURI(), 
										ADNIOntologyUtil.prefix.concat(ADNIOntologyUtil.SUBJECT));
								DBManager.getInstance().addStatement(ADNIOntologyUtil.MODEL_URI, 
										ADNIOntologyUtil.prefix + RID, 
										RDF.type.getURI(), 
										ADNIOntologyUtil.NAMED_INDIVIDUAL);
								DBManager.getInstance().addStatement(ADNIOntologyUtil.MODEL_URI, 
										ADNIOntologyUtil.prefix + RID, 
										ADNIOntologyUtil.prefix + ADNIOntologyUtil.STANDARD_ASSESMENT, 
										ADNIOntologyUtil.prefix + nodoCorrente + ADNIOntologyUtil.UNDERSCORE + String.valueOf(id));
								
							} else if (ultimoNodo) {
								//Imposto il valore del dato nell'utlimo nodo
									DBManager.getInstance().addStatement(ADNIOntologyUtil.MODEL_URI, 
											ADNIOntologyUtil.prefix + nodoPrecedente + ADNIOntologyUtil.UNDERSCORE  + String.valueOf(id), 
											ADNIOntologyUtil.prefix + nodoCorrente, 
											ADNIOntologyUtil.prefix + value);
								
							} else {
								//Compongo l'ontologia nei nodi di mezzo
								DBManager.getInstance().addStatement(ADNIOntologyUtil.MODEL_URI, 
										String.format("%s%s_%s", ADNIOntologyUtil.prefix,nodoPrecedente,String.valueOf(id)), 
										String.format("%s%s",ADNIOntologyUtil.prefix , ADNIOntologyUtil.STANDARD_ASSESMENT_ITEM), 
										String.format("%s%s_%s",ADNIOntologyUtil.prefix , nodoCorrente , String.valueOf(id))); 
								DBManager.getInstance().addStatement(ADNIOntologyUtil.MODEL_URI, 
										ADNIOntologyUtil.prefix + nodoCorrente + ADNIOntologyUtil.UNDERSCORE + String.valueOf(id), 
										RDF.type.getURI(), 
										ADNIOntologyUtil.prefix + nodoCorrente);
								
							}
							indexObj++;
						}
						
						
					}
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
 
	}
	
	/**
	 * Load transformations values
	 * 
	 * @param path
	 * @param csvDelimiter
	 * @return
	 * @throws IOException
	 */
	private List<ADNIVariableTransfomation> loadTransfValues(Path path, char csvDelimiter) throws IOException {
		
		List<ADNIVariableTransfomation> result = new ArrayList<ADNIVariableTransfomation>();
		List<CSVRecord> ontologyMappings = getCsvLoader().load(path,csvDelimiter).getRecords();
		ontologyMappings.forEach(record -> result.add(new ADNIVariableTransfomation(record)));
		return result;
	}
	
	
	/**
	 * Extract value from ADNI table raw and transform if necessary
	 * 
	 * @param record
	 * @param csvHeader
	 * @param adniMapping
	 * @param datasetPrefix
	 * @param transformationValues
	 * @return value 
	 */
	private String extractValue(CSVRecord record, Map<String, Integer> csvHeader, ADNIMapping adniMapping, String datasetPrefix, List<ADNIVariableTransfomation> transformationValues) {
		String value = "";
		String variablename = adniMapping.getVariable();
		String valueRaw  = record.get(csvHeader.get(variablename));
		if(org.apache.commons.lang3.StringUtils.isNotBlank(valueRaw)) {
			value = valueRaw;
			for(ADNIVariableTransfomation transfCorr : transformationValues) {
				if(transfCorr.getTable().equalsIgnoreCase(datasetPrefix) 
						&& transfCorr.getVariable().equalsIgnoreCase(variablename) 
						&& transfCorr.getValueExpected().equalsIgnoreCase(valueRaw) ) {
					value = transfCorr.getValueCorrected();
					break;
				}
			}
		}
		return value;
	}

	/**
	 * Test if a missing value
	 * 
	 * @param value
	 * @return
	 */
	private boolean isNotMissingValue(String value) { 
		return value != null && !"".equals(value) && !"NA".equals(value.trim().toUpperCase());
	}
	
	private synchronized long getId() { 
		return ++id;
	}
	
	public CSVLoader getCsvLoader() {
		if(csvLoader == null)
			csvLoader = new CSVLoader();
		return csvLoader;
	}
	 

}
