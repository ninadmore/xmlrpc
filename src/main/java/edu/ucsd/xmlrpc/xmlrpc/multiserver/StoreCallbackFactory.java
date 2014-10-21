package edu.ucsd.xmlrpc.xmlrpc.multiserver;

import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.ClientCallbackFactory;
import edu.ucsd.xmlrpc.xmlrpc.webserver.WebServer;

class StoreCallbackFactory extends ClientCallbackFactory {

	private StoreCallback callback;

	public StoreCallbackFactory(WebServer server) {
    callback = new StoreCallback(server);
	}

	@Override
	public AsyncCallback getIntstace() {
		return callback;
	}
}
