package edu.ucsd.xmlrpc.xmlrpc.server;


public class SampleHandler {

	public int sum(int n0, int n1) {
	  System.out.println("here: " + n0 + ", " + n1);
		return n0 + n1;
	}

	public int mul(int n0, int n1) {
		return n0 * n1;
	}

	public long waitFor(long timeMs) {
		try {
      Thread.sleep(timeMs);
    } catch (InterruptedException ignore) {}
		return timeMs;
	}
}
