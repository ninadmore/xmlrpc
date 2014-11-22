package edu.ucsd.xmlrpc.xmlrpc;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.ucsd.xmlrpc.xmlrpc.client.PrintCallbackFactory;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;
import edu.ucsd.xmlrpc.xmlrpc.multiserver.MultiServer;

public class Demo {

  public static void main (String [] args) {
    try {
      BasicConfigurator.configure();
      Logger.getRootLogger().setLevel(Level.FATAL);

      //String[] urls = Constants.URLS;
      Class<?>[] handlers = Constants.HANDLERS;
      String[] urls = new String[]{"http://127.0.0.1:8003"};

      MultiClient client = new MultiClient(urls, new PrintCallbackFactory());

      MultiServer server0 = new MultiServer(urls, 0, handlers);
      server0.start();
//
//      MultiServer server1 = new MultiServer(urls, 1, handlers);
//      server1.start();
//
//      MultiServer server2 = new MultiServer(urls, 2, handlers);
//      server2.start();
//
      System.out.println("All servers started");
//
//      waitFor(1000*1);
//
//      client.executeAsync("SampleHandler.sum", Integer.parseInt(args[0]), Integer.parseInt(args[1]));
//      client.executeAsync("SampleHandler.sum", -11, 2);
//      client.executeAsync("SampleHandler.sum", 2, 2);
//
//      client.executeAsync("SampleHandler.zMat", BitmapFactory.decodeByteArray(new byte[0], 0, 0));
//
//      client.executeAsync("SampleHandler.waitFor", new Long(1000*5));
//
//      waitFor(1000*7);
//
//      client.executeAsyncJob("SampleHandler.sum", "testJobId", 3, 7);

      client.executeAsync("FrameHandler.get", new byte[]{1,2,3});

//      StreamMapper streamJob = new StreamMapper(client);
//      streamJob.processData("SampleHandler.sum", 0, 1);
//      streamJob.processData("SampleHandler.sum", 0, 2);
//      streamJob.processData("SampleHandler.sum", 0, 3);
//      streamJob.processData("SampleHandler.sum", 0, 4);
//      streamJob.processData("SampleHandler.sum", 0, 5);
//      streamJob.processData("SampleHandler.sum", 0, 6);
//      streamJob.processData("SampleHandler.sum", 0, 7);
//      streamJob.processData("SampleHandler.sum", 0, 8);
//    waitFor(1000*3);
//
//      StreamMapper streamJob2 = new StreamMapper(client);
//      streamJob2.processData("SampleHandler.sum", 0, 1);
//      streamJob2.processData("SampleHandler.sum", 0, 2);
//      streamJob2.processData("SampleHandler.sum", 0, 3);
//      streamJob2.processData("SampleHandler.sum", 0, 4);
//      streamJob2.processData("SampleHandler.sum", 0, 5);
//      streamJob2.processData("SampleHandler.sum", 0, 6);
//      streamJob2.processData("SampleHandler.sum", 0, 7);
//      streamJob2.processData("SampleHandler.sum", 0, 8);
//
//      StreamReducer streamReducer = new SumStreamReducer(streamJob);
//      streamReducer.start();
//      Iterator<Class<Void>> it = streamReducer.getResultIterator();
//      while (it.hasNext()) {
//        it.next();
//      }
//
//      StreamReducer streamReducer2 = new SumStreamReducer(streamJob2);
//      streamReducer2.start();
//      Iterator<Class<Void>> it2 = streamReducer2.getResultIterator();
//      while (it2.hasNext()) {
//        it2.next();
//      }
//
//      waitFor(1000*7);

//      server0.printRequests();
//      server1.printRequests();
//      server2.printRequests();
      server0.printHandlers();
      while(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void waitFor(long timeMs) {
    try {
      //      System.out.println("\n *WAIT*\n");
      Thread.sleep(timeMs);
    } catch (InterruptedException ignore) {}
  }
}
