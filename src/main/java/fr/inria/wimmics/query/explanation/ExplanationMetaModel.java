package fr.inria.wimmics.query.explanation;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;

public class ExplanationMetaModel {
	private Model model;
	private Query subquery;
	private String source;
	
	
	public void setSubquery(Query subquery) {
		this.subquery = subquery;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public Model getModel() {
		return model;
	}
	public String getSource() {
		return source;
	}
	public Query getSubquery() {
		return subquery;
	}

}
