package fr.inria.wimmics.query.dqp;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;

public class Environment {
	private List<String> endpoints = null;
	
	//Model model=null;
	private EndpointTriplePatternIndex endpointTriplePatternIndex;
	private boolean boundJoin = false;
	private boolean askSessionCache = false;

	
	public Environment() {
		endpointTriplePatternIndex = new EndpointTriplePatternIndex();
		endpoints = new ArrayList<String>();
	}
	
	public Environment(List<String> endpoints) {
		this();
		//setEndpoints(endpoints);
		this.endpoints.addAll(endpoints);
		
	}
//	public void setModel(Model model) {
//		this.model = model;
//	}
//	public Model getModel() {
//		return model;
//	}
	public void setEndpoints(List<String> endpoints) {
		this.endpoints = endpoints;
		
	}
	
	public List<String> getEndpoints() {
		return endpoints;
	}
	
	public void setEndpointTriplePatternIndex(
			EndpointTriplePatternIndex endpointTriplePatternIndex) {
		this.endpointTriplePatternIndex = endpointTriplePatternIndex;
	}
	public EndpointTriplePatternIndex getEndpointTriplePatternIndex() {
		return endpointTriplePatternIndex;
	}

	public boolean addEndpoint(String url) {
		if(endpoints.contains(url)==false) {
			endpoints.add(url);
			return true;
		}
		return false;
	}
	
	public boolean removeEndpoint(String endpoint) {
		endpointTriplePatternIndex.removeEndpoint(endpoint);
		return endpoints.remove(endpoint);
	}

	public void setBoundJoin(boolean boundJoin) {
		this.boundJoin = boundJoin;
	}
	public boolean getBoundJoin() {
		return boundJoin;
	}
	
	public void setAskSessionCache(boolean askSessionCache) {
		this.askSessionCache = askSessionCache;
	}
	public boolean isAskSessionCache() {
		return askSessionCache;
	}
}
