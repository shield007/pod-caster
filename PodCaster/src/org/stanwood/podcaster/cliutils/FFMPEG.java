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
 * This is a utility class that wraps around the ffmpeg applcation inorder to use it to perform various
 * tasks.
 */
public class FFMPEG extends AbstractExecutable {

	private final static Log log = LogFactory.getLog(FFMPEG.class);

	private static final String MP3_BITRATE = "112k";
	private static final String MP4_BITRATE = "112k";
	private static final String OGG_BITRATE = "112k";

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
			args.add("-i");
			args.add(wavFile.getAbsolutePath());
			args.add("-acodec");
			args.add("libfaac");
			args.add("-ac");
			args.add("2");
			args.add("-y");
			args.add("-ab");
			args.add(MP4_BITRATE);
			args.add(targetFile.getAbsolutePath());
			execute(args);

			if (execute(args)!=0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","mp4",targetFile));
			}

			if (!targetFile.exists()) {
				throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","mp4",targetFile));
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","mp4",targetFile),e);
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","mp4",targetFile),e);
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
			args.add("-i");
			args.add(raw.getAbsolutePath());
			args.add("-vn");
			args.add("-f");
			args.add("wav");
			args.add(target.getAbsolutePath());
			if (execute(args)!=0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","wav",target));
			}

			if (!target.exists()) {
				throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","wav",target));
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","wav",target),e);
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","wav",target),e);
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
			args.add("-i");
			args.add(wavFile.getAbsolutePath());
			args.add("-acodec");
			args.add("libmp3lame");
			args.add("-ac");
			args.add("2");
			args.add("-y");
			args.add("-ab");
			args.add(MP3_BITRATE);
			args.add(targetFile.getAbsolutePath());
			execute(args);
			if (!targetFile.exists() || targetFile.length()==0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","mp3",targetFile));
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","mp3",targetFile),e);
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","mp3",targetFile),e);
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
			args.add("-i");
			args.add(wavFile.getAbsolutePath());
			args.add("-acodec");
			args.add("vorbis");
			args.add("-ac");
			args.add("2");
			args.add("-y");
			args.add("-ab");
			args.add(OGG_BITRATE);
			args.add(targetFile.getAbsolutePath());
			execute(args);
			if (!targetFile.exists() || targetFile.length()==0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","ogg",targetFile));
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","ogg",targetFile),e);
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","ogg",targetFile),e);
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
			args.add("-i");
			args.add(wavFile.getAbsolutePath());
			args.add("-acodec");
			args.add("flac");
			args.add("-ac");
			args.add("2");
			args.add("-y");
			args.add(targetFile.getAbsolutePath());
			execute(args);
			if (!targetFile.exists() || targetFile.length()==0) {
				log.info(getOutputStream());
				log.error(getErrorStream());

				throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","flac",targetFile));
			}
		} catch (IOException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","flac",targetFile),e);
		} catch (InterruptedException e) {
			throw new FFMPEGException(MessageFormat.format("Unable to create {0} file: {1}","flac",targetFile),e);
		}
	}
}
