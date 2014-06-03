package fr.inria.wimmics.query.explanation;

import java.io.StringWriter;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.ResultBinding;

import fr.inria.wimmics.common.utils.LoggerLocal;

public class QueryResultExplainer {
	private static Logger log = LoggerLocal.getLogger(QueryResultExplainer.class.getName()); 
	public static Model explainQueryResult(Model model, Query query, ResultBinding result) {
		Model expModel = ModelFactory.createDefaultModel();
		// variable binding in an explanation construct query
		Query explQuery = JenaExplanationUtils.buildExplanationQuery(query, result);
		
		QueryExecution qe = QueryExecutionFactory.create(explQuery,model);
		
		expModel = qe.execConstruct();
		
		//
		StringWriter wr = new StringWriter();
		RDFDataMgr.write(wr, expModel, Lang.TRIG);
		
		
		//log.info("Explanation model:\n"+wr.getBuffer().toString());
		return expModel;
	}

	public static Model explainQueryResult(Model model, Query query, Map<String,String> resultMap) {
		Model expModel = ModelFactory.createDefaultModel();
		
		//TODO variable binding in a construct query
		return expModel;
	}
	
	
}
