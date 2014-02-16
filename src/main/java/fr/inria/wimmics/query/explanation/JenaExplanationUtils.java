package fr.inria.wimmics.query.explanation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.iri.IRIFactory;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.Template;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.QueryRouter;
import fr.inria.wimmics.query.dqp.QueryTriplePattern;
import fr.inria.wimmics.query.jena.utils.JenaQueryUtils;

public class JenaExplanationUtils {
	private static Logger log = LoggerLocal.getLogger(JenaExplanationUtils.class.getName());
	public static boolean isBlank(String name) {
		if(name.startsWith("_:")) return true;
		return false;
	}
	public static String getBlankNodeId(String name) {
		return name.substring(2);
	}
	
	public static boolean isValidIRI(String iri) {
		
		try {
			IRIFactory.jenaImplementation().construct(iri);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	public static ResultBinding convertToResultBinding(Model model, Map<String,String> m) {
		List<String> vars = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		for(Entry<String, String> entry:m.entrySet()) {
			//log.info(entry.getKey()+"->"+entry.getValue());
			vars.add(entry.getKey());
			values.add(entry.getValue());
			
		}		
		return convertToResultBinding(model, vars, values);
	}
	public static ResultBinding convertToResultBinding(Model model, List<String> vars, List<String> values) {
		
		BindingHashMap binding = new BindingHashMap();
		
		List<Var> varList = Var.varList(vars);
		
		
		for(int i=0;i<vars.size();i++) {
			Node val = null;
			String valStr = values.get(i);
			if(isBlank(valStr)) {
				AnonId id = new AnonId(getBlankNodeId(valStr));
				val = NodeFactory.createAnon(id);
			} else {
				if(isValidIRI(valStr)) {
					val = NodeFactory.createURI(valStr);
				} else {
					val = NodeFactory.createLiteral(valStr);
				}
			}

			Var var = varList.get(i);
			binding.add(var, val);
		}
		
		ResultBinding resultBinding = new ResultBinding(model, binding);
		
		return resultBinding;
	}
	
	public static Query buildExplanationQuery(Query query, ResultBinding result) {
		

		
		ElementPathBlock queryPatternBlock = new ElementPathBlock();
		
		List<TriplePath> queryPathsOld = JenaQueryUtils.getTriplePaths(query);
		
		List<Triple> triples = new ArrayList<Triple>();
		for(TriplePath qp:queryPathsOld) {
			//log.info(qp);
			queryPatternBlock.addTriplePath(qp);
			triples.add(qp.asTriple());
		}		

		Query newQuery = QueryFactory.make();
		newQuery.setQueryConstructType();
		BasicPattern bgp = BasicPattern.wrap(triples);
		Template templ = new Template(bgp);
		ElementGroup body = new ElementGroup();
		body.addElement(queryPatternBlock);
		
		newQuery.setQueryPattern(body);
		newQuery.setConstructTemplate(templ);
		
		List<Var> vars = new ArrayList<Var>();
		Iterator<Var> varIt = result.getBinding().vars();
		while(varIt.hasNext()) {
			vars.add(varIt.next());
		}
		List<Binding> bindings = new ArrayList<Binding>();
		bindings.add(result.getBinding());
		newQuery.setValuesDataBlock(vars, bindings);
		log.info("Explanation query: "+newQuery);
		return newQuery;
	}

}
