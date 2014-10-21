package edu.ucsd.xmlrpc.xmlrpc.multiclient;

import java.net.MalformedURLException;
import java.net.URL;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;
import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClient;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientConfigImpl;

class MultiClientConnection extends XmlRpcClient {
	
	private static final int TIMEOUT = 10;
	private MultiClient client;
	
	// Represents a single connection between the client and a server.
	protected MultiClientConnection(String URL, MultiClient c) throws MalformedURLException {
		super();
		this.client = c;
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setServerURL(new URL(URL));
        config.setEnabledForExtensions(true);  
        config.setConnectionTimeout(TIMEOUT * 1000);
        config.setReplyTimeout(TIMEOUT * 1000);
		
		this.setConfig(config);
	}
	
	// Execute on a single server
	protected void executeAsync(String method, Object...args) throws XmlRpcException {
		this.executeAsync(method, args, client);
	}

	// Execute on a single server
	protected void executeAsync(XmlRpcRequest request) throws XmlRpcException {
		this.executeAsync(request, client);
	}

	// Helper method to switch the URL of a request which is in the request config.
	protected static void switchURL(MultiClientConnection connection, XmlRpcRequest request) throws XmlRpcException {
		XmlRpcClientConfigImpl config = (XmlRpcClientConfigImpl) request.getConfig();
		URL newURL = ((XmlRpcClientConfigImpl) connection.getClientConfig()).getServerURL();
		config.setServerURL(newURL);
	}
	
	protected URL getURL() {
		XmlRpcClientConfigImpl config = (XmlRpcClientConfigImpl) getConfig();
		return config.getServerURL();
	}
}
