package fr.inria.wimmics.query.dqp.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.Environment;

@SuppressWarnings("serial")
public class GetEndpointListServlet extends HttpServlet {
	Logger log = LoggerLocal.getLogger(GetEndpointListServlet.class.getName());
	
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Environment env = ServerUtils.getEnvironmentAttribute(request);
		List<String> endpoints = env.getEndpoints();
		
		if(endpoints.size()>0) {
			response.setStatus(HttpServletResponse.SC_OK);
			
		} else {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
		String json = new Gson().toJson(endpoints);
		log.info("Endpoint json:"+json);

		response.getWriter().write(json);
	}
}