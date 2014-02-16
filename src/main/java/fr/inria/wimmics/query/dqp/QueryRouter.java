package fr.inria.wimmics.query.dqp;

import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.jena.utils.BoundJoinUtils;

public class QueryRouter {
	Logger log = LoggerLocal.getLogger(QueryRouter.class.getName());
	Environment env;
	public QueryRouter(Environment env) {
		this.env = env;
	}
	
	public Model routQueries(List<DecomposedQuery> queries) {
		Model model = ModelFactory.createDefaultModel();
		
		Model prevModel = null;
		Query prevQuery = null;
		for(DecomposedQuery dcq:queries) {
			
			log.info("Sending subquery to: "+dcq.getEndpoint());
			log.info("Subuery: "+dcq.getQuery());
			// TODO [bound join] bound values (for common variables) from the previous subquery in the current subquery
			if(env.getBoundJoin() && prevModel != null) {
				BoundJoinUtils.convertToBoundJoinQuery(prevModel, prevQuery, dcq.getQuery());
			}
			
			QueryExecution qe = QueryExecutionFactory.sparqlService(dcq.getEndpoint(), dcq.getQuery());
			Model m = qe.execConstruct();
			
			// TODO [bound join] store variable bindings for the current subquery, to be used in the next subquery value bindings
			if(env.getBoundJoin()) {
				prevModel = m;
				prevQuery = dcq.getQuery();
			}
			
			//log.info("Triples: "+m);
			log.info("Number of triples: "+m.size());
			model.add(m);
			
		}
		//env.setModel(model);
		return model;
	}
}
