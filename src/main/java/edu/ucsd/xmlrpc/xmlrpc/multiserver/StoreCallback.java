package edu.ucsd.xmlrpc.xmlrpc.multiserver;

import java.util.UUID;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientRequestImpl;
import edu.ucsd.xmlrpc.xmlrpc.webserver.StoredRequest;
import edu.ucsd.xmlrpc.xmlrpc.webserver.WebServer;

public class StoreCallback implements AsyncCallback {

  private static final int uuidLength = UUID.randomUUID().toString().length();

  WebServer server;

	public StoreCallback (WebServer server) {
	  this.server = server;
	}

	public void handleError(XmlRpcRequest request, Throwable t) {
	  // TODO error
	}

	public void handleResult(XmlRpcRequest request, Object result) {
	  XmlRpcClientRequestImpl req = (XmlRpcClientRequestImpl) request;
	  if (req.getJobID().length() != uuidLength) { // if StreamMapper job
	    server.addRequest(req.getJobID(), new StoredRequest(req, result));
	  }
	}
}
