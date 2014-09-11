package edu.ucsd.xmlrpc.xmlrpc.server;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.mapreduce.StreamReducer;

/**
 * Work in progress
 */

public class SumReducer extends StreamReducer {

	public SumReducer(SumMapper mapper) {
		super(mapper);
	}

	public void handleResult(XmlRpcRequest request, Object result) {
		mapper.handleResult(result);
	}

	public void handleError(XmlRpcRequest result, Throwable error) {
		System.out.println("THIS SHOULD NOT OCCUR");
	}

}
