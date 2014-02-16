package fr.inria.wimmics.query.dqp;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import com.hp.hpl.jena.query.ResultSet;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.Environment;
import fr.inria.wimmics.query.dqp.JenaFederatedQueryProcessor;

public class JenaFederatedQueryProcessorTest {
	Logger log = LoggerLocal.getLogger(JenaFederatedQueryProcessorTest.class.getName());

	@Test
	public void executeSelectTest() {
		
		String queryString = "PREFIX : <http://example/>" +
				"PREFIX  dc:     <http://purl.org/dc/elements/1.1/>" +
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
				"SELECT ?author ?title ?friend where" +
				"{" +
				"	 ?author foaf:name ?name . " +
				"    ?author foaf:knows ?friend ." +
				"    ?book dc:title ?title. " +
				"    ?book dc:creator ?author " +


				"}";

//		String queryString = "PREFIX : <http://example/> PREFIX  dc:     <http://purl.org/dc/elements/1.1/> PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
//				"SELECT * where " +
//				"{ ?book dc:title ?title . " +
//				"?book dc:creator ?author. " +
//				":s :p :o. " +
//				"OPTIONAL {?a ?b ?c." +
//				"?d ?b ?f.}" +
//				"{?x ?y ?z. " +
//				"?p ?q ?r.} " +
//				"UNION { " +
//				":us :up ?uo. " +
//				":uz ?uy ?ux. } " +
//				" _:bbbb foaf:name \"John\"@fr. " +
//				" _:ccc foaf:name \"Doe\"@fr. " +
//				
//				"{ SELECT ?author ?name ?friend " +
//					"{ ?author foaf:name ?name . " +
//					"?author foaf:knows ?friend  } }" +
//				"}";
		
		List<String> endpoints = new ArrayList<String>();
		endpoints.add("http://localhost:3031/persons/query");
		endpoints.add("http://localhost:3030/books/query");
		
		
		Environment env = new Environment(endpoints);
		env.setBoundJoin(true);

		//String json = new Gson().toJson(endpoints);
		//log.info("Endpoint json:"+json);
		//log.info(endpoints.size());
		
		
		JenaFederatedQueryProcessor processor = new JenaFederatedQueryProcessor(env);
		ResultSet results = processor.executeSelect(queryString);
		
		while(results.hasNext()) {
			log.info(results.next());
		}

	}

}
