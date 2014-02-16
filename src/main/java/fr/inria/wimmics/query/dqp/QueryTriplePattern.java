package fr.inria.wimmics.query.dqp;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.core.TriplePath;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.jena.utils.JenaQueryUtils;

public class QueryTriplePattern {
	static Logger log = LoggerLocal.getLogger(QueryTriplePattern.class.getName()); 
	private Query askQuery;
	private TriplePath triplePath;
	private String hashValue;

	
	public Query getAskQuery() {
		return askQuery;
	}
	public void setAskQuery(Query askQuery) {
		this.askQuery = askQuery;
	}
	public TriplePath getTriplePath() {
		return triplePath;
	}
	public void setTriplePath(TriplePath triplePath) {
		this.triplePath = triplePath;
		hashValue = JenaQueryUtils.generateHashForCache(triplePath);
		log.debug("Hash for triple path ["+triplePath+"] generated: "+hashValue);
	}
	public String getEncodedTriplePattern() {
		return hashValue;
	}
	
//	@Override
//	public int hashCode() {
//		return hashValue.hashCode();
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		
//		return this.hashCode() == obj.hashCode();
//	}
}
