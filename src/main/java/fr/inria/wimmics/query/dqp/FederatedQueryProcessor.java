package fr.inria.wimmics.query.dqp;

import java.util.List;

import com.hp.hpl.jena.query.ResultSet;

public interface FederatedQueryProcessor {

	public ResultSet executeSelect(String query);
}
