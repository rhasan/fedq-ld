package fr.inria.wimmics.query.dqp.server;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.ResultBinding;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.Environment;
import fr.inria.wimmics.query.dqp.JenaFederatedQueryProcessor;
import fr.inria.wimmics.query.explanation.JenaExplanationUtils;
import fr.inria.wimmics.query.explanation.QueryResultExplainer;
import fr.inria.wimmics.query.explanation.SourceSubqueryMeta;

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
		Model m = QueryResultExplainer.explainQueryResult(queryProcessor.getVirtualModel(), queryProcessor.getQuery(), soln);
		
		Map<SourceSubqueryMeta, HashSet<String>> sourceSubqueryTriple = new HashMap<SourceSubqueryMeta, HashSet<String>>();
		
		
		StmtIterator it = m.listStatements();
		while(it.hasNext()) {
			Statement st = it.next();
			if(env.containsInTripleSourceSubqueryIndex(st)) {
				HashSet<SourceSubqueryMeta> set = env.getValeuFromTripleSourceSubqueryIndex(st);
				for(SourceSubqueryMeta meta: set) {
					
					HashSet<String> tripleSet = null;
					if(sourceSubqueryTriple.containsKey(meta)) {
						tripleSet = sourceSubqueryTriple.get(meta);
					} else {
						tripleSet = new HashSet<String>();
						sourceSubqueryTriple.put(meta, tripleSet);
					}
					
					tripleSet.add(st.toString());
					
				}
			}
		}
		
		log.info("Explanation");
		for(Entry<SourceSubqueryMeta,HashSet<String>>  entry: sourceSubqueryTriple.entrySet()) {
			SourceSubqueryMeta meta = entry.getKey();
			HashSet<String> set = entry.getValue();
			log.info("Source: "+meta.getSource());
			log.info("Subquery: "+meta.getSubquery());
			log.info("Lineage:");
			for(String st:set) {
				log.info(st.toString());
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		
	}
}
