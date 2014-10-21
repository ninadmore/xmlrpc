package edu.ucsd.xmlrpc.xmlrpc;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.ucsd.xmlrpc.xmlrpc.client.PrintCallbackFactory;
import edu.ucsd.xmlrpc.xmlrpc.mapreduce.StreamMapper;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;
import edu.ucsd.xmlrpc.xmlrpc.multiserver.MultiServer;

public class Demo {

	public static void main (String [] args) {

		try {
			BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.FATAL);

			String[] urls = Constants.URLS;
			Class<?>[] handlers = Constants.HANDLERS;

			MultiClient client = new MultiClient(urls, new PrintCallbackFactory());

			MultiServer server0 = new MultiServer(urls, 0, handlers);
			server0.start();

			MultiServer server1 = new MultiServer(urls, 1, handlers);
			server1.start();

			MultiServer server2 = new MultiServer(urls, 2, handlers);
			server2.start();

			System.out.println("All servers started");

//			waitFor(1000*1);

//			client.executeAsync("SampleHandler.sum", Integer.parseInt(args[0]), Integer.parseInt(args[1]));
//			client.executeAsync("SampleHandler.sum", -11, 2);
//
//			client.executeAsync("SampleHandler.mul", 3, 7);
//
//			client.executeAsync("SampleHandler.waitFor", new Long(1000*5));

//			waitFor(1000*7);

			StreamMapper streamJob = new StreamMapper(client);
			streamJob.processData("SampleHandler.sum", "test");

			server0.printRequests();
			server1.printRequests();
			server2.printRequests();
			while(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void waitFor(long timeMs) {
    try {
      Thread.sleep(timeMs);
    } catch (InterruptedException ignore) {}
	}
}
