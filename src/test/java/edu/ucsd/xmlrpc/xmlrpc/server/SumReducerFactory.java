package edu.ucsd.xmlrpc.xmlrpc.server;

import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;
import edu.ucsd.xmlrpc.xmlrpc.mapreduce.StreamReducer;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.ClientCallbackFactory;

public class SumReducerFactory extends ClientCallbackFactory {
	
	StreamReducer reducer;
	
	public SumReducerFactory(SumMapper mapper) {
		reducer = new SumReducer(mapper);
	}

	@Override
	public AsyncCallback getIntstace() {
		return reducer;
	}

}
