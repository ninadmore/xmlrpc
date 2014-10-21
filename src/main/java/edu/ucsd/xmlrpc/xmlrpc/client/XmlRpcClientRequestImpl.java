/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.ucsd.xmlrpc.xmlrpc.client;

import java.util.List;
import java.util.UUID;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest;
import edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequestConfig;


/** Default implementation of
 * {@link edu.ucsd.xmlrpc.xmlrpc.XmlRpcRequest}.
 */
public class XmlRpcClientRequestImpl implements XmlRpcRequest {
    private static final Object[] ZERO_PARAMS = new Object[0];
    private final XmlRpcRequestConfig config;
	private final String methodName;
	private final Object[] params;
	// Ucsd modified code
	private final String jobID;


	/** Creates a new instance.
	 * @param pConfig The request configuration.
	 * @param pMethodName The method name being performed.
	 * @param pParams The parameters.
	 * @param pJobID The job ID
	 * @throws NullPointerException One of the parameters is null.
	 */
	public XmlRpcClientRequestImpl(XmlRpcRequestConfig pConfig,
								   String pMethodName, Object[] pParams, String pJobID) {
		config = pConfig;
		if (config == null) {
			throw new NullPointerException("The request configuration must not be null.");
		}
		methodName = pMethodName;
		if (methodName == null) {
			throw new NullPointerException("The method name must not be null.");
		}
		params = pParams == null ? ZERO_PARAMS : pParams;
		jobID = pJobID;
	}

	/** Creates a new instance.
	 * @param pConfig The request configuration.
	 * @param pMethodName The method name being performed.
	 * @param pParams The parameters.
	 * @param pJobID The job ID
	 * @throws NullPointerException One of the parameters is null.
	 */
	public XmlRpcClientRequestImpl(XmlRpcRequestConfig pConfig,
								   String pMethodName, List pParams, String pJobID) {
		this(pConfig, pMethodName, pParams == null ? null : pParams.toArray(), pJobID);
	}

	/** Creates a new instance. A job ID is randomly assigned.
	 * @param pConfig The request configuration.
	 * @param pMethodName The method name being performed.
	 * @param pParams The parameters.
	 * @throws NullPointerException One of the parameters is null.
	 */
	public XmlRpcClientRequestImpl(XmlRpcRequestConfig pConfig,
								   String pMethodName, Object[] pParams) {
		this(pConfig, pMethodName, pParams, UUID.randomUUID().toString());
	}

	/** Creates a new instance. A job ID is randomly assigned.
	 * @param pConfig The request configuration.
	 * @param pMethodName The method name being performed.
	 * @param pParams The parameters.
	 * @throws NullPointerException The method name or the parameters are null.
	 */
	public XmlRpcClientRequestImpl(XmlRpcRequestConfig pConfig,
								   String pMethodName, List pParams) {
		this(pConfig, pMethodName, pParams == null ? null : pParams.toArray());
	}
	// end

	public String getMethodName() { return methodName; }

	public int getParameterCount() { return params.length; }

	public Object getParameter(int pIndex) { return params[pIndex]; }

	public XmlRpcRequestConfig getConfig() { return config; }

	public String getJobID() { return jobID; }

	public String toString() {
		String s = "";

		s += getMethodName() + "(";
		for (int i = 0; i < getParameterCount() - 1; i++) {
			s += getParameter(i) + ", ";
		}
		s += getParameter(getParameterCount()-1) + ")";

		return s;
	}
}
