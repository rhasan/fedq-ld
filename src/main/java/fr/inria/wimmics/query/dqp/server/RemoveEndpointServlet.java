package fr.inria.wimmics.query.dqp.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.Environment;

@SuppressWarnings("serial")
public class RemoveEndpointServlet extends HttpServlet {
	Logger log = LoggerLocal.getLogger(RemoveEndpointServlet.class.getName());
	
//	@Override
//	public void init(ServletConfig config) throws ServletException {
//		
//	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Environment env = ServerUtils.getEnvironmentAttribute(request);
		
		String endpoint = request.getParameter("endpoint");
		if(env.removeEndpoint(endpoint)) {
			//success
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write("Endpoint successfully removed");
			log.info("Removed endpoint: "+endpoint);
			
		} else {
			// remove error
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("Endpoint can't be removed");
			log.info("Endpoint can't be removed: "+endpoint);
		}
	}
}
