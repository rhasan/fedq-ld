package fr.inria.wimmics.query.dqp;

import java.util.List;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;

import fr.inria.wimmics.common.utils.LoggerLocal;

public class JenaFederatedQueryProcessor implements FederatedQueryProcessor {
	
	static Logger log = LoggerLocal.getLogger(
			JenaFederatedQueryProcessor.class.getName());
	
	private Environment env;
	
	private SourceSelecter sourceSelecter;
	private QueryDecomposer queryDecomposer;
	private QueryRouter queryRouter;
	private ResultIntegrator resultIntegrator;
	
	public JenaFederatedQueryProcessor(Environment env) {
		this.env = env;
		initDefaults();
		
		sourceSelecter = new SourceSelecter(env);
		queryDecomposer = new QueryDecomposer(env);
		queryRouter = new QueryRouter(env);
		resultIntegrator = new ResultIntegrator(env);
	}
	
	public void initDefaults() {
		this.env.setBoundJoin(true);
	}

	public ResultSet executeSelect(String queryString) {
		
		Query query = QueryFactory.create(queryString);
		
		//subquery source selection
		//TODO global caching in source selection
		sourceSelecter.selectSources(query);
		
		//decompose
		List<DecomposedQuery> decomposedQueries = queryDecomposer.decomposeQuery(query);
		
		//TODO optimizations: filter optimization
		
		//query routing
		//TODO bound join
		Model virtualModel = queryRouter.routQueries(decomposedQueries);
		
		
		//result integration
		ResultSet results = resultIntegrator.integrateResults(virtualModel, query);
		
		return results;
	}

}
