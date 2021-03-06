package org.stanwood.podcaster.cliutils;


import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.util.AbstractExecutable;

/**
 * This is a utility class that wraps around the ffmpeg application in order to use it to perform various
 * tasks.
 */
public class FFMPEG extends AbstractExecutable {

	private final static Log log = LogFactory.getLog(FFMPEG.class);

	private static final String MP3_BITRATE = "112k"; //$NON-NLS-1$
	private static final String MP4_BITRATE = "112k"; //$NON-NLS-1$
	private static final String OGG_BITRATE = "112k"; //$NON-NLS-1$

	/**
	 * The constructor
	 * @param config the application configuration
	 */
	public FFMPEG(ConfigReader config) {
		super(config);
	}

	/**
	 * Convert a WAV file to a MP4 file
	 * @param wavFile The WAV file to convert
	 * @param targetFile The location the MP4 will be saved
	 * @throws FFMPEGException Thrown if their is a problem performing the conversion
	 */
	public void wav2mp4(File wavFile,File targetFile) throws FFMPEGException
	{
		try {

			List<String> args = new ArrayList<String>();
			args.add(getConfig().getFFMpegPath());
			args.add("-i"); //$NON-NLS-1$
			args.add(wavFile.getAbsolutePath());
			args.add("-acodec"); //$NON-NLS-1$
			args.add("libfaac"); //$NON-NLS-1$
			args.add("-ac"); //$NON-NLS-1$
			args.add("2"); //$NON-NLS-1$
			args.add("-y"); //$NON-NLS-1$
			args.add("-ab"); //$NON-NLS-1$
			args.add(MP4_BITRATE);
			args.add(targetFile.getAbsolutePath());
			execute(args);

			if (execute(args)!=0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"mp4",targetFile)); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (!targetFile.exists()) {
				throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"mp4",targetFile)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"mp4",targetFile),e); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"mp4",targetFile),e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Used to convert a file format to a wav using FFMPEG's auto detection
	 * @param raw The file to convert
	 * @param target The converted file
	 * @throws FFMPEGException Thrown if their is a problem
	 */
	public void raw2Wav(File raw,File target) throws FFMPEGException {
		try {
			List<String> args = new ArrayList<String>();
			args.add(getConfig().getFFMpegPath());
			args.add("-i"); //$NON-NLS-1$
			args.add(raw.getAbsolutePath());
			args.add("-vn"); //$NON-NLS-1$
			args.add("-f"); //$NON-NLS-1$
			args.add("wav"); //$NON-NLS-1$
			args.add(target.getAbsolutePath());
			if (execute(args)!=0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"wav",target)); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (!target.exists()) {
				throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"wav",target)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"wav",target),e); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"wav",target),e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Convert a WAV file to a MP43 file
	 * @param wavFile The WAV file to convert
	 * @param targetFile The location the MP3 will be saved
	 * @throws FFMPEGException Thrown if their is a problem performing the conversion
	 */
	public void wav2mp3(File wavFile,File targetFile) throws FFMPEGException
	{
		try {
			List<String> args = new ArrayList<String>();
			args.add(getConfig().getFFMpegPath());
			args.add("-i"); //$NON-NLS-1$
			args.add(wavFile.getAbsolutePath());
			args.add("-acodec"); //$NON-NLS-1$
			args.add("libmp3lame"); //$NON-NLS-1$
			args.add("-ac"); //$NON-NLS-1$
			args.add("2"); //$NON-NLS-1$
			args.add("-y"); //$NON-NLS-1$
			args.add("-ab"); //$NON-NLS-1$
			args.add(MP3_BITRATE);
			args.add(targetFile.getAbsolutePath());
			execute(args);
			if (!targetFile.exists() || targetFile.length()==0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"mp3",targetFile)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"mp3",targetFile),e); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"mp3",targetFile),e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Used to convert a wav audio file to a ogg audio file
	 * @param wavFile The wav file
	 * @param targetFile The ogg file to create
	 * @throws FFMPEGException Thrown if their is a problem
	 */
	public void wav2ogg(File wavFile,File targetFile) throws FFMPEGException {
		try {
			List<String> args = new ArrayList<String>();
			args.add(getConfig().getFFMpegPath());
			args.add("-i"); //$NON-NLS-1$
			args.add(wavFile.getAbsolutePath());
			args.add("-acodec"); //$NON-NLS-1$
			args.add("vorbis"); //$NON-NLS-1$
			args.add("-ac"); //$NON-NLS-1$
			args.add("2"); //$NON-NLS-1$
			args.add("-y"); //$NON-NLS-1$
			args.add("-ab"); //$NON-NLS-1$
			args.add(OGG_BITRATE);
			args.add(targetFile.getAbsolutePath());
			execute(args);
			if (!targetFile.exists() || targetFile.length()==0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"ogg",targetFile)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"ogg",targetFile),e); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"ogg",targetFile),e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Used to convert a wav audio file to a flac audio file
	 * @param wavFile The wav file
	 * @param targetFile The flac file to create
	 * @throws FFMPEGException Thrown if their is a problem
	 */
	public void wav2flac(File wavFile,File targetFile) throws FFMPEGException {
		try {
			List<String> args = new ArrayList<String>();
			args.add(getConfig().getFFMpegPath());
			args.add("-i"); //$NON-NLS-1$
			args.add(wavFile.getAbsolutePath());
			args.add("-acodec"); //$NON-NLS-1$
			args.add("flac"); //$NON-NLS-1$
			args.add("-ac"); //$NON-NLS-1$
			args.add("2"); //$NON-NLS-1$
			args.add("-y"); //$NON-NLS-1$
			args.add(targetFile.getAbsolutePath());
			execute(args);
			if (!targetFile.exists() || targetFile.length()==0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"flac",targetFile)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"flac",targetFile),e); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format(Messages.getString("FFMPEG.UnableToCreateFile"),"flac",targetFile),e); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
}
