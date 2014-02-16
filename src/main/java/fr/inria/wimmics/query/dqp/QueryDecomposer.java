package fr.inria.wimmics.query.dqp;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.Template;

import fr.inria.wimmics.common.utils.LoggerLocal;

public class QueryDecomposer {
	static Logger log = LoggerLocal.getLogger(QueryDecomposer.class.getName());
	private Environment env;
	public QueryDecomposer(Environment env) {
		this.env = env;
	}
	//Decompose to construct subqueries
	public List<DecomposedQuery> decomposeQuery(Query query) {

		List<DecomposedQuery> decomposedQueries = new ArrayList<DecomposedQuery>();

		for(String endpoint:env.getEndpoints()) {
			log.info("Decomposing for endpoint :"+endpoint);
			if(env.getEndpointTriplePatternIndex().containsInReverseIndex(endpoint)==false) {
				log.info("not found for this service in the reverese index");
				continue;
			}
			ElementPathBlock block = new ElementPathBlock();
			
			List<Triple> triples = new ArrayList<Triple>();
			for(QueryTriplePattern qtp:env.getEndpointTriplePatternIndex().getQueryTriplePatternFromInverseIndex(endpoint)) {
				log.info(qtp.getTriplePath());
				block.addTriplePath(qtp.getTriplePath());
				triples.add(qtp.getTriplePath().asTriple());
			}
			//ElementService srvc = new ElementService(endpoint, block);
			
			//body.addElement(srvc);
			
			ElementGroup body = new ElementGroup();
			body.addElement(block);
			Query decomposedQuery = QueryFactory.make();
			decomposedQuery.setQueryConstructType();
			BasicPattern bgp = BasicPattern.wrap(triples);
			Template templ = new Template(bgp);
			
			
			decomposedQuery.setQueryPattern(body);
			decomposedQuery.setConstructTemplate(templ);
			decomposedQuery.setResultVars();
			
			DecomposedQuery dq = new DecomposedQuery(decomposedQuery, endpoint);
			decomposedQueries.add(dq);			
			
			log.info(decomposedQuery);		
			
		}
		return decomposedQueries;
		
		

	}
}
