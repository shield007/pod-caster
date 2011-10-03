package org.stanwood.podcaster.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.stanwood.podcaster.audio.Format;
import org.stanwood.podcaster.util.FileHelper;
import org.stanwood.podcaster.xml.XMLParser;
import org.stanwood.podcaster.xml.XMLParserException;
import org.stanwood.podcaster.xml.XMLParserNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
	private String getIPlayerPath = "get-iplayer";

	private Map<String, AbstractPodcast> podcasts;
	
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
			parsePodcasts(doc);
		} catch (XMLParserException e) {
			throw new ConfigException("Unable to parse configuration file",e); 
		}		
	}
	
	private void parsePodcasts(Document rootNode) throws XMLParserException, ConfigException {
		podcasts=new HashMap<String,AbstractPodcast>();
		Element podcasterNode = getElement(rootNode, "podcaster");
		if (podcasterNode!=null) {
			for (Element podcastNode : selectChildNodes(podcasterNode, "podcast")) {
				AbstractPodcast genericPodcast = null;
				Element typeNode = (Element) selectSingleNode(podcastNode, "radioStream");
				if (typeNode!=null) {
					StreamPodcast podcast = new StreamPodcast(podcastNode.getAttribute("id"));
					String sURL = typeNode.getAttribute("url");
					if (sURL==null || sURL.length()==0) {
						throw new ConfigException(MessageFormat.format("No stream URL for for podcast ''{0}''",podcast.getId()));
					}
					try { 
						podcast.setStreamURL(new URL(sURL));
					} catch (MalformedURLException e) {
						throw new ConfigException(MessageFormat.format("Unable to create URL ''{0}''",sURL),e);
					}
					String sTime = typeNode.getAttribute("captureTime");
					if (sTime==null || sTime.length()==0) {
						throw new ConfigException(MessageFormat.format("No capture time for for podcast ''{0}''",podcast.getId()));
					}
					try {
						podcast.setCaptureTime(Long.parseLong(sTime));
					}
					catch (NumberFormatException e) {
						throw new ConfigException(MessageFormat.format("Capture time ''{0}'' is invalid.",podcast.getId()));
					}
					genericPodcast = podcast;
				}
				else {
					typeNode = (Element) selectSingleNode(podcastNode, "iplayer");				
					if (typeNode!=null) {
						IPlayerPodcast podcast = new IPlayerPodcast(podcastNode.getAttribute("id"));
						String episodeId = typeNode.getAttribute("episode");
						if (episodeId==null || episodeId.length()==0) {
							throw new ConfigException(MessageFormat.format("No episode id given for iplayer podcast ''{0}''",podcast.getId()));
						}
						podcast.setEpisodeId(episodeId);
						String sTime = typeNode.getAttribute("captureTime");
						if (sTime==null || sTime.length()==0) {
							throw new ConfigException(MessageFormat.format("No capture time for for podcast ''{0}''",podcast.getId()));
						}
						try {
							podcast.setCaptureTime(Long.parseLong(sTime));
						}
						catch (NumberFormatException e) {
							throw new ConfigException(MessageFormat.format("Capture time ''{0}'' is invalid.",podcast.getId()));
						}
						
						genericPodcast = podcast;
					}
				}
				
				if (genericPodcast==null) {
					throw new ConfigException("Missing podcast node of name <iplayer> or <radioStream>");
				}	
				
				genericPodcast.setEntryDescription(parseString(getStringFromXMLOrNull(podcastNode, "metadata/entry/description/text()")));
				genericPodcast.setFeedArtist(parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/artist/text()")));
				genericPodcast.setFeedCopyright(parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/copyright/text()")));
				genericPodcast.setFeedDescription(parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/description/text()")));
				genericPodcast.setFeedTitle(parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/title/text()")));
				String sURL = parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/image/@url"));
				if (sURL!=null && sURL.length() >0) {
					try {
						genericPodcast.setFeedImageURL(new URL(sURL));
					} catch (MalformedURLException e) {
						throw new ConfigException("Unable to create URL",e);
					}
				}
				
				String file = podcastNode.getAttribute("rssFile");
				if (file==null || file.length()==0) {
					throw new ConfigException(MessageFormat.format("No RSS file given for podcast ''{0}''",genericPodcast.getId()));
				}
				genericPodcast.setRSSFile(new File(file));				
				String url = podcastNode.getAttribute("rssUrl");
				if (url==null || url.length()==0) {
					throw new ConfigException(MessageFormat.format("No RSS url given for podcast ''{0}''",genericPodcast.getId()));
				}
				try {
					genericPodcast.setRSSURL(new URL(url));
				} catch (MalformedURLException e) {
					throw new ConfigException(MessageFormat.format("Unable to create URL ''{0}''",url),e);
				}				
				String sEntries = podcastNode.getAttribute("maxEntries");
				if (sEntries==null || sEntries.length()==0) {
					genericPodcast.setMaxEntries(20);
				}
				else {
					try {
						genericPodcast.setMaxEntries(Integer.parseInt(sEntries));
					}
					catch (NumberFormatException e) {
						throw new ConfigException(MessageFormat.format("Inavid max entries ''{0}'', must be a number",sEntries),e);
					}
				}
					
				
				String sformat= podcastNode.getAttribute("format");
				if (sformat==null || sformat.length()==0) {
					throw new ConfigException(MessageFormat.format("No format given for podcast ''{0}''",genericPodcast.getId()));	
				}
				
				Format format = Format.fromName(sformat);
				if (format==null) {
					throw new ConfigException(MessageFormat.format("Unsupported format ''{0}'' for podcast ''{1}''", sformat,genericPodcast.getId()));
				}
				genericPodcast.setFormat(format);
								
				podcasts.put(genericPodcast.getId(),genericPodcast);
			}
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
				String path = parseString(getStringFromXML(node, "mplayer-path/text()")); //$NON-NLS-1$
				if (path.trim().length()>0) {
					mplayerPath =path;
				}
			}
			catch (XMLParserNotFoundException e) {
				// Ignore
			}			
			try {
				String path = parseString(getStringFromXML(node, "ffmpeg-path/text()")); //$NON-NLS-1$
				if (path.trim().length()>0) {
					ffmpegPath =path;
				}
			}
			catch (XMLParserNotFoundException e) {
				// Ignore
			}
			try {
				String path = parseString(getStringFromXML(node, "get-iplayer-path/text()")); //$NON-NLS-1$
				if (path.trim().length()>0) {
					getIPlayerPath =path;
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
		if (input==null) {
			return null;
		}
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
	
	public String getGetIPlayerPath() {
		return getIPlayerPath;
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
	
	public Collection<AbstractPodcast> getPodcasts() {
		return podcasts.values();
	}
	
	public AbstractPodcast getPodcast(String id) {
		return podcasts.get(id);
	}	
}
