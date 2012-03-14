package org.stanwood.podcaster.cliutils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stanwood.podcaster.capture.CaptureException;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.util.AbstractExecutable;

/**
 * This class is used to wrap around the get_iplayer application so it can be used from
 * java to capture audio from iplayer.
 */
public class GetIPlayer extends AbstractExecutable {

	private final static Log log = LogFactory.getLog(GetIPlayer.class);

	/**
	 * The constructor
	 * @param config The application configuration
	 */
	public GetIPlayer(ConfigReader config) {
		super(config);
	}

	/**
	 * Used to create the process that will be used to capture audio from iplayer.
	 * @param epsiode The epsiode id to capture
	 * @return The process
	 * @throws CaptureException Thrown if their is a problem
	 */
	public Process captureLiveAudioStream(String epsiode) throws  CaptureException
	{
		File wavOutputFile;
		try {
			wavOutputFile = File.createTempFile("captured", ".wav");  //$NON-NLS-1$//$NON-NLS-2$
		} catch (IOException e) {
			throw new MPlayerException("Unable to create temp file",e);
		}

		log.info(MessageFormat.format("Recording live radio from BBC IPlayer: {0} to {1}",epsiode,wavOutputFile.getAbsolutePath()));
		List<String> args = new ArrayList<String>();
		args.add(getConfig().getGetIPlayerPath());
		args.add("--stream"); //$NON-NLS-1$
		args.add(epsiode);
		args.add("--type=liveradio"); //$NON-NLS-1$

		Process p;
		try {
			p = createProcess(args);
		} catch (IOException e) {
			throw new CaptureException("Unable to execute get-iplayer",e);
		}
		return p;
	}

}
