package org.stanwood.podcaster.capture;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.audio.WavFile;
import org.stanwood.podcaster.cliutils.FFMPEG;
import org.stanwood.podcaster.cliutils.FFMPEGException;
import org.stanwood.podcaster.cliutils.GetIPlayer;
import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.config.IPlayerPodcast;
import org.stanwood.podcaster.util.AbstractExecutable;
import org.stanwood.podcaster.util.FileHelper;
import org.stanwood.podcaster.util.IStreamGobbler;
import org.stanwood.podcaster.util.Piper;
import org.stanwood.podcaster.util.StreamGobbler;

/**
 * This class is used to capture BBC iplayer content using a 3rd part application get_iplayer.
 */
public class IPlayerCapture implements ICaptureStream {

	private final static Log log = LogFactory.getLog(IPlayerCapture.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAudioFile captureLiveAudioStream(final ConfigReader config,AbstractPodcast pc) throws CaptureException {
		final File wavOutputFile;
		try {
			wavOutputFile = FileHelper.createTempFile("captured", ".wav");
			wavOutputFile.delete();
		} catch (IOException e) {
			throw new CaptureException("Unable to create temp file",e);
		}
		File rawFile = capture(config, pc, wavOutputFile);
		if (log.isDebugEnabled()) {
			log.debug("Captured "+rawFile+" with size "+rawFile.length());
		}
		log.info(MessageFormat.format("Converting ''{0}'' to wav file ''{1}''...",rawFile,wavOutputFile));
		FFMPEG ffmpeg = new FFMPEG(config);
		try {
			ffmpeg.raw2Wav(rawFile,wavOutputFile);
			FileHelper.delete(rawFile);
		} catch (IOException e) {
			throw new CaptureException(MessageFormat.format("Unable to delete raw file ''{0}''", rawFile));
		} catch (FFMPEGException e) {
			throw new CaptureException(MessageFormat.format("Unable to convert file to wav ''{0}''", rawFile));
		}
		log.info("Convestion complete");
		WavFile result = new WavFile(wavOutputFile);
		return result;
	}

	protected File capture(final ConfigReader config, AbstractPodcast pc,final File wavOutputFile) throws CaptureException {
		final IPlayerPodcast podcast = (IPlayerPodcast)pc;
		GetIPlayer downloader = new GetIPlayer (config);
		final Process getiplayer = downloader.captureLiveAudioStream(podcast.getEpisodeId());

		final StreamGobbler getiplayerErrorGobbler = new StreamGobbler(getiplayer.getErrorStream(),"getiplayer stderr reader");
		FileOutputStream fs = null;
		try {
			File captureFile = FileHelper.createTempFile("capture", ".dat");
			log.info(MessageFormat.format("Capturing stream to file {0}",captureFile.getAbsolutePath()));
			fs = new FileOutputStream(captureFile);

			final Piper piper = new Piper(getiplayer.getInputStream(), new BufferedOutputStream(fs));
			executeWithTimeout(podcast, getiplayer, getiplayerErrorGobbler, piper);
			fs.flush();
			return captureFile;
		} catch (IOException e) {
			throw new CaptureException("Unable to capture radio stream",e);
		}
		finally {
			if (fs!=null) {
				try {
					fs.close();
				} catch (IOException e) {
					throw new CaptureException("Unable to close stream",e);
				}
			}
		}
	}

	protected void executeWithTimeout(final IPlayerPodcast podcast,final Process proc, final StreamGobbler getiplayerErrorGobbler,final IStreamGobbler piper) throws CaptureException {
		FutureTask<Integer>task = new FutureTask<Integer>(new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				try {
					return AbstractExecutable.execute("get-iplayer",proc,getiplayerErrorGobbler,piper);
				} catch (IOException e) {
					throw new CaptureException("Unable to convert stream to wav",e);
				} catch (InterruptedException e) {
					throw new CaptureException("Unable to convert stream to wav",e);
				}
			}
		});

		ExecutorService es = Executors.newSingleThreadExecutor ();
		es.submit (task);
		try {
			task.get(podcast.getCaptureTime(), TimeUnit.MILLISECONDS);
			log.error("Unable to execute get-iplayer command: " + getiplayerErrorGobbler.getResult());
			throw new CaptureException("Unexpected exit");
		}
		catch (TimeoutException e) {
			proc.destroy();
			getiplayerErrorGobbler.done();
			piper.done();
		} catch (InterruptedException e) {
			throw new CaptureException("Execution interrupted",e);
		} catch (ExecutionException e) {
			throw new CaptureException(e.getMessage(),e);
		}
		es.shutdown();
		while (!es.isTerminated()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


//	/**
//	 * Execute the command with a list of arguments. The this argument should be the application.
//	 * @param args The arguments. The first is the application been executed.
//	 * @param timeout The timeout in milliseconds before the process is killed,
//	 *                or -1 for no timeout
//	 * @return The exit code of the application that is executed.
//	 * @throws MPlayerException Thrown if their is a problem with mplayer
//	 */
//	private void executeWithTimeout(final List<String> args,long timeout) throws MPlayerException {
//		FutureTask<Integer>task = new FutureTask<Integer>(new Callable<Integer>() {
//			@Override
//			public Integer call() throws Exception {
//				return execute(args);
//			}
//		});
//
//		ExecutorService es = Executors.newSingleThreadExecutor ();
//		es.submit (task);
//		try {
//			int value = task.get(timeout, TimeUnit.MILLISECONDS);
//			log.error("Unable to execute mplayer command: " + getErrorStream());
//			throw new MPlayerException("Unexpected exit with exit code " + value);
//		}
//		catch (TimeoutException e) {
//			kill();
//			log.debug(getOutputStream());
//			log.debug(getErrorStream());
//		} catch (InterruptedException e) {
//			throw new MPlayerException("Execution interrupted",e);
//		} catch (ExecutionException e) {
//			throw new MPlayerException(e.getMessage(),e);
//		}
//	}
}
