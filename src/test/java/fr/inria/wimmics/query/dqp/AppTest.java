package fr.inria.wimmics.query.dqp;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.utils.URIUtils;
import org.apache.jena.iri.IRI;
import org.apache.jena.iri.IRIFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.github.jsonldjava.core.RDFDataset.Literal;
import com.hp.hpl.jena.assembler.Mode;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.sparql.core.TriplePath;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.explanation.JenaExplanationUtils;
import fr.inria.wimmics.query.jena.utils.JenaQueryUtils;


/**
 * Unit test for simple App.
 */
public class AppTest 
{
	
	
	static Logger log = LoggerLocal.getLogger(
			AppTest.class.getName());
	
	@Test
	public void iriUriTest() {
		IRI iri = IRIFactory.semanticWebImplementation().construct("https://www.b.com");
		log.info(iri);
		//Node n = NodeFactory.createURI("b");
		//log.info(n.getClass());
		
		//Node l = NodeFactory.createURI("s");
		
	}
	
	@Test
	public void test() {
		String service = "http://localhost:3031/persons/query";
		String query = "select * where{?s ?p ?o}";
        //QueryExecution qe = QueryExecutionFactory.sparqlService(service, query);
		
		Model model = ModelFactory.createDefaultModel();
		model.read("/Users/hrakebul/Documents/code/sw/jena_and_fuseki/jena-fuseki-1.0.0/Data/persons.ttl", "TURTLE");
		QueryExecution qe = QueryExecutionFactory.create(query,model);
		
        ResultSet rs = qe.execSelect();
        log.info(rs.getClass().getName());
        while(rs.hasNext()) {
        	QuerySolution qs = rs.next();
        	//log.info(qs.getClass());
        	
        	log.info(qs);
        
        	//ResultBinding rbd = (ResultBinding)qs;
        	
        	Iterator<String> it = qs.varNames();
        	List<String> vars = new ArrayList<String>();
        	List<String> vals = new ArrayList<String>();
        	while(it.hasNext()) {
        		String var = it.next();
        		String val = qs.get(var).toString();
        		vars.add(var);
        		vals.add(val);
        		
        		//log.info("var="+var);
        		//log.info("val="+val);
        		
        		
        	}
        	QuerySolution qsTest = JenaExplanationUtils.convertToQuerySolution(model, vars, vals);
        	//log.info("convertToQuerySolution:"+qsTest);
        	log.info(qsTest);
        	//log.info(rbd.getBinding().getClass());
        }
	
	}
	

	
	@Test
	public void testJenaQueryUtils() {
//		
//		String queryStr = "PREFIX : <http://example/> PREFIX  dc:     <http://purl.org/dc/elements/1.1/> PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
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
//				":uz ?uy ?ux. }" +
//				" _:b foaf:name \"John\"@en. " +
//				"{ SELECT ?author ?name ?friend " +
//					"{ ?author foaf:name ?name . " +
//					"?author foaf:knows ?friend  } }" +
//				"}";
		
//		String queryStr = "PREFIX : <http://example/> PREFIX  dc:     <http://purl.org/dc/elements/1.1/> PREFIX foaf: <http://xmlns.com/foaf/0.1/> " +
//				"SELECT * where " +
//				"{ " +
//				"    _:b foaf:name \"John\"@en. " +
//				"    _:c foaf:name \"Bob\"@en. " +
//				
//				"}";		

		
		String queryStr = "SELECT  ?author ?title ?friend"+
				
						"WHERE "+
						  "{ SERVICE <http://localhost:3030/books/query> "+
						      "{ ?book <http://purl.org/dc/elements/1.1/title> ?title . "+
						        "?book <http://purl.org/dc/elements/1.1/creator> ?author} "+
						    "SERVICE <http://localhost:3031/persons/query> "+
						      "{ ?author <http://xmlns.com/foaf/0.1/name> ?name . "+
						        "?author <http://xmlns.com/foaf/0.1/knows> ?friend" +
						        "} "+
						  "}";
		
		Query query = QueryFactory.create(queryStr);
		log.info(query);
		List<TriplePath> tps = JenaQueryUtils.getTriplePaths(query);
		for(TriplePath tp:tps) {
			log.info(tp);
		}

	}
}
