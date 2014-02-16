package fr.inria.wimmics.query.dqp.server;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.sparql.core.ResultBinding;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.Environment;
import fr.inria.wimmics.query.dqp.JenaFederatedQueryProcessor;
import fr.inria.wimmics.query.explanation.JenaExplanationUtils;
import fr.inria.wimmics.query.explanation.QueryResultExplainer;

@SuppressWarnings("serial")
public class ExplainResultServlet extends HttpServlet {
	Logger log = LoggerLocal.getLogger(ExplainResultServlet.class.getName());
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	process(request,response);
    }
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		process(request,response);
		
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Environment env = ServerUtils.getEnvironmentAttribute(request);
		//request.getParameterMap();
		//log.info(Arrays.toString(request.getParameterMap().entrySet().toArray()));
		String result = request.getParameter("result");
		JenaFederatedQueryProcessor queryProcessor = ServerUtils.getJenaQueryProcessorAttribute(request, env);
		
		log.info("Received result:"+result);
		
		Gson gson=new Gson();
		Type type = new TypeToken<Map<String, String>>(){}.getType();
		Map<String, String> solnMap = gson.fromJson(result, type);
		
		ResultBinding soln = JenaExplanationUtils.convertToResultBinding(queryProcessor.getVirtualModel(), solnMap);
		log.info("Solution:"+soln);
		QueryResultExplainer.explainQueryResult(queryProcessor.getVirtualModel(), queryProcessor.getQuery(), soln);
		response.setStatus(HttpServletResponse.SC_OK);
		
	}
}
