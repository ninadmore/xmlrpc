package edu.ucsd.xmlrpc.xmlrpc;

import edu.ucsd.xmlrpc.xmlrpc.server.FrameHandler;
import edu.ucsd.xmlrpc.xmlrpc.server.SampleHandler;

public class Constants {
	public static final String[] URLS = new String[] {
		"http://localhost:8083",
	};

	public static final Class<?>[] HANDLERS = new Class<?>[] {
	  FrameHandler.class,
		SampleHandler.class
	};

	public static final Class<?>[] NOHANDLERS = new Class<?>[] {};
}
