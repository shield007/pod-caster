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
 * This class is a wrapper around the mplayer application and is used to drive
 * mplayer in a more java friendly way
 */
public class GetIPlayer extends AbstractExecutable {

	private final static Log log = LogFactory.getLog(GetIPlayer.class);

	public GetIPlayer(ConfigReader config) {
		super(config);
	}

	/**
	 * This will capture a audio stream from using mplayer for the given amount of time
	 * @param wavOutputFile The WAV file to create from the audio stream
	 * @param stream The stream to capture
	 * @param time The time in milliseconds to capture the stream
	 * @throws MPlayerException Thrown if their is a problem with mplayer
	 */
	public Process captureLiveAudioStream(String epsiode) throws  CaptureException
	{
		File wavOutputFile;
		try {
			wavOutputFile = File.createTempFile("captured", ".wav");
		} catch (IOException e) {
			throw new MPlayerException("Unable to create temp file",e);
		}

		log.info(MessageFormat.format("Recording live radio from BBC IPlayer: {0} to {1}",epsiode,wavOutputFile.getAbsolutePath()));
		List<String> args = new ArrayList<String>();
		args.add(getConfig().getGetIPlayerPath());
		args.add("--stream");
		args.add(epsiode);
		args.add("--type=liveradio");

		Process p;
		try {
			p = createProcess(args);
		} catch (IOException e) {
			throw new CaptureException("Unable to execute get-iplayer",e);
		}
		return p;
	}

}
