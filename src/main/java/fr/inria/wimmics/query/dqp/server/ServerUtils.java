package fr.inria.wimmics.query.dqp.server;

import javax.servlet.http.HttpServletRequest;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;

import fr.inria.wimmics.query.dqp.Environment;

public class ServerUtils {
	public static Environment getEnvironmentAttribute(HttpServletRequest request) {
		Environment env = (Environment) request.getSession().getAttribute("Environment");
		if(env == null) {
			env = new Environment();
			request.getSession().setAttribute("Environment", env);
		}
		return env;
		
	}
	
	public static boolean checkEndpoint(String endpoint) {
		String query = "ASK {?s ?p ?o}";
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
		return qe.execAsk();
	}
}
