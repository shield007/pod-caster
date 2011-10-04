package org.stanwood.podcaster.cliutils;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stanwood.podcaster.StreamReference;
import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.audio.WavFile;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.util.AbstractExecutable;

/**
 * This class is a wrapper around the mplayer application and is used to drive
 * mplayer in a more java friendly way
 */
public class MPlayer extends AbstractExecutable {

	private final static Log log = LogFactory.getLog(MPlayer.class);

	private final static int CACHE_SIZE = 500;

	public MPlayer(ConfigReader config) {
		super(config);
	}

	/**
	 * This will capture a audio stream from using mplayer for the given amount of time
	 * @param wavOutputFile The WAV file to create from the audio stream
	 * @param stream The stream to capture
	 * @param time The time in milliseconds to capture the stream
	 * @throws MPlayerException Thrown if their is a problem with mplayer
	 */
	public IAudioFile captureLiveAudioStream(StreamReference stream,long time) throws  MPlayerException
	{
		File wavOutputFile;
		try {
			wavOutputFile = File.createTempFile("captured", ".wav");
		} catch (IOException e) {
			throw new MPlayerException("Unable to create temp file",e);
		}

		log.info("Capturing audio from stream: " + stream.getUrl() + " to " + wavOutputFile.getAbsolutePath());
		List<String> args = new ArrayList<String>();
		args.add(getConfig().getMPlayerPath());
		args.add("-nojoystick");
		args.add("-nolirc");
		args.add("-cache");
		args.add(String.valueOf(CACHE_SIZE));
		args.add("-vc");
		args.add("null");
		args.add("-vo");
		args.add("null");
		args.add("-ao");
		args.add("pcm:waveheader:fast:file=\""+wavOutputFile.getAbsolutePath()+"\"");
		if (stream.isPlaylist()) {
			args.add("-playlist");
		}
		args.add(stream.getUrl());
		executeWithTimeout(args,time);
		if (wavOutputFile.length()==0) {
			throw new MPlayerException("Unable to caputre audio, the caputred audio file is empty. Try increasing the caputre time.");
		}
		WavFile result = new WavFile(wavOutputFile);
		return result;
	}

	/**
	 * Execute the command with a list of arguments. The this argument should be the application.
	 * @param args The arguments. The first is the application been executed.
	 * @param timeout The timeout in milliseconds before the process is killed,
	 *                or -1 for no timeout
	 * @return The exit code of the application that is executed.
	 * @throws MPlayerException Thrown if their is a problem with mplayer
	 */
	private void executeWithTimeout(final List<String> args,long timeout) throws MPlayerException {
		FutureTask<Integer>task = new FutureTask<Integer>(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return execute(args);
			}
		});

		ExecutorService es = Executors.newSingleThreadExecutor ();
		es.submit (task);
		try {
			int value = task.get(timeout, TimeUnit.MILLISECONDS);
			log.error("Unable to execute mplayer command: " + getErrorStream());
			throw new MPlayerException(MessageFormat.format("Unexpected exit with exit code {0}",value));
		}
		catch (TimeoutException e) {
			kill();
			log.debug(getOutputStream());
			log.debug(getErrorStream());
		} catch (InterruptedException e) {
			throw new MPlayerException("Execution interrupted",e);
		} catch (ExecutionException e) {
			throw new MPlayerException(e.getMessage(),e);
		}
	}

}
