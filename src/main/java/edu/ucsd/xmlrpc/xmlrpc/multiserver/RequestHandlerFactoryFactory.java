package edu.ucsd.xmlrpc.xmlrpc.multiserver;

import java.util.HashMap;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;
import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.server.RequestProcessorFactoryFactory;

/**
 * The request handler factory factory of the server.
 */

class RequestHandlerFactoryFactory implements RequestProcessorFactoryFactory {
	private HashMap<Class<?>, RequestHandlerFactory> factoryMap;

	protected RequestHandlerFactoryFactory(MultiServer servlet, Class<?>[] handlers) {
		factoryMap = new HashMap<Class<?>, RequestHandlerFactory>();
		
		factoryMap.put(CoreHandler.class, new CoreHandlerFactory(servlet));
		for (Class<?> c : handlers) {
			factoryMap.put(c, new RequestHandlerFactory.DefaultFactory(c));
		}
	}
	
	/**
	 * Get the requestProcessorFactory of the class c.
	 */

	@SuppressWarnings("rawtypes")
	public RequestProcessorFactory getRequestProcessorFactory(Class c)
			throws XmlRpcException {
		return factoryMap.get(c);
	}
	
	/**
	 * Add a requestProcessor with a specified RequestHandlerFactory.
	 * @param requestProcessor The request processor class.
	 * @param factory The request processor class' factory.
	 */
	
	protected void addRequestProcessor(Class<?> requestProcessor, RequestHandlerFactory factory) {
		factoryMap.put(requestProcessor, factory);
	}

	// Special factory for the CoreHandler
	private class CoreHandlerFactory extends RequestHandlerFactory {

		private final MultiServer servlet;
		private CoreHandler requestHandler;
		
		private CoreHandlerFactory(MultiServer servlet) {
			this.servlet = servlet;
		}
		
		public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest) {
			if (requestHandler == null) {
				this.requestHandler = new CoreHandler(servlet);
			}
			return requestHandler;
		}
	}
}
