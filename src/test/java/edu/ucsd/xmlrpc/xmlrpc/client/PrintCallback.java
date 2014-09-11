package edu.ucsd.xmlrpc.xmlrpc.client;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientRequestImpl;

public class PrintCallback implements AsyncCallback {

	public PrintCallback () {}

	public void handleError(XmlRpcRequest request, Throwable t) {
		t.printStackTrace();
	}

	public void handleResult(XmlRpcRequest request, Object result) {
		System.out.println(" --- " + ((XmlRpcClientRequestImpl) request).toString() + " = " + result);
	}
}
