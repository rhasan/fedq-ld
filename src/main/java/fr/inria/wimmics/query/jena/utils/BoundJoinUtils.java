package fr.inria.wimmics.query.jena.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;
import com.hp.hpl.jena.sparql.expr.Expr;

import fr.inria.wimmics.common.utils.LoggerLocal;

public class BoundJoinUtils {
	
	private static Logger log = LoggerLocal.getLogger(BoundJoinUtils.class.getName());
	
	public static void convertToBoundJoinQuery(Model prevModel, Query prevQuery, Query currentQuery) {

		
		Query tQuery = QueryFactory.create("SELECT * {<http://example.com/s> <http://example.com/p> <http://example.com/o>}");
		//Query tQuery = QueryFactory.make();
		
		tQuery.setQueryPattern(prevQuery.getQueryPattern());
		tQuery.setQuerySelectType();
		//tQuery.addResultVar("*");
		
		
		tQuery.setResultVars();
		
		//tQuery.setValuesDataBlock(new ArrayList<Var>(), new ArrayList<Binding>());
		//log.info("Bound join modified prev query:"+tQuery);
		
		List<Binding> prevBindings = new ArrayList<Binding>();
		List<Var> prevVars = new ArrayList<Var>();
		
		QueryExecution qe = QueryExecutionFactory.create(tQuery, prevModel);
		ResultSet rSet = qe.execSelect();
		
		
		while(rSet.hasNext()) {
			Binding b = rSet.nextBinding();
			//log.info(b);
			prevBindings.add(b);
			
			if(prevVars.isEmpty()) {
				Iterator<Var> itVar = b.vars();
				while(itVar.hasNext()) {
					prevVars.add(itVar.next());
				}
			}
			
		}
		
		List<Var> currentVars = JenaQueryUtils.getQueryPatternVars(currentQuery);
		
		List<Binding> finalBindings = new ArrayList<Binding>();
		
		List<Var> finalVars = new ArrayList<Var>();
		for(Var v:currentVars) {
			for(Var vv:prevVars) {
				if(v.getName().equals(vv.getName())) {
					finalVars.add(v);
				}
			}
		}
		
		Map<String, HashSet<Node>> noRepeat = new HashMap<String, HashSet<Node>>();
		for(Binding b:prevBindings) {
		
			BindingHashMap bb = new BindingHashMap();
			for(Var v:finalVars) {
				Node n = b.get(v);
				//log.info(v+"="+n);
				if(n.isBlank()) {
					//log.info("Continue blank node check");
					continue;
				}
				HashSet<Node> valsForV;
				if(noRepeat.containsKey(v.getName())) {
					valsForV = noRepeat.get(v.getName());
					if(valsForV.contains(n))
						continue;
				} else {
					valsForV = new HashSet<Node>();
				}
				
				bb.add(v, n);
				valsForV.add(n);
				noRepeat.put(v.getVarName(), valsForV);

			}
			if(bb.size()>0) {
				finalBindings.add(bb);
			}
		}
		
		
		currentQuery.setValuesDataBlock(finalVars, finalBindings);
		
		log.info("Bound join modified query: "+ currentQuery);

	}

}
