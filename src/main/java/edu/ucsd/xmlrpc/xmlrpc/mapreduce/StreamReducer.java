package edu.ucsd.xmlrpc.xmlrpc.mapreduce;

import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;

public abstract class StreamReducer implements AsyncCallback {
	public StreamMapper mapper;
	
	public StreamReducer(StreamMapper mapper) {
		this.mapper = mapper;
	}
}
