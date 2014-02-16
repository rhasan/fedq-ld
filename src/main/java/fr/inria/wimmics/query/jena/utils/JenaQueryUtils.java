package fr.inria.wimmics.query.jena.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.jena.iri.IRIFactory;
import org.apache.log4j.Logger;

import com.github.jsonldjava.core.RDFDataset.IRI;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.QueryTriplePattern;

public class JenaQueryUtils {
	private static Logger logger = LoggerLocal.getLogger(JenaQueryUtils.class.getName());

	public static List<TriplePath> getTriplePaths(Query query) {
		final List<TriplePath> tps = new ArrayList<TriplePath>();
		ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
			
			@Override
			public void visit(ElementPathBlock el) {
				ListIterator<TriplePath> it = el.getPattern().iterator();
				
				while ( it.hasNext() ) { 
					TriplePath tp = it.next();
					//System.out.println("TriplePath:  "+tp);
					
					tps.add(tp);
					
				}
			}
			
			@Override
			public void visit(ElementSubQuery el) {
				ElementWalker.walk(el.getQuery().getQueryPattern(),this);
			}

			/*
			@Override
			public void visit(ElementOptional el) {
				
				ElementWalker.walk(el.getOptionalElement(),this);
			}*/
			
		});
		
		return tps;
	}
	
	
	public static List<Var> getQueryPatternVars(Query query) {
		List<TriplePath> tps = getTriplePaths(query);
		Set<Var> vars = new HashSet<Var>();
		for(TriplePath tp:tps) {
			if(tp.getSubject().isVariable()) {
				vars.add((Var)tp.getSubject());
			}
			if(tp.getObject().isVariable()) {
				vars.add((Var)tp.getObject());
			}
			if(tp.getPredicate().isVariable()) {
				vars.add((Var)tp.getPredicate());
			}
		}
		
		List<Var> varsList = new ArrayList<Var>(vars);
		return varsList;
	}
	
	public static String toStringForSPARQL(Node node) {
		
		if(node.isURI() ) {
			return "<"+node.toString()+">";
		} else if(node.isBlank()) {
			logger.info("Blank node:"+node.getBlankNodeLabel());
			return node.getBlankNodeLabel();
		} else if(node.toString().startsWith("??")) {
			logger.info("Weird blank node bug in jena TriplePath blank node label:"+node.toString());
			return node.toString().substring(1);
		}
		return node.toString();
	}
	
	
	public static String toStringForHashForCache(Node node) {
		
		if(node.isURI() ) {
			return "<"+node.toString()+">";
		} else if(node.isBlank()) {
			logger.info("Blank node:"+node.getBlankNodeLabel());
			return "[]";
		} else if(node.toString().startsWith("??")) {
			logger.info("Weird blank node bug in jena TriplePath blank node label:"+node.toString());
			return "[]";
		} else if(node.isVariable()) {
			return "?";
		} else if(node.isLiteral()) {
			return "v";
		}
		return "o";
	}
	
	public static String generateHashForCache(TriplePath triplePath) {
		String subject = toStringForHashForCache(triplePath.getSubject());
		String predicate =   toStringForHashForCache(triplePath.getPredicate());
		String object = toStringForHashForCache(triplePath.getObject());
		return subject+" "+predicate+" "+object;
	}	
	
	public static List<QueryTriplePattern> getAskQueriesForTriplePaths(Query query){
		List<QueryTriplePattern> askQueries = new ArrayList<QueryTriplePattern>();
		logger.info("Query:"+ query.toString());
		
		List<TriplePath> tps = getTriplePaths(query);
		
		for(TriplePath tp:tps) {
			//logger.info(tp.toString());
			
			
			ElementPathBlock block = new ElementPathBlock();
			block.addTriplePath(tp);
			ElementGroup body = new ElementGroup();
			body.addElement(block);
			
			
			
			Query subquery = QueryFactory.make();
			subquery.setQueryAskType();
			subquery.setQueryPattern(body);
			
			
			
			logger.debug("Programatically re-writtn ASK query: "+subquery.toString());
			
			//String subject = toStringForSPARQL(tp.getSubject());
			//String predicate =   toStringForSPARQL(tp.getPredicate());
			//String object = toStringForSPARQL(tp.getObject());
			
			//String subqueryStr = "ASK {"+subject+" "+predicate+" "+object+"}";
			//logger.info(" Re-writtne ASK query: "+subqueryStr);
			
			QueryTriplePattern qtp = new QueryTriplePattern();
			qtp.setAskQuery(subquery);
			qtp.setTriplePath(tp);
			
			askQueries.add(qtp);
			
			
		}
		
		return askQueries;
	}
	
	public static boolean sendAskQuery(Query query, String endpoint) {
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
		
		return qe.execAsk();
	}
	
}
