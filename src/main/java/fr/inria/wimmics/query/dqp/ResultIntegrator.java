package fr.inria.wimmics.query.dqp;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class ResultIntegrator {

	Environment env;

	public ResultIntegrator(Environment env) {
		this.env = env;
	}
	
	public ResultSet integrateResults(Model model, Query query) {
		
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		return qe.execSelect();
	}
	
}
