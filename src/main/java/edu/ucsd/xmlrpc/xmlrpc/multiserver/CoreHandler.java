package edu.ucsd.xmlrpc.xmlrpc.multiserver;

import java.net.URL;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;
import edu.ucsd.xmlrpc.xmlrpc.webserver.StoredRequest;

/**
 * The core request handler that handles faults and getting requests from this
 * server or remote servers. This handler is automatically added to the
 * MultiServer.
 */
public final class CoreHandler {

  public static final String DELEGATOR_METHOD = "CoreHandler.delegate";
  public static final String FETCH_RESULT_METHOD = "CoreHandler.fetchResult";
  public static final String FIND_RESULT_METHOD = "CoreHandler.findResult";
  public static final String GET_REMOTE_RESULT_METHOD = "CoreHandler.getRemoteResult";
  public static final String GET_RESULT_METHOD = "CoreHandler.getResult";
  public static final String JOB_ID_FUNCTION_METHOD = "CoreHandler.jobIdFunction";

  public static final String RESULT_NOT_FOUND = "Result not found on server.";
  public static final String RETRY_SAME_CONNECTION = "retry same connection";

  private static final int MAX_RETRIES = 200;
  private static final int RETRY_SLEEP_MS = 200;

  private MultiServer servlet;

  protected CoreHandler(MultiServer servlet) {
    this.servlet = servlet;
  }

  public Object delegate(String jobId, String methodName, Object[] args) {
    servlet.client.executeAsyncJob(methodName, jobId, args);
    // return jobId + '.' + methodName;
    return new NoResult();
  }

  /** Copies result of jobs to this server. */
  public Object fetchResult(String jobId) throws XmlRpcException {
    StoredRequest request = servlet.getRequest(jobId);
    if (request == null) {
      servlet.client.executeAsync(FIND_RESULT_METHOD, jobId);
      throw new XmlRpcException(RETRY_SAME_CONNECTION);
    } else if (request.isValid()) {
      return request.getResult();
    } else {
      throw new XmlRpcException(RETRY_SAME_CONNECTION);
    }
  }

  public Object findResult(String jobId) throws XmlRpcException {
    StoredRequest request = servlet.getRequest(jobId);
    if (request != null && !request.getRequest().getMethodName().equals(FIND_RESULT_METHOD)) {
      for (int retries = 0; !request.isValid() && retries < MAX_RETRIES; retries++) {
        //System.out.println("not ready: " + jobId + ": " + request.getRequest().getMethodName());
        sleep();
        retries++;
      }
    }
    if (request != null && request.isValid()) {
      return request.getResult();
    }
    throw new XmlRpcException("don't retry");
  }

  /**
   * Get the result of a job with the jobId on the server at URL url.
   *
   * @param jobId The job ID of the job to get.
   * @param url The url to get it from.
   * @return result
   * @throws XmlRpcException
   */
  public Object getRemoteResult(String jobId, URL url) throws XmlRpcException {
    return servlet.client.executeGet(jobId, url);
  }

  /**
   * Get the result of a job with the jobId on this server.
   *
   * @param jobId The job ID of the job to get.
   * @return result
   * @throws XmlRpcException If result is not found on server or if result is not valid yet.
   */
  public Object getResult(String jobId) throws XmlRpcException {
    StoredRequest request = servlet.getRequest(jobId);
    if (request == null) {
      throw new XmlRpcException(RESULT_NOT_FOUND);
    }
    if (request.isValid()) {
      // TODO DEMO
      return request.getResult().equals(-9) ? "-9" : request.getResult();
    } else {
      throw new XmlRpcException(RESULT_NOT_FOUND);
    }
  }

  public Object jobIdFunction(String methodName, String jobId, Object[] jobIds)
      throws XmlRpcException {
    Object[] results = new Object[jobIds.length];
    for (int i = 0; i < results.length;i++) {
      String remoteJobId = (String) jobIds[i];
      try {
        results[i] = fetchResult(remoteJobId);
      } catch (XmlRpcException retry) {}
      for (int retries = 0; results[i] == null && retries < MAX_RETRIES * 2; retries++) {
        sleep();
        StoredRequest request = servlet.getRequest(remoteJobId);
        if (request != null && request.isValid()) {
          results[i] = request.getResult();
        }
      }
      if (results[i] == null) {
        throw new XmlRpcException("jobs not found on server");
      }
    }
    servlet.client.executeAsyncJob(methodName, jobId, results);
    // return methodName + " = " + jobIds[0] + " + " + jobIds[1] + " = " + results[0] + " + " + results[1];
    return new NoResult();
  }

  private void sleep() {
    try {
      Thread.sleep(RETRY_SLEEP_MS);
    } catch (InterruptedException ignore) {}
  }
}
