package fr.inria.wimmics.query.explanation;

import java.util.List;

import org.apache.jena.iri.IRIFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;

public class JenaExplanationUtils {
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
	
	public static QuerySolution convertToQuerySolution(Model model, List<String> vars, List<String> values) {
		
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

}
