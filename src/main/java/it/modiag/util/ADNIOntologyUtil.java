package it.modiag.util;

public class ADNIOntologyUtil { 
	
	public static final String prefix = "http://www.modiag.it#";
	public static final String prefixProp = "http://modiag#";
	public static final String RID_HEADER = "RID";
	public static final String UNDERSCORE = "_";
	public static final String SUBJECT = "Subject";
	public static final String STANDARD_ASSESMENT = "hasStandardizedAssesment";
	public static final String STANDARD_ASSESMENT_ITEM = "hasAssesmentItem";
	public static final String NAMED_INDIVIDUAL = "http://www.w3.org/2002/07/owl#NamedIndividual";
	public static final String MODEL_URI = "ADNIOntology";
	private static String modelPath; 
	private static String filesHomeDir; 
	private static String dbHomeDir; 
	 

	public static String getModelPath() { 
		return modelPath;
	}


	public static void setModelPath(String modelPath) {
		ADNIOntologyUtil.modelPath = modelPath;
	}


	public static String getFilesHomeDir() {
		return filesHomeDir;
	}


	public static void setFilesHomeDir(String filesHomeDir) {
		ADNIOntologyUtil.filesHomeDir = filesHomeDir;
	}


	public static String getDbHomeDir() {
		return dbHomeDir;
	}


	public static void setDbHomeDir(String dbHomeDir) {
		ADNIOntologyUtil.dbHomeDir = dbHomeDir;
	} 
	
	
	
	
}
