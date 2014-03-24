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
	private Model virtualModel;
	private Query query;
	private List<DecomposedQuery> decomposedQueries;
	
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
		
		query = QueryFactory.create(queryString);
		
		//subquery source selection
		//TODO global caching in source selection, now it's a session caching
		sourceSelecter.selectSources(query);
		
		//decompose
		//TODO - partially done - to be tested - usecase ? rdf:type ? 
		// implement a count for how many endpoints can solve a triple pattern, 
		//then group only the triple patterns which can be solved by only one endpoint,
		// if a triple pattern can be solved by more than one endpoint,
		// send a query with only that triple pattern to all the corresponding endpoints
		//ref: Querying Distributed RDF Data Sources with SPARQL
		decomposedQueries = queryDecomposer.decomposeQuery(query);
		
		//TODO optimizations: filter optimization
		//static optimizations here
		
		
		//query routing
		//DONE in bound join, keep trace of all the variable bindings - not only the previous query result variable bindings
		//DONE bound join flag check
		virtualModel = queryRouter.routQueries(decomposedQueries);
		
		
		//result integration
		ResultSet results = resultIntegrator.integrateResults(virtualModel, query);
		
		return results;
	}
	
	public List<DecomposedQuery> getDecomposedQueries() {
		return decomposedQueries;
	}
	
	public Model getVirtualModel() {
		return virtualModel;
	}
	public Query getQuery() {
		return query;
	}

}
