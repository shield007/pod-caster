package org.stanwood.podcaster.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.stanwood.podcaster.util.FileHelper;
import org.stanwood.podcaster.xml.XMLParser;
import org.stanwood.podcaster.xml.XMLParserException;
import org.stanwood.podcaster.xml.XMLParserNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * This is used to parse the XML configuration files. These are used to tell the 
 * application which stores and other settings
 */
public class ConfigReader extends XMLParser {	

	private static final String SCHEMA_NAME = "Podcaster-Config-1.1.xsd"; //$NON-NLS-1$
	
	/** The default location to store configuration */
	private static final File DEFAULT_CONFIG_DIR = new File(FileHelper.HOME_DIR,".podcaster"); //$NON-NLS-1$
	
	private InputStream is;
	private Map<String, String> globalVars = new HashMap<String, String>();
	private File configDir;

	private String ffmpegPath = "ffmpeg";
	private String mplayerPath = "mplayer";
	private String iplayerdlPath = "iplayer-dl";
	
	/**
	 * The constructor used to create a instance of the configuration reader
	 * @param is The configuration file input stream
	 */
	public ConfigReader(InputStream is) {
		this.is = is;
	}
	
	/**
	 * Used to parse the configuration
	 * @throws ConfigException Thrown if their is a problem parsing the configuration
	 */
	public void parse() throws ConfigException {
		try {
			Document doc = XMLParser.parse(is, SCHEMA_NAME);
			parseGlobal(doc);			
		} catch (XMLParserException e) {
			throw new ConfigException("Unable to parse configuration file",e); 
		}		
	}
	
	private void parseGlobal(Document configNode) throws XMLParserException {
		Element node = (Element) selectSingleNode(configNode, "/podcaster/global"); //$NON-NLS-1$
		if (node!=null) {
			try {
				String dir = parseString(getStringFromXML(node, "configDirectory/text()")); //$NON-NLS-1$
				if (dir.trim().length()>0) {
					configDir =FileHelper.resolveRelativePaths(new File(dir));
				}
			}
			catch (XMLParserNotFoundException e) {
				// Ignore
			}			
			try {
				String path = parseString(getStringFromXML(node, "mplayerPath/text()")); //$NON-NLS-1$
				if (path.trim().length()>0) {
					mplayerPath =path;
				}
			}
			catch (XMLParserNotFoundException e) {
				// Ignore
			}			
			try {
				String path = parseString(getStringFromXML(node, "ffmpegPath/text()")); //$NON-NLS-1$
				if (path.trim().length()>0) {
					ffmpegPath =path;
				}
			}
			catch (XMLParserNotFoundException e) {
				// Ignore
			}
			try {
				String path = parseString(getStringFromXML(node, "iplayerdlPath/text()")); //$NON-NLS-1$
				if (path.trim().length()>0) {
					iplayerdlPath =path;
				}
			}
			catch (XMLParserNotFoundException e) {
				// Ignore
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
	
	private String parseString(String input) {
		input = input.replaceAll("\\$HOME", FileHelper.HOME_DIR.getAbsolutePath()); //$NON-NLS-1$
		return input;
	}
	
	/**
	 * Get the location of the media directory
	 * @return The location of the media directory
	 * @throws ConfigException Thrown if their is a problem
	 */
	public File getConfigDir() throws ConfigException {
		if (configDir == null) {
			configDir = getDefaultConfigDir();
		}
		return configDir;
	}

	/**
	 * Used to get the default location of the media manager configuration directory
	 * @return the default location of the media manager configuration directory
	 * @throws ConfigException Thrown if their is a problem
	 */
	public static File getDefaultConfigDir() throws ConfigException {
		File dir = DEFAULT_CONFIG_DIR;
		if (!dir.exists()) {
			if (!dir.mkdirs() && !dir.exists()) {
				throw new ConfigException (MessageFormat.format("Unable to create configuration directory {0}",dir)); //$NON-NLS-1$
			}
		}
		return dir;
	}

	public String getFFMpegPath() {
		return ffmpegPath;
	}
	
	public String getMPlayerPath() {
		return mplayerPath;
	}
	
	public String getIPlayerDLPath() {
		return iplayerdlPath;
	}
	
	/**
	 * Used to the default configuration filename
	 * @return The default configuration filename
	 * @throws ConfigException Thrown if their are any problems
	 */
	public static File getDefaultConfigFile() throws ConfigException {
		File file = new File(ConfigReader.getDefaultConfigDir(),"mediamanager-conf.xml"); //$NON-NLS-1$
		if (!file.exists()) {
			file = new File(File.separator+"etc"+File.separator+"mediamanager-conf.xml"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!file.exists()) {
			file = new File(ConfigReader.getDefaultConfigDir(),"mediamanager-conf.xml"); //$NON-NLS-1$
			try {
				FileHelper.copy(ConfigReader.class.getResourceAsStream("defaultConfig.xml"), file); //$NON-NLS-1$
			} catch (IOException e) {
				throw new ConfigException(MessageFormat.format("Unable to create default configuration file {0}",file),e); //$NON-NLS-1$
			}
		}
		return file;
	}
}
