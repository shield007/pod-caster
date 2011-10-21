package org.stanwood.podcaster.capture;

import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.config.ConfigReader;

/**
 * This interface should be implemented by classes used to capture audio streams
 */
public interface ICaptureStream {

	/**
	 * Capture audio from the stream
	 * @param configReader The application configuration
	 * @param podcast The details of the podcast to be captured
	 * @return The audio file that was captured
	 * @throws CaptureException
	 */
	public IAudioFile captureLiveAudioStream(ConfigReader configReader,AbstractPodcast podcast) throws  CaptureException;
}
