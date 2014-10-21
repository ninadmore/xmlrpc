package edu.ucsd.xmlrpc.xmlrpc.multiserver;

import java.net.URL;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;
import edu.ucsd.xmlrpc.xmlrpc.webserver.StoredRequest;

/**
 * The core request handler that handles faults and getting requests from this
 * server or remote servers. This handler is automatically added to the
 * MultiServer.
 */
public class CoreHandler {

  private MultiServer servlet;

  protected CoreHandler(MultiServer servlet) {
    this.servlet = servlet;
  }

  public Object delegate(String jobId, String methodName, Object[] args) {
    return jobId + '.' + methodName;
  }

  /**
   * Get the result of a job with the jobID on the server at URL url.
   *
   * @param jobID The job ID of the job to get.
   * @param url The url to get it from.
   * @return result
   * @throws XmlRpcException
   */
  public Object getRemoteResult(String jobID, URL url) throws XmlRpcException {
    return servlet.client.executeGet(jobID, url);
  }

  /**
   * Get the result of a job with the jobID on this server.
   *
   * @param jobID The job ID of the job to get.
   * @return result
   * @throws XmlRpcException If result is not found on server or if result is not valid yet.
   */
  public Object getResult(String jobID) throws XmlRpcException {
    StoredRequest request = servlet.getRequest(jobID);
    if (request == null) {
      throw new XmlRpcException("Result not found on server.");
    }
    if (request.isValid()) {
      // TODO DEMO
      return request.getResult().equals(-9) ? "-9" : request.getResult();
    } else {
      throw new XmlRpcException("Result not found on server.");
    }
  }
}
