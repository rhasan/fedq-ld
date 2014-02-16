package fr.inria.wimmics.query.dqp.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpException;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.Environment;

@SuppressWarnings("serial")
public class AddEndpointServlet extends HttpServlet {
	Logger log = LoggerLocal.getLogger(AddEndpointServlet.class.getName());
	
//	@Override
//	public void init(ServletConfig config) throws ServletException {
//		
//	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Environment env = ServerUtils.getEnvironmentAttribute(request);
		
		String endpoint = request.getParameter("endpoint");
		try {
			boolean available = ServerUtils.checkEndpoint(endpoint);
			if(available ) {
				boolean added = env.addEndpoint(endpoint);
				//success
				if(added) {
					response.setStatus(HttpServletResponse.SC_OK);
					response.getWriter().write("Endpoint successfully added");
					log.info("Added endpoint: "+endpoint);
				} else {
					// already exists
					response.setStatus(HttpServletResponse.SC_CONFLICT);
					response.getWriter().write("Endpoint already added");
					log.info("Already added: "+endpoint);
	
				}
				
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().write("Endpoint not available");
				log.info("Endpoint not available: "+endpoint);
	
			}
		} catch(QueryExceptionHTTP e) {

			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().write("Endpoint not available");
			log.info("Endpoint not available: "+endpoint);
			log.info(e);
		} catch(Exception e) {
			
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write("Can't find endpoint");
			log.info("Can't find endpoint: "+endpoint);
		
			log.info(e);
		}

	}
}
