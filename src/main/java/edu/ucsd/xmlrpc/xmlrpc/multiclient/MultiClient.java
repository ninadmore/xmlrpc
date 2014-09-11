package edu.ucsd.xmlrpc.xmlrpc.multiclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;
import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientConfigImpl;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientRequestImpl;

/**
 * A MultiClient can connect to multiple servers.
 */

public class MultiClient implements AsyncCallback{

	private MultiClientConnection[] connections;
	protected ClientCallbackFactory clientCallbackFactory;
	
	private int conInd = 0;

	private ConcurrentHashMap<String,RetryJob> retry = new ConcurrentHashMap<String,RetryJob>();
	
	/**
	 * Create a new MultiClient. This client keeps track of potential servers that it can connect to. 
	 * Under some fault conditions upon sending a job to the server, the client will try to send
	 * the job to another server if the job was not initiated, or get the result from another server
	 * if the job was submitted and the connection to the server was lost.
	 * @param serverUrls All of the client's potential connections.
	 * @throws MalformedURLException
	 */

	public MultiClient(String[] serverUrls, ClientCallbackFactory callback) throws MalformedURLException {
		// Set a callback that will call handleResult and handleError after fault handling
		clientCallbackFactory = callback;

		// Create a MultiClientConnection for each potential server
		connections = new MultiClientConnection[serverUrls.length];
		for (int i = 0; i < serverUrls.length; i++) {
			connections[i] = new MultiClientConnection(serverUrls[i], this);
		}
	}
	
	/**
	 * This method is used to get the precomputed result of a job on a specific server.
	 * @param jobID The job to look for.
	 * @param url The URL on which to look.
	 * @return The result of the job
	 * @throws XmlRpcException
	 */
	
	public Object executeGet(String jobID, URL url) throws XmlRpcException {
		for (MultiClientConnection con : connections) {
			if (con.getURL().equals(url)) {
				return con.execute("CoreHandler.getResult", new Object[]{jobID});
			}
		}
		return null;
	}
	
	/**
	 * Execute the method asynchronously on the servers with the specified args.
	 * @param method The method name to execute. The method name is specified by 
	 * "name-of-the-handler-class.method-name". For example to execute the method "sum" in the handler 
	 * class "SampleHander", call execute with "SampleHandler.sum".
	 * @param args The args to pass to the method.
	 */

	public void executeAsync(String method, Object...args) {
		try {
			// execute async on some connection
			connections[conInd].executeAsync(method, args);
			conInd = (conInd + 1) % connections.length;
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the ClientCallbackFactory.
	 * @param clientCallbackFactory
	 */

	public void setClientCallbackFactory(ClientCallbackFactory clientCallbackFactory) {
		this.clientCallbackFactory = clientCallbackFactory;
	}
	
	/**
	 * Handle the result of an asynchronous call. An instance from the current ClientCallbackFactory
	 * is created and handleResult is invoked on it.
	 */
	
	public void handleResult(XmlRpcRequest pRequest, Object pResult) {
		XmlRpcClientRequestImpl request = (XmlRpcClientRequestImpl) pRequest;
		
		// Job no longer needs to be retried.
		retry.remove(request.getJobID());
		clientCallbackFactory.getIntstace().handleResult(request, pResult);
	}
	
	/**
	 * Handle the error of an asynchronous call. An instance from the current ClientCallbackFactory
	 * is created and handleError is invoked on it after expected faults have been already handled.
	 */

	public void handleError(XmlRpcRequest pRequest, Throwable t) {
		XmlRpcClientRequestImpl request = (XmlRpcClientRequestImpl) pRequest;
		RetryJob job = retry.get(request.getJobID());
		String message = t.getMessage();
		
		if (t.getCause() instanceof IOException || message.startsWith("Result not found on server.")) {  // TODO DEMO correct error message
			// Connection not initiated, or result is not ready. RETRY the job.
			if (job == null) {
				job = new RetryJob(request, 0);
				retry.put(request.getJobID(), job);
			}
			job.connectionIndex = (job.connectionIndex + 1) % connections.length;

			try {
				MultiClientConnection.switchURL(connections[job.connectionIndex], request);
				connections[job.connectionIndex].executeAsync(request);
			} catch (XmlRpcException e) {
				e.printStackTrace();
			}
		} else if (message.startsWith("Server Disconnected")) { // TODO DEMO correct error message
			// Job was submitted but the server was disconnected. GET the job.
			if (job != null) {
				retry.remove(request.getJobID());
			}
			
			XmlRpcClientConfigImpl conf = (XmlRpcClientConfigImpl) request.getConfig();
			executeAsync("CoreHandler.getRemoteResult", request.getJobID(), conf.getServerURL());
			
		} else {
			// Unexpected error
			clientCallbackFactory.getIntstace().handleError(request, t);
		}
	}
	
	private class RetryJob {
		public XmlRpcRequest request;
		public int connectionIndex;

		public RetryJob (XmlRpcRequest request, int connectionIndex) {
			this.request = request;
			this.connectionIndex = connectionIndex;
		}
	}
}
