package edu.ucsd.xmlrpc.xmlrpc.mapreduce;

import java.util.List;

import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;

/**
 * Work in progress
 */

//TODO interface for all mappers?
public abstract class StreamMapper {
	public MultiClient client;
	public StreamReducer reducerCallback;
	
	public Object dataStreamLock = new Object();
	public List<Object> dataStream; // TODO Synchronize
	
	public StreamMapper(MultiClient client) {
		this.client = client;
	}
	
	public abstract Object map();

	public void handleResult(Object result) {
		dataStream.add(result); // TODO synchronize
	}
}
