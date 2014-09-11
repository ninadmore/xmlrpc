package edu.ucsd.xmlrpc.xmlrpc.multiserver;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.server.RequestProcessorFactoryFactory.RequestProcessorFactory;

/**
 * A request handler factory interface. Also contains a default request handler factory implementation.
 */

public abstract class RequestHandlerFactory implements RequestProcessorFactory {
	
	public abstract Object getRequestProcessor(XmlRpcRequest request);
	
	/**
	 * A default request handler factory implementation. This factory will keep a singleton instance of the class
	 * specified instantiated with the no arg constructor.
	 */
	
	public static class DefaultFactory extends RequestHandlerFactory {
		private Class<?> c;
		private Object singleton;
		
		/**
		 * Create an instance of the factory. The factory will make a singleton no arg instance of the class c.
		 * @param c The class to instantiate.
		 */
		
		public DefaultFactory(Class<?> c) {
			this.c = c;
		}

		public Object getRequestProcessor(XmlRpcRequest request) {
			if (singleton == null) {
				try {
					singleton = c.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return singleton;
		}
	}
}
