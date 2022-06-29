package it.modiag.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

public class TDBConnection 
{
	private Dataset ds;
	
	public TDBConnection( String path )
	{
		ds = TDBFactory.createDataset( path );
	}
	
	public void loadModel( String modelName, String path )
	{
		Model model = null;
			
		ds.begin( ReadWrite.WRITE );
		try
		{
			model = ds.getNamedModel( modelName );
			FileManager.get().readModel( model, path );
			model.write(System.out);
			ds.commit();
		}
		finally
		{
			ds.end();
		}
	}
	public void loadModelOWL( String uri, String path )
	{
		Model model = null;
			
		ds.begin( ReadWrite.WRITE );
		try
		{
			if(!ds.containsNamedModel(uri)) {
				model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
				model.read(path);
				ds.addNamedModel(uri, model); 
				ds.commit();
			}
		}
		finally
		{
			ds.end();
		}
	}
	public void printModel( String uri) {
		Model model = null;
			
		ds.begin( ReadWrite.READ );
		try
		{
		model = ds.getNamedModel( uri );
		model.write(System.out) ;
		}
		finally
		{
			ds.end();
		}
	}

	public void removeLock(String pathLock) {
		
		File fileToRemove = new File(pathLock);
		
		if(fileToRemove.exists())
			fileToRemove.delete(); 
	}
	
	public void addStatement( String modelName, String subject, String property, String object )
	{
		Model model = null;
		
		ds.begin( ReadWrite.WRITE );
		try
		{
			model = ds.getNamedModel( modelName );
			
			Statement stmt = model.createStatement
							 ( 	
								model.createResource( subject ), 
								model.createProperty( property ), 
								model.createResource( object ) 
							 );
			
			model.add( stmt );
			ds.commit();
		}
		finally
		{
			if( model != null ) model.close();
			ds.end();
		}
	}
	
	public List<Statement> getStatements( String modelName, String subject, String property, String object )
	{
		List<Statement> results = new ArrayList<Statement>();
			
		Model model = null;
			
		ds.begin( ReadWrite.READ );
		try
		{
			model = ds.getNamedModel( modelName );
				
			Selector selector = new SimpleSelector(
						( subject != null ) ? model.createResource( subject ) : (Resource) null, 
						( property != null ) ? model.createProperty( property ) : (Property) null,
						( object != null ) ? model.createResource( object ) : (RDFNode) null
						);
				
			StmtIterator it = model.listStatements( selector );
			{
				while( it.hasNext() )
				{
					Statement stmt = it.next(); 
					results.add( stmt );
				}
			}
				
			ds.commit();
		}
		finally
		{
			if( model != null ) model.close();
			ds.end();
		}
			
		return results;
	}
	
	public void removeStatement( String modelName, String subject, String property, String object )
	{
		Model model = null;
		
		ds.begin( ReadWrite.WRITE );
		try
		{
			model = ds.getNamedModel( modelName );
			
			Statement stmt = model.createStatement
							 ( 	
								model.createResource( subject ), 
								model.createProperty( property ), 
								model.createResource( object ) 
							 );
					
			model.remove( stmt );
			ds.commit();
		}
		finally
		{
			if( model != null ) model.close();
			ds.end();
		}
	}
	
	public void close()
	{
		ds.close();
	}
}
