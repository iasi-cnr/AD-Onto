/**
 * 
 */
package it.modiag.db;

import it.modiag.util.ADNIOntologyUtil;

/**
 * @author rvoyat
 *
 */
public class DBManager {
	

    static DBManager instance = null; 
    private DBManager() {  } 
    
    // Factory method to provide the users with instances 
    static public DBManager getInstance() 
    { 
        if (instance == null)         
             instance = new DBManager(); 
   
        return instance; 
    }  
    
	private TDBConnection tdbConnection;
	private String db_home_dir;
	private String db_lock_file; 

	public void addStatement( String modelName, String subject, String property, String object )
	{
		getTdbConnection().addStatement(modelName, subject, property, object);
	}
	public void loadModel() {
		getTdbConnection().loadModelOWL(ADNIOntologyUtil.MODEL_URI, ADNIOntologyUtil.getModelPath());
	}
	public void printModel() {
		getTdbConnection().printModel(ADNIOntologyUtil.MODEL_URI);
	}
	public void closeDBConnection() {
		getTdbConnection().removeLock(getDb_home_dir().concat(getDb_lock_file())); 
	}
	 
	public String getDb_home_dir() {
		if(db_home_dir == null)
			db_home_dir =ADNIOntologyUtil.getDbHomeDir();
		return db_home_dir;
	}

	public void setDb_home_dir(String db_home_dir) {
		this.db_home_dir = db_home_dir;
	}

	public String getDb_lock_file() {
		if(db_lock_file == null)
			db_lock_file = "tdb.lock";
		return db_lock_file; 
	}

	public void setDb_lock_file(String db_lock_file) {
		this.db_lock_file = db_lock_file;
	}
	
	public TDBConnection getTdbConnection() {
		if(tdbConnection == null ) {
			tdbConnection = new TDBConnection(getDb_home_dir());
		}
		return tdbConnection;
	}
	public void setTdbConnection(TDBConnection tdbConnection) {
		this.tdbConnection = tdbConnection;
	}
	
	
}
