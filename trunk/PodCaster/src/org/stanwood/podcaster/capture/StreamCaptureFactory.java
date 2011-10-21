package org.stanwood.podcaster.capture;

import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.config.IPlayerPodcast;
import org.stanwood.podcaster.config.StreamPodcast;

/**
 * This factory is used to ge the stream capture class depending on the podcast type
 */
public class StreamCaptureFactory {

	/**
	 * Get the stream capture class depending on the podcast type
	 * @param podcast The podcast
	 * @return The stream capture class, or null if the podcast is not supported
	 */
	public static ICaptureStream getStreamCapture(AbstractPodcast podcast) {
		if (podcast instanceof StreamPodcast) {
			return new RadioStreamCapture();
		}
		else if (podcast instanceof IPlayerPodcast) {
			return new IPlayerCapture();
		}
		return null;
	}

}
