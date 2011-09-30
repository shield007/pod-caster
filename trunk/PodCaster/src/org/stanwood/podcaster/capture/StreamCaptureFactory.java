package org.stanwood.podcaster.capture;

import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.config.IPlayerPodcast;
import org.stanwood.podcaster.config.StreamPodcast;

public class StreamCaptureFactory {

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
