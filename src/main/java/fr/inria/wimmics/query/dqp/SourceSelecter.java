package fr.inria.wimmics.query.dqp;

import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.jena.utils.JenaQueryUtils;

public class SourceSelecter {
	static Logger log = LoggerLocal.getLogger(SourceSelecter.class.getName());
	private Environment env;
	public SourceSelecter(Environment env) {
		this.env = env;
	}

	
	public boolean selectSources(Query query) {
		
		List<QueryTriplePattern> askQueries = JenaQueryUtils.getAskQueriesForTriplePaths(query);
		env.getEndpointTriplePatternIndex().clear();
		//env.getEndpointTriplePatternIndex().printIndexMap();
		//TODO: parallel execution
		for(QueryTriplePattern qtp:askQueries) {
			boolean found = false;
			log.info("Selecting source for ASK query:"+qtp.getAskQuery().toString());
			for(String endpoint:env.getEndpoints()) {
				log.info("Checking for source:"+endpoint);
				//a cache of triple pattern -> endpoint index for user session
				boolean ask = false;
				
				if(env.getEndpointTriplePatternIndex().contains(qtp)) {
					List<String> eps = env.getEndpointTriplePatternIndex().getEndpoints(qtp);
					if(eps.contains(endpoint)) {
						ask = true;
						log.info("ASK solved from the session cache");
					}
				}
				else {
					ask = JenaQueryUtils.sendAskQuery(qtp.getAskQuery(), endpoint);
					log.info("ASK solved by sending query");
				}
				
				if(ask == true) {
					env.getEndpointTriplePatternIndex().add(qtp, endpoint);
					found = true;
					log.info("Found source: "+endpoint+" for triple pattern: "+qtp.getEncodedTriplePattern());
				}
			}
			
			if(found == false) { 
				//TODO: do not return false if this triple pattern was in a union or optional part ** think more about this case
				log.info("No source found source for triple pattern: "+qtp.getEncodedTriplePattern());
				//return false; 
			}
		}
		
		return true;
	}
}
