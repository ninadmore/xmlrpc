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
package edu.ucsd.xmlrpc.xmlrpc.util;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.ucsd.xmlrpc.xmlrpc.XmlRpcException;


/** Utility class for working with SAX parsers.
 */
public class SAXParsers {
	private static final SAXParserFactory spf;
	static {
		spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		spf.setValidating(false);
	}

	/** Creates a new instance of {@link XMLReader}.
	 */
	public static XMLReader newXMLReader() throws XmlRpcException {
		try {
			return spf.newSAXParser().getXMLReader();
		} catch (ParserConfigurationException e) {
			throw new XmlRpcException("Unable to create XML parser: " + e.getMessage(), e);
		} catch (SAXException e) {
			throw new XmlRpcException("Unable to create XML parser: " + e.getMessage(), e);
		}
	}
}
