package org.stanwood.podcaster.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * This is used to parse the XML configuration files. These are used to tell the 
 * application which stores and other settings
 */
public class ConfigReader {

	private File file;
	private Map<String, String> globalVars = new HashMap<String, String>();
	
	/**
	 * The constructor used to create a instance of the configuration reader
	 * @param file The configuration file
	 */
	public ConfigReader(File file) {
		this.file = file;
	}
	
	/**
	 * Used to parse the configuration
	 * @throws ConfigException Thrown if their is a problem parsing the configuration
	 */
	public void parse() throws ConfigException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);		 
		if (file.exists()) {
			try {
				Document doc = factory.newDocumentBuilder().parse(file);

				NodeList varNodes = XPathAPI.selectNodeList(doc, "/config/global/var");
				if (varNodes!=null) {
					for (int i=0;i<varNodes.getLength();i++) {
						Element el = (Element) varNodes.item(i);
						if (el.getAttribute("name")==null || el.getAttribute("value")==null) {
							throw new ConfigException("global var entry has missing attributes");
						}
						globalVars.put(el.getAttribute("name"),el.getAttribute("value"));
					}					
				}
			} catch (SAXException e) {
				throw new ConfigException("Unable to parse config file: " + e.getMessage(),e);
			} catch (IOException e) {
				throw new ConfigException("Unable to parse config file: " + e.getMessage(),e);
			} catch (ParserConfigurationException e) {
				throw new ConfigException("Unable to parse config file: " + e.getMessage(),e);
			} catch (TransformerException e) {
				throw new ConfigException("Unable to parse config file: " + e.getMessage(),e);
			}
		}
	}

	/**
	 * Used to get the global variables found in the configuration
	 * @return The global variables
	 */
	public Map<String,String> getGlobalVars() {
		return globalVars;
	}
}
