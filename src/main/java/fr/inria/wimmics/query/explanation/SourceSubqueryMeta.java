package fr.inria.wimmics.query.explanation;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;

public class SourceSubqueryMeta {
	
	
	private Query subquery;
	private String source;
	
	
	public void setSubquery(Query subquery) {
		this.subquery = subquery;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}
	public Query getSubquery() {
		
		return subquery;
	}
	
	public boolean equals(Object y) {
		if(this == y) return true;
		if(y instanceof SourceSubqueryMeta) {
			SourceSubqueryMeta mm = (SourceSubqueryMeta) y;
			return mm.source.equals(this.source) && mm.subquery.toString().equals(this.subquery.toString());
		}
		
		return false;
	}
	public int hashCode() {
		int hash = 17;
		hash = 31*hash + source.hashCode();
		hash = 31*hash + subquery.toString().hashCode();
		return hash;
		
	}
	
	

}
