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

import org.joda.time.Duration;
import org.stanwood.podcaster.audio.Format;
import org.stanwood.podcaster.util.FileHelper;
import org.stanwood.podcaster.xml.XMLParser;
import org.stanwood.podcaster.xml.XMLParserException;
import org.stanwood.podcaster.xml.XMLParserNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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

	private String ffmpegPath = "ffmpeg"; //$NON-NLS-1$
	private String mplayerPath = "mplayer"; //$NON-NLS-1$
	private String getIPlayerPath = "get-iplayer"; //$NON-NLS-1$

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
			throw new ConfigException(Messages.getString("ConfigReader.UnableParseConfigFile"),e); //$NON-NLS-1$
		}
	}

	private void parsePodcasts(Document rootNode) throws XMLParserException, ConfigException {
		podcasts=new HashMap<String,AbstractPodcast>();
		Element podcasterNode = getElement(rootNode, "podcaster"); //$NON-NLS-1$
		if (podcasterNode!=null) {
			for (Element podcastNode : selectChildNodes(podcasterNode, "podcast")) { //$NON-NLS-1$
				AbstractPodcast genericPodcast = null;
				Element typeNode = (Element) selectSingleNode(podcastNode, "radioStream"); //$NON-NLS-1$
				if (typeNode!=null) {
					StreamPodcast podcast = new StreamPodcast(podcastNode.getAttribute("id")); //$NON-NLS-1$
					String sURL = typeNode.getAttribute("url"); //$NON-NLS-1$
					if (sURL==null || sURL.length()==0) {
						throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.NoStramURL"),podcast.getId())); //$NON-NLS-1$
					}
					try {
						podcast.setStreamURL(new URL(sURL));
					} catch (MalformedURLException e) {
						throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.UnableCreateURL"),sURL),e); //$NON-NLS-1$
					}
					String sTime = typeNode.getAttribute("captureTime"); //$NON-NLS-1$
					if (sTime==null || sTime.length()==0) {
						throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.NoCaptureTime"),podcast.getId())); //$NON-NLS-1$
					}
					try {
						podcast.setCaptureTime(new Duration(Long.parseLong(sTime)));
					}
					catch (NumberFormatException e) {
						throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.InvalidCaptureTime"),podcast.getId())); //$NON-NLS-1$
					}
					genericPodcast = podcast;
				}
				else {
					typeNode = (Element) selectSingleNode(podcastNode, "iplayer"); //$NON-NLS-1$
					if (typeNode!=null) {
						IPlayerPodcast podcast = new IPlayerPodcast(podcastNode.getAttribute("id")); //$NON-NLS-1$
						String episodeId = typeNode.getAttribute("episode"); //$NON-NLS-1$
						if (episodeId==null || episodeId.length()==0) {
							throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.NoEpiusodeId"),podcast.getId())); //$NON-NLS-1$
						}
						podcast.setEpisodeId(episodeId);
						String sTime = typeNode.getAttribute("captureTime"); //$NON-NLS-1$
						if (sTime==null || sTime.length()==0) {
							throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.NoCaptureTime"),podcast.getId())); //$NON-NLS-1$
						}
						try {
							podcast.setCaptureTime(new Duration(Long.parseLong(sTime)));
						}
						catch (NumberFormatException e) {
							throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.InvalidCaptureTime"),podcast.getId())); //$NON-NLS-1$
						}

						genericPodcast = podcast;
					}
				}

				if (genericPodcast==null) {
					throw new ConfigException(Messages.getString("ConfigReader.MissingPodcastNode")); //$NON-NLS-1$
				}

				genericPodcast.setEntryDescription(parseString(getStringFromXMLOrNull(podcastNode, "metadata/entry/description/text()"))); //$NON-NLS-1$
				genericPodcast.setFeedArtist(parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/artist/text()"))); //$NON-NLS-1$
				genericPodcast.setFeedCopyright(parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/copyright/text()"))); //$NON-NLS-1$
				genericPodcast.setFeedDescription(parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/description/text()"))); //$NON-NLS-1$
				genericPodcast.setFeedTitle(parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/title/text()"))); //$NON-NLS-1$
				String sURL = parseString(getStringFromXMLOrNull(podcastNode, "metadata/feed/image/@url")); //$NON-NLS-1$
				if (sURL!=null && sURL.length() >0) {
					try {
						genericPodcast.setFeedImageURL(new URL(sURL));
					} catch (MalformedURLException e) {
						throw new ConfigException(Messages.getString("ConfigReader.UnableCreateURL2"),e); //$NON-NLS-1$
					}
				}

				String file = podcastNode.getAttribute("rssFile"); //$NON-NLS-1$
				if (file==null || file.length()==0) {
					throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.NoRSSFile"),genericPodcast.getId())); //$NON-NLS-1$
				}
				genericPodcast.setRSSFile(new File(file));
				String url = podcastNode.getAttribute("rssUrl"); //$NON-NLS-1$
				if (url==null || url.length()==0) {
					throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.NoRSSURL"),genericPodcast.getId())); //$NON-NLS-1$
				}
				try {
					genericPodcast.setRSSURL(new URL(url));
				} catch (MalformedURLException e) {
					throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.UnableCreateURL1"),url),e); //$NON-NLS-1$
				}
				String sEntries = podcastNode.getAttribute("maxEntries"); //$NON-NLS-1$
				if (sEntries==null || sEntries.length()==0) {
					genericPodcast.setMaxEntries(20);
				}
				else {
					try {
						genericPodcast.setMaxEntries(Integer.parseInt(sEntries));
					}
					catch (NumberFormatException e) {
						throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.InvalidMaxEntries"),sEntries),e); //$NON-NLS-1$
					}
				}


				String sformat= podcastNode.getAttribute("format"); //$NON-NLS-1$
				if (sformat==null || sformat.length()==0) {
					throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.NoForamt"),genericPodcast.getId())); //$NON-NLS-1$
				}

				Format format = Format.fromName(sformat);
				if (format==null) {
					throw new ConfigException(MessageFormat.format(Messages.getString("ConfigReader.UnspportedFormat"), sformat,genericPodcast.getId())); //$NON-NLS-1$
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

	/**
	 * Used to get the path to FFMPEG command line application
	 * @return the path to FFMPEG command line application
	 */
	public String getFFMpegPath() {
		return ffmpegPath;
	}

	/**
	 * Used to get the path to mplayer command line application
	 * @return the path to mplayer command line application
	 */
	public String getMPlayerPath() {
		return mplayerPath;
	}

	/**
	 * Used to get the path to get_iplayer command line application
	 * @return the path to get_iplayer command line application
	 */
	public String getGetIPlayerPath() {
		return getIPlayerPath;
	}

	/**
	 * Used to the default configuration filename
	 * @return The default configuration filename
	 * @throws ConfigException Thrown if their are any problems
	 */
	public static File getDefaultConfigFile() throws ConfigException {
		File file = new File(ConfigReader.getDefaultConfigDir(),"podcaster-conf.xml"); //$NON-NLS-1$
		if (!file.exists()) {
			file = new File(File.separator+"etc"+File.separator+"podcaster-conf.xml"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!file.exists()) {
			file = new File(ConfigReader.getDefaultConfigDir(),"podcaster-conf.xml"); //$NON-NLS-1$
			try {
				FileHelper.copy(ConfigReader.class.getResourceAsStream("defaultConfig.xml"), file); //$NON-NLS-1$
			} catch (IOException e) {
				throw new ConfigException(MessageFormat.format("Unable to create default configuration file {0}",file),e); //$NON-NLS-1$
			}
		}
		return file;
	}

	/**
	 * Used to get all the podcast information in the configuration
	 * @return all the podcast information in the configuration
	 */
	public Collection<AbstractPodcast> getPodcasts() {
		return podcasts.values();
	}

	/**
	 * Used to lookup podcast information
	 * @param id The ID of the podcast to lookup
	 * @return The podcast information, or null if not found
	 */
	public AbstractPodcast getPodcast(String id) {
		return podcasts.get(id);
	}
}
