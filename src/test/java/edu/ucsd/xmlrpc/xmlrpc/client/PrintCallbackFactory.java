package edu.ucsd.xmlrpc.xmlrpc.client;

import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.ClientCallbackFactory;

public class PrintCallbackFactory extends ClientCallbackFactory {
	
	private PrintCallback callback;

	public PrintCallbackFactory() {
		callback = new PrintCallback();
	}
	
	@Override
	public AsyncCallback getIntstace() {
		return callback;
	}

}
