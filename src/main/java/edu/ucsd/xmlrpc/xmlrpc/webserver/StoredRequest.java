package edu.ucsd.xmlrpc.xmlrpc.webserver;

import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientRequestImpl;

/**
 * Represents a request that is stored on the WebServer
 */

public class StoredRequest {
	private XmlRpcClientRequestImpl request;
	private boolean valid;
	private Object result;
	
	/**
	 * Creates an instance
	 * @param request
	 */
	
	public StoredRequest(XmlRpcClientRequestImpl request) {
		this.request = request;
	}
	
	public boolean hasJobID(String jobID) {
		return request.getJobID().equals(jobID);
	}
	
	public XmlRpcClientRequestImpl getRequest() {
		return request;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	public Object getResult() {
		return result;
	}
	
	public void setResult(Object result) {
		this.result = result;
	}
}
