package org.stanwood.podcaster;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import org.stanwood.podcaster.cliutils.ICaptureStream;
import org.stanwood.podcaster.cliutils.MPlayer;
import org.stanwood.podcaster.launcher.AbstractLauncher;
import org.stanwood.podcaster.launcher.DefaultExitHandler;
import org.stanwood.podcaster.launcher.IExitHandler;

/**
 * This class provides a entry point for capturing audio and storing meta data about.
 * See the {@link CaptureStream.main(String[])} method for more details. 
 */
public class CaptureStream extends AbstractLauncher{

	/* package for test */ static IExitHandler exitHandler = null;
	private final static Log log = LogFactory.getLog(CaptureStream.class);

	private static final List<Option> OPTIONS;
	
	private final static String OUTPUT_FILE_OPTION = "o";
	private final static String TIME_OPTION = "t";
	private final static String FORMAT_OPTION = "f";
	private final static String URL_OPTION = "u";
	private final static String TYPE_OPTION = "y";

	private final static String META_TITLE_OPTION = "i";
	private final static String META_ARTWORK_URL_OPTION = "a";
	private final static String META_COPYRIGHT_OPTION = "c";
	private final static String META_ARTIST_OPTION = "r";
	private final static String META_DESCRIPTION_OPTION = "e";

	private long time;	
	private String url;
	private Format format = Format.WAV;
	private File outputFile = null; 
	private String metaTitle = null;	
	private URL metaArtworkURL = null;
	private String metaCopyright = null;
	private String metaArtist = null;
	private String metaDescription = null;
	private Type type = null;	

	static {
		OPTIONS = new ArrayList<Option>();

		Option o = new Option(FORMAT_OPTION,"format",true,"Capture format (wav,mp3,mp4)");
		o.setArgName("format");
		OPTIONS.add(o);		
		o = new Option(META_TITLE_OPTION,"metaTitle",true,"Set the title meta data");
		o.setArgName("title");
		OPTIONS.add(o);
		o = new Option(META_ARTWORK_URL_OPTION,"metaArtworkUrl",true,"Set the artwork to the URL");
		o.setArgName("url");
		OPTIONS.add(o);
		o = new Option(META_COPYRIGHT_OPTION,"metaCopyright",true,"Set the copyright meta data");
		o.setArgName("copyright");
		OPTIONS.add(o);
		o = new Option(META_ARTIST_OPTION,"metaArtist",true,"Set the artist meta data");
		o.setArgName("artist");
		OPTIONS.add(o);
		o = new Option(META_DESCRIPTION_OPTION,"metaDescription",true,"Set the description meta data");
		o.setArgName("description");
		OPTIONS.add(o);
		
		o = new Option(URL_OPTION,"url",true,"Radio url");
		o.setArgName("url");
		o.setRequired(true);		
		OPTIONS.add(o);
		
		o = new Option(OUTPUT_FILE_OPTION,"output",true,"Audio Output file");
		o.setArgName("file");
		o.setRequired(true);
		OPTIONS.add(o);
		
		o = new Option(TIME_OPTION,"time",true,"Capture time (msecs)");
		o.setArgName("msecs");
		o.setRequired(true);
		OPTIONS.add(o);
		
		o = new Option(TYPE_OPTION,"type",true,"Type of stream downloader (stream | iplayer_dl)");
		o.setArgName("type");
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
		super("stream-capture",OPTIONS,exitHandler);
	}

	/**
	 * This does the actual work of the tool.
	 * @return true if successful, otherwise false.
	 */
	@Override
	protected boolean run() {
		try {
			URLFetcher urlFetcher = new URLFetcher(new URL(url));
			urlFetcher.getMediaUrl();
			
			ICaptureStream streamCapture;
			switch (type) {
				case STREAM: streamCapture = new MPlayer();
							 break;
				case IPLAYER_DL: streamCapture = new IPlayerDownloader();
				 			 break;
				default:
					log.error("Unkown type");
					return false;
			}
			IAudioFile audioFile = streamCapture.captureLiveAudioStream(urlFetcher.getMediaUrl(), time);
			if (log.isDebugEnabled()) {
				log.debug("Captured " + audioFile.getFile() + " with size " +audioFile.getFile().length());
			}
			
			log.info("Converting stream to " + format.getName());
			IAudioFile audio = AudioFileConverter.convertAudio(audioFile, format,outputFile);
			if (format!=Format.WAV) {
				if (metaTitle!=null) {
					audio.setTitle(metaTitle);
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
				if (metaDescription!=null) {
					audio.setDescription(metaDescription);
				}
				audio.writeMetaData();
			}
			else {
				log.error("Meta data can't be set on "+Format.WAV.getName()+" format files");
				return false;
			}
			// Encode meta data
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
	protected boolean processOptions(CommandLine cmd) {				
		try {
			time = parseLongOption(cmd.getOptionValue(TIME_OPTION));
		}
		catch (ParseException e) {
			log.error("Unable to parse time from '"+cmd.getOptionValue(TIME_OPTION)+"'");
			return false;
		}
		outputFile = new File(cmd.getOptionValue(OUTPUT_FILE_OPTION));
		if (outputFile.exists()) {
			log.error("Output file " + outputFile.getAbsolutePath()+" already exsits");
			return false;
		}
		if (cmd.getOptionValue(TYPE_OPTION).toLowerCase().equals("stream")) {
			type = Type.STREAM;
		}
		if (cmd.getOptionValue(TYPE_OPTION).toLowerCase().equals("iplayer_dl")) {
			type = Type.IPLAYER_DL;
		}
		else {
			log.error("Unknown type, possible values are: stream, iplayer_dl");
			return false;
		}
		
		url = cmd.getOptionValue(URL_OPTION);
		String sformat = cmd.getOptionValue(FORMAT_OPTION);
		if (sformat!=null) {
			for (Format f : Format.values()) {
				if (f.getName().toLowerCase().equals(sformat.toLowerCase())) {
					this.format = f;
				}
			}
		}

		if (cmd.hasOption(META_TITLE_OPTION)) {
			metaTitle = cmd.getOptionValue(META_TITLE_OPTION);
		}
		if (cmd.hasOption(META_ARTWORK_URL_OPTION)) {
			try {
				String url = cmd.getOptionValue(META_ARTWORK_URL_OPTION);
				metaArtworkURL = new URL(url);
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
		if (cmd.hasOption(META_DESCRIPTION_OPTION)) {
			metaDescription = cmd.getOptionValue(META_DESCRIPTION_OPTION);
		}

		return true;
	}



}
