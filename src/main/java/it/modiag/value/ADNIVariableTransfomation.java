package it.modiag.value;

import java.io.Serializable;

import org.apache.commons.csv.CSVRecord;

public class ADNIVariableTransfomation implements Serializable{
	
	private String table;
	private String variable;
	private String valueExpected;
	private String valueCorrected;
	
	public ADNIVariableTransfomation(CSVRecord record) { 
		this.table = record.get(0);
		this.variable = record.get(1);
		this.valueExpected = record.get(2);
		this.valueCorrected = record.get(3);
	}

	public String getTable() {
		return table;
	} 

	public String getVariable() {
		return variable;
	} 

	public String getValueExpected() {
		return valueExpected;
	} 

	public String getValueCorrected() {
		return valueCorrected;
	} 
	
}
