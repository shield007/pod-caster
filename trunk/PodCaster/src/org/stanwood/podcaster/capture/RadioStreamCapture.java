package org.stanwood.podcaster.capture;

import java.io.IOException;
import java.text.MessageFormat;

import org.stanwood.podcaster.URLFetcher;
import org.stanwood.podcaster.audio.IAudioFile;
import org.stanwood.podcaster.cliutils.MPlayer;
import org.stanwood.podcaster.config.AbstractPodcast;
import org.stanwood.podcaster.config.ConfigReader;
import org.stanwood.podcaster.config.StreamPodcast;

/**
 * This class is used to capture a radio stream using
 * @author johsta01
 *
 */
public class RadioStreamCapture implements ICaptureStream {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IAudioFile captureLiveAudioStream(ConfigReader config,AbstractPodcast pc) throws CaptureException {
		StreamPodcast podcast = (StreamPodcast)pc;
		try {
			URLFetcher urlFetcher = new URLFetcher(podcast.getStreamURL());

			MPlayer mplayer = new MPlayer(config);
			return mplayer.captureLiveAudioStream(urlFetcher.getMediaUrl(), podcast.getCaptureTime());
		} catch (IOException e) {
			throw new CaptureException(MessageFormat.format("Unable to capture stream ''{0}''",podcast.getStreamURL().toExternalForm()),e);
		}
	}

}
