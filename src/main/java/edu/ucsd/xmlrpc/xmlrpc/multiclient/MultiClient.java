package edu.ucsd.xmlrpc.xmlrpc.multiclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;
import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientConfigImpl;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientRequestImpl;
import edu.ucsd.xmlrpc.xmlrpc.multiserver.CoreHandler;
import edu.ucsd.xmlrpc.xmlrpc.multiserver.NoResult;

/** A MultiClient can connect to multiple servers. */
public class MultiClient implements AsyncCallback{

  public static final int MIN_RETRY_SLEEP_MS = 100;

  protected ClientCallbackFactory clientCallbackFactory;

	private MultiClientConnection[] connections;
	private int conIndex = 0;
	private int clientIndex = -1; // used in MultiServer;
	private ConcurrentHashMap<String, RetryJob> retry = new ConcurrentHashMap<String, RetryJob>();
	private int retrySleepMs = 300;

	/**
	 * Create a new MultiClient. This client keeps track of potential servers that it can connect to.
	 * Under some fault conditions upon sending a job to the server, the client will try to send
	 * the job to another server if the job was not initiated, or get the result from another server
	 * if the job was submitted and the connection to the server was lost.
	 * @param serverUrls All of the client's potential connections.
	 * @throws MalformedURLException
	 */
	public MultiClient(String[] serverUrls, ClientCallbackFactory callback) throws MalformedURLException {
		// set a callback that will call handleResult and handleError after fault handling
		clientCallbackFactory = callback;

		// create a MultiClientConnection for each potential server
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
				return con.execute(CoreHandler.GET_RESULT_METHOD, new Object[]{jobID});
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
		executeAsyncJob(method, UUID.randomUUID().toString(), args);
	}

  /**
   * Execute the method asynchronously on the servers with the specified args.
   * @param methodName The method name to execute. The method name is specified by
   * "name-of-the-handler-class.method-name". For example to execute the method "sum" in the handler
   * class "SampleHander", call execute with "SampleHandler.sum".
   * @param args The args to pass to the method.
   */
  public void executeAsyncJob(String methodName, String jobId, Object...args) {
    try {
      // execute async on some connection
      connections[conIndex].executeAsync(methodName, jobId, args);
      conIndex = nextConnection(conIndex);
    } catch (XmlRpcException e) {
      e.printStackTrace();
    }
  }

  public void executeJobIdFunction(String methodName, String jobId, String[] jobIds) {
    try {
      connections[conIndex].executeAsync(CoreHandler.JOB_ID_FUNCTION_METHOD,
          new Object[] {methodName, jobId, jobIds});
    } catch (XmlRpcException e) {
      e.printStackTrace();
    }
  }

  public void executeStreamRequest(String methodName, String jobId, Object[] args) {
    try {
      connections[conIndex].executeAsync(
          CoreHandler.DELEGATOR_METHOD, new Object[] {jobId, methodName, args});
    } catch (XmlRpcException e) {
      e.printStackTrace();
    }
  }

  public int getRetrySleepMs() {
    return retrySleepMs;
  }

  /**
   * Handle the result of an asynchronous call. An instance from the current ClientCallbackFactory
   * is created and handleResult is invoked on it. Void type is ignored.
   */
  public void handleResult(XmlRpcRequest pRequest, Object pResult) {
    XmlRpcClientRequestImpl request = (XmlRpcClientRequestImpl) pRequest;

    // Job no longer needs to be retried.
    retry.remove(request.getJobID());

    // remove suppressed results from map-reduce
    if (!(pResult instanceof NoResult) && !(pResult instanceof Void)) {
      clientCallbackFactory.getIntstace().handleResult(request, pResult);
    }
  }

  /**
   * Handle the error of an asynchronous call. An instance from the current ClientCallbackFactory
   * is created and handleError is invoked on it after expected faults have been already handled.
   */
  public void handleError(XmlRpcRequest pRequest, Throwable t) {
    XmlRpcClientRequestImpl request = (XmlRpcClientRequestImpl) pRequest;
    RetryJob job = retry.get(request.getJobID());
    String message = t.getMessage();
    // System.out.println("err: " + message);

    if (t.getCause() instanceof IOException || message.equals(CoreHandler.RESULT_NOT_FOUND)) {
      // connection not initiated, or result is not ready. RETRY the job.
      if (job == null) {
        job = new RetryJob(request, 0);
        retry.put(request.getJobID(), job);
      }
      job.connectionIndex = nextConnection(job.connectionIndex);

      try {
        MultiClientConnection.switchURL(connections[job.connectionIndex], request);
        connections[job.connectionIndex].executeAsync(request);
      } catch (XmlRpcException e) {
        e.printStackTrace();
      }
    } else if (message.equals(CoreHandler.RETRY_SAME_CONNECTION)) {
      sleep();
      try {
        connections[conIndex].executeAsync(request);
      } catch (XmlRpcException ignore) {}
    } else if (message.startsWith("Server Disconnected")) {
      // Job was submitted but the server was disconnected. GET the job.
      if (job != null) {
        retry.remove(request.getJobID());
      }

      XmlRpcClientConfigImpl conf = (XmlRpcClientConfigImpl) request.getConfig();
      executeAsync(CoreHandler.GET_REMOTE_RESULT_METHOD, request.getJobID(), conf.getServerURL());
    } else {
      // unexpected error
      clientCallbackFactory.getIntstace().handleError(request, t);
    }
  }

	/**
	 * Set the ClientCallbackFactory.
	 * @param clientCallbackFactory
	 */
	public void setClientCallbackFactory(ClientCallbackFactory clientCallbackFactory) {
		this.clientCallbackFactory = clientCallbackFactory;
	}

  /** For use in MultiServer. */
  public void setClientIndex(int index) {
    this.clientIndex = index;
  }

	public void setRetrySleepMs(int retrySleepMs) {
	  if (retrySleepMs >= MIN_RETRY_SLEEP_MS) {
	    this.retrySleepMs = retrySleepMs;
	  }
	}

	private int nextConnection(int conIndex) {
	  return (conIndex + (conIndex + 1 == clientIndex ? 2 : 1)) % connections.length;
	}

  private void sleep() {
    try {
      Thread.sleep(retrySleepMs);
    } catch (InterruptedException ignore) {}
  }

  private class RetryJob {

		public XmlRpcRequest request; // for possible future use
		public int connectionIndex;

		public RetryJob (XmlRpcRequest request, int connectionIndex) {
			this.request = request;
			this.connectionIndex = connectionIndex;
		}
	}
}
