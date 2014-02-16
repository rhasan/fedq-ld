package fr.inria.wimmics.query.dqp;

import static org.junit.Assert.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.core.ResultBinding;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.explanation.JenaExplanationUtils;
import fr.inria.wimmics.query.explanation.QueryResultExplainer;

public class JenaExplanationTest {

	Logger log = LoggerLocal.getLogger(JenaExplanationTest.class.getName());

	@Test
	public void explainQueryResultTest() {
		
		String queryString = "PREFIX : <http://example/>" +
				"PREFIX  dc:     <http://purl.org/dc/elements/1.1/>" +
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
				"SELECT ?author ?title ?friend where" +
				"{" +
				    "?book dc:title ?title ." +
				    "?book dc:creator ?author ." +
				    "?author foaf:name ?name ." +
				    "?author foaf:knows ?friend ." +
				    "?friend foaf:name ?friend_name ." +
				    "FILTER(?title = \"Distributed Query Processing for Linked Data\")" +
				"}" ;

		List<String> endpoints = new ArrayList<String>();
		endpoints.add("http://localhost:3031/persons/query");
		endpoints.add("http://localhost:3030/books/query");
		
		
		Environment env = new Environment(endpoints);
		env.setBoundJoin(true);
		
		
		JenaFederatedQueryProcessor processor = new JenaFederatedQueryProcessor(env);
		ResultSet results = processor.executeSelect(queryString);
		
		String result = "{\"name\":\"Alice\",\"friend_name\":\"Charlie\"}";
		
		
		
//		while(results.hasNext()) {
//			log.info(results.next());
//		}
		Gson gson=new Gson();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		Map<String, String> solnMap = gson.fromJson(result, type);
		
		ResultBinding soln = JenaExplanationUtils.convertToResultBinding(processor.getVirtualModel(), solnMap);
		log.info("Solution:"+soln);
		
		QueryResultExplainer.explainQueryResult(processor.getVirtualModel(), processor.getQuery(), soln);
		
	}


}
