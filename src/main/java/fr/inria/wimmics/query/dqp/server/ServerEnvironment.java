package fr.inria.wimmics.query.dqp.server;

import org.apache.log4j.Logger;

import fr.inria.wimmics.common.utils.LoggerLocal;
import fr.inria.wimmics.query.dqp.Environment;
//this would be a good place to implement a global cach
public class ServerEnvironment {
	private static Logger log = LoggerLocal.getLogger(ServerEnvironment.class.getName());
	private static ServerEnvironment instance = null;
	
	
	
	public static ServerEnvironment getInstance() {
		if(instance == null) {
			instance = new ServerEnvironment();
			log.info("Server environment instantiated");
		}
		return instance;
	}
	
	
}
