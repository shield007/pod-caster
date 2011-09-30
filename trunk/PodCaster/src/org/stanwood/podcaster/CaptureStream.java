package org.stanwood.podcaster;

import java.io.File;
import java.io.PrintStream;
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
import org.stanwood.podcaster.capture.ICaptureStream;
import org.stanwood.podcaster.capture.StreamCaptureFactory;
import org.stanwood.podcaster.cli.AbstractLauncher;
import org.stanwood.podcaster.cli.DefaultExitHandler;
import org.stanwood.podcaster.cli.IExitHandler;
import org.stanwood.podcaster.cliutils.IPlayerDownloader;
import org.stanwood.podcaster.cliutils.MPlayer;
import org.stanwood.podcaster.config.AbstractPodcast;

/**
 * This class provides a entry point for capturing audio and storing meta data about.
 * See the {@link CaptureStream.main(String[])} method for more details. 
 */
public class CaptureStream extends AbstractLauncher {

	private final DateFormat DF = new SimpleDateFormat("dd-MM-yyyy.HH-mm-ss");
	
	/* package for test */ static IExitHandler exitHandler = null;
	private final static Log log = LogFactory.getLog(CaptureStream.class);

	private static PrintStream stdout = System.out;
	private static PrintStream stderr = System.err;
	
	private static final List<Option> OPTIONS;
	
	private final static String OUTPUT_FILE_OPTION = "o";
	private final static String PODCAST_ID_OPTION = "p";
	
	private File outputFile = null;
	private AbstractPodcast podcast;

	static {
		OPTIONS = new ArrayList<Option>();

		Option o = new Option(PODCAST_ID_OPTION,"podcast",true,"The ID of the podcast from the configuration");
		o.setArgName("id");
		o.setRequired(true);
		OPTIONS.add(o);
		
		o = new Option(OUTPUT_FILE_OPTION,"output",true,"Audio Output file");
		o.setArgName("file");
		o.setRequired(true);
		OPTIONS.add(o);		
	}

	/**
	 * The main method used to capture a stream to a audio file. 
	 * 
	 * The following command line syntax is passed to this method:
	 * <pre> 
	 * stream-capture [-a &lt;url&gt;] [-c &lt;copyright&gt;] [-e &lt;description&gt;] [-f
     * &lt;format&gt;] [-h] [-i &lt;title&gt;] [-l &lt;arg&gt;] -o &lt;wavFile&gt; [-r &lt;artist&gt;] -t
     * &lt;msecs&gt; -u &lt;url&gt;
     * -a,--metaArtworkUrl &lt;url&gt;            Set the artwork to the URL
     * -c,--metaCopyright &lt;copyright&gt;       Set the copyright meta data
     * -e,--metaDescription &lt;description&gt;   Set the description meta data
     * -f,--format &lt;format&gt;                 Capture format (wav,mp3,mp4)
     * -h,--help                            Show the help
     * -i,--metaTitle &lt;title&gt;               Set the title meta data
     * -l,--log_config &lt;arg&gt;             The log config mode [&lt;INFO&gt;|&lt;DEBUG&gt;|&lt;log4j config file&gt;]
     * -o,--output &lt;wavFile&gt;                Output file
     * -r,--metaArtist &lt;artist&gt;             Set the artist meta data
     * -t,--time &lt;msecs&gt;                    Capture time (msecs)
     * -u,--url &lt;url&gt;                       Radio url
	 * </pre>
	 */	
	public static void main(String[] args) {
		if (exitHandler==null) {
			exitHandler = new DefaultExitHandler();
		}

		CaptureStream ca = new CaptureStream(exitHandler);
		ca.launch(args);
	}

	/**
	 * Call to create a instance of the class
	 * @param exitHandler The exit handler to use
	 */
	private CaptureStream(IExitHandler exitHandler) {
		super("stream-capture",OPTIONS,exitHandler,stdout,stderr);
	}

	/**
	 * This does the actual work of the tool.
	 * @return true if successful, otherwise false.
	 */
	@Override
	protected boolean run() {
		try {						
			Date startDate = new Date();
			String entryTitle = podcast.getFeedTitle()+" "+DF.format(startDate);
			
			ICaptureStream streamCapture = StreamCaptureFactory.getStreamCapture(podcast);					
			IAudioFile audioFile = streamCapture.captureLiveAudioStream(getConfig(),podcast);
			if (log.isDebugEnabled()) {
				log.debug("Captured " + audioFile.getFile() + " with size " +audioFile.getFile().length());
			}
			
			log.info("Converting stream to " + podcast.getFormat().getName());
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
				log.error("Meta data can't be set on "+Format.WAV.getName()+" format files");
				return false;
			}			
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			return false;
		}
		log.debug("Audio captured successfully");
		return true;
	}

	/**
	 * Used to check the CLI options are valid
	 * @param cmd The CLI options
	 * @return true if valid, otherwise false.
	 */
	@Override
	protected boolean processOptions(String[] args, CommandLine cmd) {
					
		outputFile = new File(cmd.getOptionValue(OUTPUT_FILE_OPTION));
		if (outputFile.exists()) {
			log.error("Output file " + outputFile.getAbsolutePath()+" already exsits");
			return false;
		}
		
		String id = cmd.getOptionValue(PODCAST_ID_OPTION);
		if (id==null) {
			log.error("No podcast ID given");
			return false;
		}
		podcast = getConfig().getPodcast(id);

		return true;
	}



}
