package fr.inria.wimmics.trying.jena;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.util.FileManager;

import fr.inria.wimmics.query.explanation.JenaExplanationUtils;
import fr.inria.wimmics.query.explanation.QueryResultExplainer;

public class QueryManipulation {
	
	

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		String queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "+
								"SELECT ?name ?mbox "+
									"WHERE { ?x foaf:name  ?name ;"+
												"foaf:mbox  ?mbox ."+
											"FILTER regex(str(?mbox), \"@work.example\") } limit 10";
		Model m = ModelFactory.createDefaultModel();
		InputStream in= FileManager.get().open("files/test.ttl");
		m.read(in,"","TURTLE");
		Query query = QueryFactory.create(queryString);
		
		QueryExecution qe = QueryExecutionFactory.create(query,m);
		ResultSet rset = qe.execSelect();
		ResultBinding result=null;
		while(rset.hasNext()) {
			result = (ResultBinding)rset.next();
			break;
		}
		
		//System.out.println(m);
		//query.setQueryConstructType();
		System.out.println(query);
		//System.out.println(query.getQueryPattern());
		//System.out.println(JenaExplanationUtils.buildExplanationQuery(query, result));
		System.out.println(QueryResultExplainer.explainQueryResult(m, query, result));
		

	}

}
