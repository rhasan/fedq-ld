package fr.inria.wimmics.query.dqp;

import com.hp.hpl.jena.query.Query;

public class DecomposedQuery {

	private Query query;
	private String endpoint;
	public DecomposedQuery() {
		// TODO Auto-generated constructor stub
	}
	
	public DecomposedQuery(Query query, String endpoint) {
		super();
		this.query = query;
		this.endpoint = endpoint;
	}

	public Query getQuery() {
		return query;
	}
	public void setQuery(Query query) {
		this.query = query;
	}
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
}
