package edu.ucsd.xmlrpc.xmlrpc;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.ucsd.xmlrpc.xmlrpc.client.PrintCallbackFactory;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;
import edu.ucsd.xmlrpc.xmlrpc.multiserver.MultiServer;
import edu.ucsd.xmlrpc.xmlrpc.multiserver.RequestHandlerFactory;
import edu.ucsd.xmlrpc.xmlrpc.server.SampleHandler;

public class Demo {

	public static void main (String [] args) {

		try {
			BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.FATAL);
			
			String[] urls = Constants.URLS;
			Class<?>[] handlers = Constants.NOHANDLERS;
			
			MultiClient client = new MultiClient(urls, new PrintCallbackFactory());
			
			MultiServer server0 = new MultiServer(urls, 0, handlers);
			server0.start();

			MultiServer server1 = new MultiServer(urls, 1, handlers);
			server1.start();

			MultiServer server2 = new MultiServer(urls, 2, handlers);
			server2.start();			

			System.out.println("All servers started");
			
			server1.addRequestProcessor(SampleHandler.class);
			server2.addRequestProcessor(SampleHandler.class);
			server0.addRequestProcessor(SampleHandler.class, new RequestHandlerFactory.DefaultFactory(SampleHandler.class));

			waitFor(1000*1);

			client.executeAsync("SampleHandler.sum", Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			client.executeAsync("SampleHandler.sum", -11, 2);

			client.executeAsync("SampleHandler.mul", 3, 7);
			
			client.executeAsync("SampleHandler.waitFor", new Long(1000*5));
			
			client.executeAsync("SampleHandler.multiSum", new int[]{1,2,3,4,5,6,7,8,9,10,11});//66
			client.executeAsync("SampleHandler.multiSum", new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20});//210
			
			waitFor(1000*10);
			
			server0.printRequests();
			server1.printRequests();
			server2.printRequests();
			while(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static long waitFor(long timeMs) {
		long end = System.currentTimeMillis() + timeMs;
		while (System.currentTimeMillis() < end);
		return timeMs;
	}
}
