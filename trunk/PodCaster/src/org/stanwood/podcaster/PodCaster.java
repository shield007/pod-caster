package org.stanwood.podcaster;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stanwood.podcaster.audio.AudioFileConverter;
import org.stanwood.podcaster.audio.Format;
import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.audio.WavFile;
import org.stanwood.podcaster.cliutils.MPlayer;
import org.stanwood.podcaster.launcher.AbstractLauncher;
import org.stanwood.podcaster.launcher.DefaultExitHandler;
import org.stanwood.podcaster.launcher.IExitHandler;
import org.stanwood.podcaster.rss.RSSFeed;
import org.stanwood.podcaster.util.FileHelper;

/**
 * This class provides a tool for capturing audio streams and storing them as podcasts.
 * See the {@link PodCaster.main(String[])} method for more details.
 */
public class PodCaster extends AbstractLauncher{

	/* package for test */ static IExitHandler exitHandler = null;
	private final static Log log = LogFactory.getLog(PodCaster.class);

	private final static DateFormat DF = new SimpleDateFormat("dd-MM-yyyy.HH-mm-ss");

	private static final List<Option> OPTIONS;
	private final static String TIME_OPTION = "t";
	private final static String FORMAT_OPTION = "f";
	private final static String URL_OPTION = "u";
	private static final String RSS_FILE_OPTION ="rf";
	private static final String RSS_URL_OPTION ="ru";
	private static final String FEED_ARTWORK_OPTION="fa";

	private final static String META_TITLE_OPTION = "i";
	private final static String META_ARTWORK_URL_OPTION = "a";
	private final static String META_COPYRIGHT_OPTION = "c";
	private final static String META_ARTIST_OPTION = "r";

	private final static String FEED_DESCRIPTION_OPTION = "fd";
	private final static String ENTRY_DESCRIPTION_OPTION = "ed";
	private final static String MAX_ENTRIES_OPTIONS = "m";

	private long time;
	private String radioURL;
	private Format format = Format.WAV;
	private String title;
	private URL metaArtworkURL = null;
	private String metaCopyright = null;
	private String metaArtist = null;
	private String entryDescription = null;
	private String feedDescription = null;
	private long maxEntries = 20;

	private File rssFile = null;
	private URL rssUrl;
	private URL feedArtworkURL;

	static {
		OPTIONS = new ArrayList<Option>();

		Option o = new Option(RSS_FILE_OPTION,"rssFile",true,"The location of the rss file");
		o.setArgName("file");
		o.setRequired(true);
		OPTIONS.add(o);

		o = new Option(RSS_URL_OPTION,"rssUrl",true,"The public URL to the rss file");
		o.setArgName("URL");
		o.setRequired(true);
		OPTIONS.add(o);

		o = new Option(TIME_OPTION,"time",true,"Capture time (msecs)");
		o.setArgName("msecs");
		o.setRequired(true);
		OPTIONS.add(o);

		o = new Option(URL_OPTION,"url",true,"Radio url");
		o.setArgName("url");
		o.setRequired(true);
		OPTIONS.add(o);

		o = new Option(META_TITLE_OPTION,"metaTitle",true,"The title of the radio show");
		o.setArgName("string");
		o.setRequired(true);
		OPTIONS.add(o);

		o = new Option(FORMAT_OPTION,"format",true,"Capture format (wav,mp3,mp4)");
		o.setArgName("format");
		o.setRequired(true);
		OPTIONS.add(o);

		o = new Option(META_ARTWORK_URL_OPTION,"metaArtworkUrl",true,"Set the artwork to the URL");
		o.setArgName("url");
		OPTIONS.add(o);

		o = new Option(META_COPYRIGHT_OPTION,"metaCopyright",true,"Set the copyright meta data");
		o.setArgName("string");
		OPTIONS.add(o);

		o = new Option(META_ARTIST_OPTION,"metaArtist",true,"Set the artist meta data");
		o.setArgName("string");
		OPTIONS.add(o);

		o = new Option(FEED_DESCRIPTION_OPTION,"feedDescription",true,"Description of the feed");
		o.setArgName("sting");
		OPTIONS.add(o);

		o = new Option(ENTRY_DESCRIPTION_OPTION,"entryDescription",true,"Description of the entry");
		o.setArgName("string");
		OPTIONS.add(o);

		o = new Option(MAX_ENTRIES_OPTIONS,"maxEntries",true,"Maximum entries allowed in the feed, defaults to 20");
		o.setArgName("number");
		OPTIONS.add(o);

		o = new Option(FEED_ARTWORK_OPTION,"feedArtwork",true,"The URL to the feeds artwork");
		o.setArgName("url");
		OPTIONS.add(o);
	}

	/**
	 * The main method used to capture a stream to a audio file.
	 *
	 * The following command line syntax is passed to this method:
	 * <pre>
	 * podcaster [-a &lt;url&gt;] [-c &lt;string&gt;] [-ed &lt;string&gt;] -f &lt;format&gt; [-fa
     * &lt;url&gt;] [-fd &lt;sting&gt;] [-h] -i &lt;string&gt; [-l &lt;arg&gt;] [-m &lt;number&gt;] [-r
	 * &lt;string&gt;] -rf &lt;file&gt; -ru &lt;URL&gt; -t &lt;msecs&gt; -u &lt;url&gt;
 	 * -a,--metaArtworkUrl &lt;url&gt;         Set the artwork to the URL
 	 * -c,--metaCopyright &lt;string&gt;       Set the copyright meta data
 	 * -ed,--entryDescription &lt;string&gt;   Description of the entry
 	 * -f,--format &lt;format&gt;              Capture format (wav,mp3,mp4)
 	 * -fa,--feedArtwork &lt;url&gt;           The URL to the feeds artwork
 	 * -fd,--feedDescription &lt;sting&gt;     Description of the feed
 	 * -h,--help                         Show the help
 	 * -i,--metaTitle &lt;string&gt;           The title of the radio show
 	 * -l,--log_config &lt;arg&gt;             The log config mode [&lt;INFO>|&lt;DEBUG>|&lt;log4j config file>]
 	 * -m,--maxEntries &lt;number&gt;          Maximum entries allowed in the feed,defaults to 20
 	 * -r,--metaArtist &lt;string&gt;          Set the artist meta data
 	 * -rf,--rssFile &lt;file&gt;              The location of the rss file
 	 * -ru,--rssUrl &lt;URL&gt;                The public URL to the rss file
 	 * -t,--time &lt;msecs&gt;                 Capture time (msecs)
 	 * -u,--url &lt;url&gt;                    Radio url
	 * </pre>
     */
	public static void main(String[] args) {
		if (exitHandler==null) {
			exitHandler = new DefaultExitHandler();
		}

		PodCaster ca = new PodCaster(exitHandler);
		ca.launch(args);
	}

	/**
	 * Call to create a instance of the class
	 * @param exitHandler The exit handler to use
	 */
	private PodCaster(IExitHandler exitHandler) {
		super("podcaster",OPTIONS,exitHandler);
	}

	/**
	 * Used to check the CLI options are valid
	 * @param cmd The CLI options
	 * @return true if valid, otherwise false.
	 */
	@Override
	protected boolean processOptions(CommandLine cmd) {
		try {
			time = parseLongOption(cmd.getOptionValue(TIME_OPTION));
		}
		catch (ParseException e) {
			log.error("Unable to parse time from '"+cmd.getOptionValue(TIME_OPTION)+"'");
			return false;
		}
		radioURL = cmd.getOptionValue(URL_OPTION);
		String sformat = cmd.getOptionValue(FORMAT_OPTION);
		if (sformat!=null) {
			for (Format f : Format.values()) {
				if (f.getName().toLowerCase().equals(sformat.toLowerCase())) {
					this.format = f;
				}
			}
		}

		rssFile = new File(cmd.getOptionValue(RSS_FILE_OPTION));
		String url = cmd.getOptionValue(RSS_URL_OPTION);
		try {
			rssUrl = new URL(url);
		}
		catch (MalformedURLException e) {
			log.error("Invalid rss url: " + url,e);
			return false;
		}

		title = cmd.getOptionValue(META_TITLE_OPTION);

		if (cmd.hasOption(META_ARTWORK_URL_OPTION)) {
			url = cmd.getOptionValue(META_ARTWORK_URL_OPTION);
			try {
				metaArtworkURL = new URL(url);
			} catch (MalformedURLException e) {
				log.error("Invalid artwork url: " + url,e);
				return false;
			}
		}
		if (cmd.hasOption(FEED_ARTWORK_OPTION)) {
			url = cmd.getOptionValue(FEED_ARTWORK_OPTION);
			try {
				feedArtworkURL = new URL(url);
			} catch (MalformedURLException e) {
				log.error("Invalid artwork url: " + url,e);
				return false;
			}
		}

		if (cmd.hasOption(META_COPYRIGHT_OPTION)) {
			metaCopyright = cmd.getOptionValue(META_COPYRIGHT_OPTION);
		}
		if (cmd.hasOption(META_ARTIST_OPTION)) {
			metaArtist = cmd.getOptionValue(META_ARTIST_OPTION);
		}

		if (cmd.hasOption(FEED_DESCRIPTION_OPTION)) {
			feedDescription = cmd.getOptionValue(FEED_DESCRIPTION_OPTION);
		}
		if (cmd.hasOption(ENTRY_DESCRIPTION_OPTION)) {
			entryDescription = cmd.getOptionValue(ENTRY_DESCRIPTION_OPTION);
		}
		if (cmd.hasOption(MAX_ENTRIES_OPTIONS)) {
			try {
				maxEntries = parseLongOption(cmd.getOptionValue(MAX_ENTRIES_OPTIONS));
			}
			catch (ParseException e) {
				log.error("max feed entries '"+cmd.getOptionValue(MAX_ENTRIES_OPTIONS)+"'");
				return false;
			}
		}

		return true;
	}

	/**
	 * This does the actual work of the tool.
	 * @return true if successful, otherwise false.
	 */
	@Override
	protected boolean run() {
		try {
			Date startDate = new Date();
			String entryTitle = title+" "+DF.format(startDate);
			URLFetcher urlFetcher = new URLFetcher(new URL(radioURL));
			urlFetcher.getMediaUrl();

			MPlayer mplayer = new MPlayer();
			File wavOutputFile = File.createTempFile("captured", ".wav");
			mplayer.captureLiveAudioStream(wavOutputFile, urlFetcher.getMediaUrl(), time);
			if (log.isDebugEnabled()) {
				log.debug("Captured " + wavOutputFile + " with size " +wavOutputFile.length());
			}
			log.info("Converting stream to " + format.getName());
			File outputFile = new File(rssFile.getParentFile(),entryTitle.replaceAll(" ","-")+format.getExtension());

			String baseUrl = rssUrl.toExternalForm();
			baseUrl = baseUrl.substring(0,baseUrl.lastIndexOf('/'));
			URL entryUrl = new URL(baseUrl+"/"+outputFile.getName());

			IAudioFile audio = AudioFileConverter.wav2Format(new WavFile(wavOutputFile), format,outputFile);
			if (format!=Format.WAV) {
				if (title!=null) {
					audio.setTitle(entryTitle);
				}
				if (metaArtworkURL!=null) {
					audio.setArtwork(metaArtworkURL);
				}
				if (metaCopyright!=null) {
					audio.setCopyright(metaCopyright);
				}
				if (metaArtist!=null) {
					audio.setArtist(metaArtist);
				}
				if (entryDescription!=null) {
					audio.setDescription(entryDescription);
				}
				audio.writeMetaData();
			}
			else {
				log.error("Meta data can't be set on "+Format.WAV.getName()+" format files");
				return false;
			}

			RSSFeed rss = new RSSFeed(rssFile);
			if (rssFile.exists()) {
				rss.parse();
			}
			else {
				rss.createNewFeed();
			}

			rss.setTitle(title);
			rss.setLink(rssUrl);
			rss.setDescription(feedDescription);

			if (feedArtworkURL!=null) {
				File feedArtwork = new File(rssFile.getParentFile(),title.replaceAll(" ","-")+
							                FileHelper.getExtension(feedArtworkURL.toExternalForm()));

				FileHelper.downloadToFile(feedArtworkURL,feedArtwork );
				URL artURL = new URL(baseUrl+"/"+feedArtwork.getName());
				rss.setArtwork(artURL);
			}

			rss.addEntry(entryTitle, entryUrl, startDate, entryDescription,metaArtist,audio);
			rss.setMaxEntries(maxEntries,rssFile.getParentFile());
			rss.write();
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			return false;
		}
		log.debug("Audio captured and rss Updated successfully");
		return true;
	}

}
