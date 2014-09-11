package edu.ucsd.xmlrpc.xmlrpc.server;

import java.net.MalformedURLException;

import edu.ucsd.xmlrpc.xmlrpc.Constants;
import edu.ucsd.xmlrpc.xmlrpc.multiclient.MultiClient;

public class SampleHandler {

	public int sum(int n0, int n1) {
		return n0 + n1;
	}

	public Object multiSum(int[] n) throws MalformedURLException {
		
		String[] urls = Constants.URLS;

		MultiClient c = new MultiClient(urls, null);
		SumMapper mapper = new SumMapper(c, n);

		SumReducerFactory reducerFactory = new SumReducerFactory(mapper);

		c.setClientCallbackFactory(reducerFactory);
		return mapper.map();
	}

	public int mul(int n0, int n1) {
		return n0 * n1;
	}

	public long waitFor(long timeMs) {
		long end = System.currentTimeMillis() + timeMs;
		while (System.currentTimeMillis() < end);
		long result = timeMs;

		return result;
	}
}
