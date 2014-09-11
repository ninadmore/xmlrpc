package edu.ucsd.xmlrpc.xmlrpc.multiclient;

import edu.ucsd.xmlrpc.xmlrpc.client.AsyncCallback;

/**
 * Interface to a ClientCallbackFactory. Should be implemented by the user to handle results and errors
 * from executing on the servers after the MultiClient has taken care of fault tolerance.
 */

public abstract class ClientCallbackFactory {

	/**
	 * Get the instance of the AsyncCallback handler.
	 * @return AsyncCallback handler
	 */
	public abstract AsyncCallback getIntstace();
}
