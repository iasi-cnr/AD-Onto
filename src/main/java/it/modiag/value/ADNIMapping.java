package it.modiag.value;

import java.io.Serializable;

import org.apache.commons.csv.CSVRecord;

public class ADNIMapping implements Serializable{
	
	private String variable;
	private String[] owlXpath;
	
	public ADNIMapping(CSVRecord record) { 
		this.variable = record.get(0).toUpperCase();
		this.owlXpath = record.get(1).split("\\$");
	}

	public String getVariable() {
		return variable;
	} 

	public String[] getOwlXpath() {
		return owlXpath;
	} 
	
	

}
