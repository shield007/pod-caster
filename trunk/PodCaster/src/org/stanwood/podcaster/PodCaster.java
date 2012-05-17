package org.stanwood.podcaster;

import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.stanwood.podcaster.audio.AudioFileConverter;
import org.stanwood.podcaster.audio.Format;
import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.capture.ICaptureStream;
import org.stanwood.podcaster.capture.StreamCaptureFactory;
import org.stanwood.podcaster.cli.AbstractLauncher;
import org.stanwood.podcaster.cli.DefaultExitHandler;
import org.stanwood.podcaster.cli.IExitHandler;
import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.rss.RSSFeed;
import org.stanwood.podcaster.util.FileHelper;

/**
 * This class provides a tool for capturing audio streams and storing them as podcasts.
 * See the {@link #main(String[])} method for more details.
 */
public class PodCaster extends AbstractLauncher{

	/* package for test */ static IExitHandler exitHandler = null;
	private final static Log log = LogFactory.getLog(PodCaster.class);

	private static PrintStream stdout = System.out;
	private static PrintStream stderr = System.err;

	private String id;

	private static final List<Option> OPTIONS;
	private final static String PODCAST_ID_OPTION = "p"; //$NON-NLS-1$

	static {
		OPTIONS = new ArrayList<Option>();

		Option o = new Option(PODCAST_ID_OPTION,"podcast",true,Messages.getString("PodCaster.0")); //$NON-NLS-1$ //$NON-NLS-2$
		o.setArgName("id"); //$NON-NLS-1$
		o.setRequired(true);
		OPTIONS.add(o);
	}

	/**
	 * The main method used to podcast a new episode as specified in the configuration file
	 *
	 * The following command line syntax is passed to this method:
	 * <pre>
	 * usage: podcaster [-c &lt;file&gt] [-h] [-l &lt;info|debug|file&gt] -p &lt;id&gt [-v]
	 *
	 *  --version, -v                 Display the version
	 *  --config_file, -c &lt;file&gt;      The location of the config file. If not present, attempts to load it from /etc/mediafetcher-conf.xml
	 *  --podcast, -p &lt;id&gt;            The ID of the podcast from the configuration
	 *  --log_config, -l &lt;info|debug|file&gt;
	 *                                The log config mode [INFO|DEBUG|log4j config file]
  	 *  --help, -h                    Show the help
	 * </pre>
	 * @param args The arguments
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
		super("podcaster",OPTIONS,exitHandler,stdout,stderr); //$NON-NLS-1$
	}

	/**
	 * Used to check the CLI options are valid
	 * @param cmd The CLI options
	 * @return true if valid, otherwise false.
	 */
	@Override
	protected boolean processOptions(String[] args, CommandLine cmd) {
		String id = cmd.getOptionValue(PODCAST_ID_OPTION);
		if (id==null) {
			log.error(Messages.getString("PodCaster.NoIDGiven")); //$NON-NLS-1$
			return false;
		}
		this.id = id;

		return true;
	}

	/**
	 * This does the actual work of the tool.
	 * @return true if successful, otherwise false.
	 */
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("REC_CATCH_EXCEPTION")
	@Override
	protected boolean run() {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy.HH-mm-ss"); //$NON-NLS-1$
		AbstractPodcast podcast = getConfig().getPodcast(id);
		if (podcast==null) {
			log.error(MessageFormat.format(Messages.getString("PodCaster.UnableFindPodcast"), id)); //$NON-NLS-1$
			return false;
		}
		try {
			DateTime startDate = new DateTime();
			String entryTitle = podcast.getFeedTitle()+" "+startDate.toString(fmt); //$NON-NLS-1$

			ICaptureStream streamCapture = StreamCaptureFactory.getStreamCapture(podcast);
			IAudioFile audioFile = streamCapture.captureLiveAudioStream(getConfig(),podcast);
			if (log.isDebugEnabled()) {
				log.debug("Captured " + audioFile.getFile() + " with size " +audioFile.getFile().length()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			log.info(MessageFormat.format(Messages.getString("PodCaster.ConvertingStream"),podcast.getFormat().getName())); //$NON-NLS-1$
			File outputFile = new File(podcast.getRSSFile().getParentFile(),entryTitle.replaceAll(" ","-")+podcast.getFormat().getExtension()); //$NON-NLS-1$ //$NON-NLS-2$

			String baseUrl = podcast.getRSSURL().toExternalForm();
			baseUrl = baseUrl.substring(0,baseUrl.lastIndexOf('/'));
			URL entryUrl = new URL(baseUrl+"/"+outputFile.getName()); //$NON-NLS-1$

			IAudioFile audio = AudioFileConverter.convertAudio(getConfig(),audioFile, podcast.getFormat(),outputFile);
			if (podcast.getFormat()!=Format.WAV) {
				if (entryTitle!=null) {
					audio.setTitle(entryTitle);
				}
				if (podcast.getFeedImageURL()!=null) {
					audio.setArtwork(podcast.getFeedImageURL());
				}
				if (podcast.getFeedCopyright()!=null) {
					audio.setCopyright(podcast.getFeedCopyright());
				}
				if (podcast.getFeedArtist()!=null) {
					audio.setArtist(podcast.getFeedArtist());
				}
				if (podcast.getEntryDescription()!=null) {
					audio.setDescription(podcast.getEntryDescription());
				}
				audio.writeMetaData();
			}
			else {
				log.error(MessageFormat.format(Messages.getString("PodCaster.MetadataCantBeSet"),Format.WAV.getName())); //$NON-NLS-1$
				return false;
			}

			RSSFeed rss = new RSSFeed(podcast.getRSSFile());
			if (podcast.getRSSFile().exists()) {
				rss.parse();
			}
			else {
				rss.createNewFeed();
			}

			rss.setTitle(podcast.getFeedTitle());
			rss.setLink(podcast.getRSSURL());
			rss.setDescription(podcast.getFeedDescription());

			if (podcast.getFeedImageURL()!=null) {
				File feedArtwork = new File(podcast.getRSSFile().getParentFile(),podcast.getFeedTitle().replaceAll(" ","-")+ //$NON-NLS-1$ //$NON-NLS-2$
							                FileHelper.getExtension(podcast.getFeedImageURL().toExternalForm()));

				FileHelper.downloadToFile(podcast.getFeedImageURL(),feedArtwork );
				URL artURL = new URL(baseUrl+"/"+feedArtwork.getName()); //$NON-NLS-1$
				rss.setArtwork(artURL);
			}

			rss.addEntry(entryTitle, entryUrl, startDate, podcast.getEntryDescription(),podcast.getFeedArtist(),audio);
			rss.setMaxEntries(podcast.getMaxEntries(),podcast.getRSSFile().getParentFile());
			rss.write();
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			return false;
		}
		log.debug(Messages.getString("PodCaster.Success")); //$NON-NLS-1$
		return true;
	}


}
