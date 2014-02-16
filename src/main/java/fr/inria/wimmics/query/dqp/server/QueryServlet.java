package fr.inria.wimmics.query.dqp.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.Environment;
import fr.inria.wimmics.query.dqp.JenaFederatedQueryProcessor;

@SuppressWarnings("serial")
public class QueryServlet  extends HttpServlet {
	Logger log = LoggerLocal.getLogger(QueryServlet.class.getName());
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Environment env = ServerUtils.getEnvironmentAttribute(request);
    	
    	try {
	    	String queryString = request.getParameter("query");
	    	
	    	String headerAccept = request.getHeader("Accept");
	    	if(headerAccept.equals("application/sparql-results+json")) {
	    		response.setContentType("application/sparql-results+json");
	    		response.setStatus(HttpServletResponse.SC_OK);
	    		
	    		JenaFederatedQueryProcessor processor = new JenaFederatedQueryProcessor(env);
	    		ResultSet results = processor.executeSelect(queryString);
	    		
	    		ResultSetFormatter.outputAsJSON(response.getOutputStream(), results);
	    	} else   {
	    		
	    		response.setContentType("application/sparql-results+xml");
	    		response.setStatus(HttpServletResponse.SC_OK);
	    		
	    		JenaFederatedQueryProcessor processor = new JenaFederatedQueryProcessor(env);
	    		ResultSet results = processor.executeSelect(queryString);
	    		
	    		response.getWriter().write(ResultSetFormatter.asXMLString(results));
	    		
	    	}
    	} catch(QueryParseException e) {
    		log.info(e);
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    	}
    	catch(Exception e) {
    		log.info(e);
    		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    	}


    }
}