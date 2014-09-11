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
package edu.ucsd.xmlrpc.xmlrpc.common;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;
import edu.ucsd.xmlrpc.xmlrpc.webserver.WebServer;


/** An instance of {@link XmlRpcRequestProcessor},
 * which is processing an XML stream.
 */
public interface XmlRpcStreamRequestProcessor extends XmlRpcRequestProcessor {
	/** Reads an XML-RPC request from the connection
	 * object and processes the request, writing the
	 * result to the same connection object.
	 * @return 
	 * @throws XmlRpcException Processing the request failed.
	 */
	void execute(XmlRpcStreamRequestConfig pConfig, ServerStreamConnection pConnection, WebServer wServer) throws XmlRpcException;
}
