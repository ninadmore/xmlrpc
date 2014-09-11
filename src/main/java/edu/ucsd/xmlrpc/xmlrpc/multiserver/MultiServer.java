package edu.ucsd.xmlrpc.xmlrpc.multiserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;
import edu.ucsd.xmlrpc.xmlrpc.server.PropertyHandlerMapping;
import edu.ucsd.xmlrpc.xmlrpc.server.XmlRpcServer;
import edu.ucsd.xmlrpc.xmlrpc.server.XmlRpcServerConfigImpl;
import edu.ucsd.xmlrpc.xmlrpc.webserver.StoredRequest;
import edu.ucsd.xmlrpc.xmlrpc.webserver.WebServer;
import edu.ucsd.xmlrpc.xmlrpc.client.XmlRpcClientRequestImpl;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;

/**
 * A MultiServer has a connection to all other servers.
 */
public class MultiServer extends WebServer {
	
	protected MultiClient client; // connection to other servers
	private RequestHandlerFactoryFactory requestHandlerFactoryFactory;
	
	/**
	 * Creates a new instance of a MultiServer which is connected to all of the servers listed in the urls.
	 * The url of the server is specified urls[i].
	 * The requestProcessors are the request processor handlers for the client requests. All methods in these classes
	 * are reflexively scanned for methods.
	 * @param urls The server urls of all of the servers to connect.
	 * @param i The index of this server's url in urls
	 * @param requestProcessors An array of request processors.
	 * @throws MalformedURLException
	 */
	
	public MultiServer(String[] urls, int i, Class<?>[] requestProcessors) throws MalformedURLException {
		super((new URL(urls[i])).getPort());
		
		// Request processors
		setHandlers(requestProcessors);
		
		// Connection to other servers
		client = new MultiClient(urls, null);

		// Setup config
		XmlRpcServerConfigImpl serverConfig =
				(XmlRpcServerConfigImpl) getXmlRpcServer().getConfig();
		serverConfig.setEnabledForExtensions(true);
		serverConfig.setContentLengthOptional(false);

	}

	// Add all the request processors to the server
	private void setHandlers(Class<?>[] requestProcessors) {
		PropertyHandlerMapping phm = new PropertyHandlerMapping();

		requestHandlerFactoryFactory = new RequestHandlerFactoryFactory(this, requestProcessors);
		phm.setRequestProcessorFactoryFactory(requestHandlerFactoryFactory);
		// Add core handler
		try {
			phm.addHandler(CoreHandler.class.getSimpleName(), CoreHandler.class);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		
		// Add user defined handlers
		for (Class<?> c : requestProcessors) {
			try {
				phm.addHandler(c.getSimpleName(), c);
			} catch (XmlRpcException e) {
				e.printStackTrace();
			}
		}

		XmlRpcServer xmlRpcServer = getXmlRpcServer();
		xmlRpcServer.setHandlerMapping(phm);
	}
	
	/**
	 * Add a requestProcessor with a default RequestHandlerFactory.
	 * @param requestProcessor The request processor class.
	 */
	
	public void addRequestProcessor(Class<?> requestProcessor) {
		requestHandlerFactoryFactory.addRequestProcessor(requestProcessor,
				new RequestHandlerFactory.DefaultFactory(requestProcessor));
		addRequestHanderPHM(requestProcessor);
	}

	/**
	 * Add a requestProcessor with a specified RequestHandlerFactory.
	 * @param requestProcessor The request processor class.
	 * @param factory The request processor class' factory.
	 */
	
	public void addRequestProcessor(Class<?> requestProcessor, RequestHandlerFactory factory) {
		requestHandlerFactoryFactory.addRequestProcessor(requestProcessor, factory);
		addRequestHanderPHM(requestProcessor);
	}
	
	// Add the requestProcessor to the XmlRpcServer's handler mapping
	private void addRequestHanderPHM(Class<?> requestProcessor) {
		PropertyHandlerMapping phm = (PropertyHandlerMapping) getXmlRpcServer().getHandlerMapping();
		try {
			phm.addHandler(requestProcessor.getSimpleName(), requestProcessor);
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
		getXmlRpcServer().setHandlerMapping(phm);
	}

	/**
	 * Print all the handlers with their methods on the server.
	 */
	
	public void printHandlers() {
		try {
			PropertyHandlerMapping phm = (PropertyHandlerMapping) getXmlRpcServer().getHandlerMapping();
			String[] methods = phm.getListMethods();

			System.out.println(methods.length + " methods on " + getPort());
			for (String s : methods) {
				System.out.println(s);
			}
		} catch (XmlRpcException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Print all the requests that have been processed on the server.
	 */
	
	// TODO DEMO Remove
	
	public void printRequests() {
		String ident = this.getPort() + "";
		System.out.println("Printing all requests on " + ident);
		for (Entry<String, StoredRequest> entry : getStoredRequests()) {
			XmlRpcClientRequestImpl r = entry.getValue().getRequest();
			System.out.println(" - " + r.toString());
		}
		System.out.println("Done printing requests on " + ident);
	}
}
