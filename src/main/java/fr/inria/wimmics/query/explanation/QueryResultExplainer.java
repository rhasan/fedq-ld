package fr.inria.wimmics.query.explanation;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class QueryResultExplainer {
	
	public static Model explainQueryResult(Model model, Query query, QuerySolution result) {
		Model expModel = ModelFactory.createDefaultModel();
		//TODO variable binding in a construct query
		return expModel;
	}

	
}
