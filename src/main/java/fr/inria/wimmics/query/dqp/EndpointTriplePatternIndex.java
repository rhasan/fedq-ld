package fr.inria.wimmics.query.dqp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import fr.inria.wimmics.common.utils.LoggerLocal;

public class EndpointTriplePatternIndex {
	Logger log = LoggerLocal.getLogger(EndpointTriplePatternIndex.class.getName());
	private Map<String, HashSet<String>> index;
	private Map<String,ArrayList<QueryTriplePattern>> reverseIndex;
	
	public EndpointTriplePatternIndex() {
		index = new HashMap<String, HashSet<String>>();
		reverseIndex = new HashMap<String, ArrayList<QueryTriplePattern>>();
	}
	
	public void clear() {
		//index.clear();
		reverseIndex.clear();
	}
	
	public void add(QueryTriplePattern qtp, String endpoint) {
		HashSet<String> endpoints;
		if(index.containsKey(qtp.getEncodedTriplePattern())) {
			endpoints = index.get(qtp.getEncodedTriplePattern());
			
		} else {
			endpoints = new HashSet<String>();
			
		}
		log.info("Adding to index: "+qtp.getEncodedTriplePattern()+"->"+endpoint);
		endpoints.add(endpoint);
		index.put(qtp.getEncodedTriplePattern(), endpoints);
		
		ArrayList<QueryTriplePattern> qtps;
		if(reverseIndex.containsKey(endpoint)) {
			qtps = reverseIndex.get(endpoint);
		} else {
			qtps = new ArrayList<QueryTriplePattern>();
		}
		
		qtps.add(qtp);
		reverseIndex.put(endpoint, qtps);
	}
	
	public boolean contains(QueryTriplePattern qtp) {
		return index.containsKey(qtp.getEncodedTriplePattern());
	}
	
	public List<String> getEndpoints(QueryTriplePattern qtp) {
		HashSet<String> endpoints = index.get(qtp.getEncodedTriplePattern());
		List<String> list = new ArrayList<String>(endpoints);
		return list;
		
	}
	
	public void removeEndpoint(String endpoint) {
		List<String> removeTriplePattern = new ArrayList<String>();
		for(Entry<String, HashSet<String>> entry:index.entrySet()) {
			if(entry.getValue().contains(endpoint)) {
				entry.getValue().remove(endpoint);
			}
			if(entry.getValue().size()==0) {
				removeTriplePattern.add(entry.getKey());
			}
		}

		for(String tp:removeTriplePattern) {
			index.remove(tp);
		}
		
		if(reverseIndex.containsKey(endpoint)) {
			reverseIndex.remove(endpoint);
		}
	}
	
	public boolean containsInReverseIndex(String endpoint) {
		return reverseIndex.containsKey(endpoint);
	}
	
	public List<QueryTriplePattern> getQueryTriplePatternFromInverseIndex(String endpoint) {
		return reverseIndex.get(endpoint);
	}
	
	protected void printIndexMap() {
		for(Entry<String, HashSet<String>> entry:index.entrySet()) {
			
			log.info(entry.getKey()+"["+entry.getKey().hashCode()+"]->"+Arrays.toString(entry.getValue().toArray()) );
		}
	}
}
